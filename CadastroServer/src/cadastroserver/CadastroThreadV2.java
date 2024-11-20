package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Produto;
import model.Usuario;


public class CadastroThreadV2 implements Runnable {

    private final ProdutoJpaController ctrlProd;
    private final UsuarioJpaController ctrlUsu;
    private final MovimentoJpaController ctrlMov;
    private final PessoaJpaController ctrlPessoa;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public CadastroThreadV2(ProdutoJpaController ctrlProd, UsuarioJpaController ctrlUsu,
                            MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa,
                            Socket socket) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                out.writeObject("Credenciais inválidas. Conexão encerrada.");
                return;
            }

            out.writeObject("Usuário conectado com sucesso.");

            String comando;
            while ((comando = (String) in.readObject()) != null) {
                if (comando.equalsIgnoreCase("X")) {
                    out.writeObject("Conexão encerrada pelo cliente.");
                    System.out.println("Cliente solicitou encerramento da conexão.");
                    break;
                }

                switch (comando.toUpperCase()) {
                    case "L":
                        List<Produto> produtos = ctrlProd.findProdutoEntities();
                        out.writeObject(produtos);
                        break;
                    case "E":
                    case "S":
                        Integer idPessoa = (Integer) in.readObject();
                        Integer idProduto = (Integer) in.readObject();
                        Integer quantidade = (Integer) in.readObject();
                        BigDecimal valorUnitario = (BigDecimal) in.readObject();

                        Movimento movimento = new Movimento();
                        movimento.setIdUsuario(usuario);
                        movimento.setTipo(comando.toUpperCase().charAt(0));
                        movimento.setIdPessoa(ctrlPessoa.findPessoa(idPessoa));
                        movimento.setIdProduto(ctrlProd.findProduto(idProduto));
                        movimento.setQuantidade(quantidade);
                        movimento.setValorUnitario(valorUnitario);

                        ctrlMov.create(movimento);

                        Produto produto = ctrlProd.findProduto(idProduto);
                        if (comando.equalsIgnoreCase("E")) {
                            produto.setQuantidade(produto.getQuantidade() + quantidade);
                        } else {
                            produto.setQuantidade(produto.getQuantidade() - quantidade);
                        }
                        ctrlProd.edit(produto);

                        out.writeObject("Movimento registrado com sucesso.");
                        break;
                    default:
                        out.writeObject("Comando desconhecido.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

package cadastroserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



public class CadastroServer {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("lojaPU");

        ProdutoJpaController ctrlProd = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);

        try (ServerSocket serverSocket = new ServerSocket(4321)) {
            while (true) {
                System.out.println("Aguardando conexão...");
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado.");

                CadastroThreadV2 thread = new CadastroThreadV2(ctrlProd, ctrlUsu, ctrlMov, ctrlPessoa, socket);
                new Thread(thread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

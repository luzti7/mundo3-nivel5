package cadastroclientv2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.Produto;



public class CadastroClientV2 {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4321);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Digite o login: ");
            String login = reader.readLine();
            System.out.print("Digite a senha: ");
            String senha = reader.readLine();

            out.writeObject(login);
            out.writeObject(senha);

            JFrame frame = new JFrame("Mensagens do Servidor");
            JTextArea textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            frame.add(textArea);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SwingUtilities.invokeLater(() -> frame.setVisible(true));

            new Thread(() -> {
                try {
                    while (true) {
                        Object response = in.readObject();
                        if (response instanceof String) {
                            String message = (String) response;
                            if ("Conexão encerrada pelo cliente.".equals(message)) {
                                break; 
                            }
                            SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
                        } else if (response instanceof List<?>) {
                            List<Produto> produtos = (List<Produto>) response;
                            SwingUtilities.invokeLater(() -> {
                                textArea.append("Produtos:\n");
                                for (Produto produto : produtos) {
                                    textArea.append(produto.getNome() + " - Quantidade: " 
                                            + produto.getQuantidade() + "\n");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (true) {
                System.out.println("Menu:");
                System.out.println("L - Listar");
                System.out.println("E - Entrada");
                System.out.println("S - Saída");
                System.out.println("X - Finalizar");
                String command = reader.readLine();

                out.writeObject(command);

                if (command.equalsIgnoreCase("X")) {
                   System.out.println("Finalizando..."); 
                    break;
                }

                if (command.equalsIgnoreCase("L")) {
                    continue;
                }

                if (command.equalsIgnoreCase("E") || command.equalsIgnoreCase("S")) {
                    System.out.print("Id da pessoa: ");
                    int idPessoa = Integer.parseInt(reader.readLine());
                    out.writeObject(idPessoa);

                    System.out.print("Id do produto: ");
                    int idProduto = Integer.parseInt(reader.readLine());
                    out.writeObject(idProduto);

                    System.out.print("Quantidade: ");
                    int quantidade = Integer.parseInt(reader.readLine());
                    out.writeObject(quantidade);

                    System.out.print("Valor unitário: ");
                    BigDecimal valorUnitario = new BigDecimal(reader.readLine());
                    out.writeObject(valorUnitario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

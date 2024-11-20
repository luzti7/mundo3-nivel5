package cadastroclientv2;

import java.io.ObjectInputStream;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.Produto;

public class ThreadClient implements Runnable {

    private ObjectInputStream entrada;
    private JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = entrada.readObject();
                if (obj instanceof String) {
                    String mensagem = (String) obj;
                    SwingUtilities.invokeLater(() -> {
                        textArea.append(mensagem + "\n");
                    });
                } else if (obj instanceof List) {
                    List<Produto> produtos = (List<Produto>) obj;
                    SwingUtilities.invokeLater(() -> {
                        for (Produto p : produtos) {
                            textArea.append(p.getNome() + " - " + p.getQuantidade() + "\n");
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

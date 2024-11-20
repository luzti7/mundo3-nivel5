/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroclientv2;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class SaidaFrame extends JDialog {
    public JTextArea texto;

    public SaidaFrame() {
        setBounds(100, 100, 400, 300);
        setModal(false);
        
        texto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(texto);
        scrollPane.setBounds(10, 10, 380, 250);
        add(scrollPane);
    }

    public JTextArea getTextArea() {
        return texto;
    }
}


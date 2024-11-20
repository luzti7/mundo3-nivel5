/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.Produto;



public class CadastroClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("op1");
            out.writeObject("op1");

            System.out.println((String) in.readObject());

            out.writeObject("L");

            List<Produto> produtos = (List<Produto>) in.readObject();
            for (Produto p : produtos) {
                System.out.println(p.getNome());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Jaime
 */
public class CristoTubeMultiServerThread extends Thread{
    private Socket socket = null;
    JTextArea textAreaDebug;
    PrintWriter salida;

    public CristoTubeMultiServerThread(Socket socket, JTextArea textAreaDebug) {
        super("CristoTubeMultiServerThread");
        this.socket = socket;
        this.textAreaDebug = textAreaDebug;
    }
    
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            this.salida = out;
            String inputLine, outputLine;
            ProtocoloCristoTube pct = new ProtocoloCristoTube();
            while ((inputLine = in.readLine()) != null) {
                outputLine = pct.processInput(inputLine, out, in);
                System.out.println(outputLine);
                textAreaDebug.append(outputLine + "\n");
                out.println(outputLine);
                textAreaDebug.append("Mensaje enviado\n");
            }
            
        } catch (IOException e) {
        } catch (SQLException ex) {
            Logger.getLogger(CristoTubeMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
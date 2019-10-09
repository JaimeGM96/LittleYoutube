/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;
import Controlador.CristoTubeMultiServerThread;
import Modelo.conexionBD;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author Jaime
 */
public class CristoTube {
    public static ArrayList<CristoTubeMultiServerThread> vectorHebrasClientes = new ArrayList<CristoTubeMultiServerThread>();
    private Connection con;
    int portNumber;
    boolean listening = true;
    JTextArea textAreaDebug;
    
    public CristoTube(int portNumber, JTextArea textAreaDebug ){
        this.portNumber = portNumber;
        this.textAreaDebug = textAreaDebug;
    }

    public CristoTube(Connection con, int portNumber) {
        this.con = con;
        this.portNumber = portNumber;
    }
    
    public synchronized void conectar(JTextArea debug) throws SQLException{
        int i = 0;
        textAreaDebug = debug;
        conexionBD conexion = new conexionBD();
        con = conexion.conectar(textAreaDebug);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
                    vectorHebrasClientes.add(new CristoTubeMultiServerThread(serverSocket.accept(), textAreaDebug));
                    vectorHebrasClientes.get(i).start();
                    i++;
                }
            } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
    }
    
}

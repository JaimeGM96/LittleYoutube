/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.CristoTubeClientModel;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author Jaime
 */
public class CristoTubeClientController {
    CristoTubeClientModel cristoTubeModel;
    String hostName;
    int portNumber;
    
    public CristoTubeClientController(){
        
    }

    public CristoTubeClientController(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }
    
    public void conectar(JTextArea textAreaDebug, String cadenaLogin) throws IOException, SQLException{
        cristoTubeModel = new CristoTubeClientModel(this.hostName, this.portNumber);
        cristoTubeModel.conectarServer(textAreaDebug, cadenaLogin);
    }
    
    public void registrar(String cadenaRegistro) throws IOException{
        cristoTubeModel.registrar(cadenaRegistro);
    }
    
    public String mensajeServer(){
        return cristoTubeModel.getMensajeDeServer();
    }
    
    public void mostrarVideos(JTextArea debug, String cadena, JTable listaVideos){
        cristoTubeModel.establecerTabla(debug, cadena, listaVideos);
    }
    
    public void descargarVideo(String cadena, JTextArea textAreaDebug, String nombreVideo){
        cristoTubeModel.buscarVideo(cadena, textAreaDebug, nombreVideo);
    }
    
    public void addMetadatos(JFileChooser elegirVideo, JTextArea textAreaDebug){
        cristoTubeModel.addMetadatos(elegirVideo, textAreaDebug);
    }

    public void subirVideo(JTextArea textAreaDebug) {
        cristoTubeModel.subirVideo(textAreaDebug);
    }
    
    public void borrarVideo(String idVideo, JTextArea textAreaDebug){
        cristoTubeModel.borrarVideo(idVideo, textAreaDebug);
    }
    
    public void actualizarTabla(JTextArea textAreaDebug){
        cristoTubeModel.actualizarTabla(textAreaDebug);
    }
}

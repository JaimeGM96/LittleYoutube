/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.UsuarioModel;
import Modelo.videoModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 *
 * @author Jaime
 */
public class ProtocoloCristoTube {
    String idVideo = null;
    int totalSize;
    int sizePackage;
    String userLogged = null;
    videoModel videoModel = null;
    
    public String processInput(String theInput, PrintWriter out, BufferedReader in) throws SQLException {
        String theOutput = null;
        
        if(theInput != null){
            String[] strings = theInput.split("#");
            if (strings[1].equals("REGISTER")) {
                usuarioController controller = new usuarioController();
                controller.addUser(strings[2], strings[3], strings[4], strings[5], strings[6], strings[7]);
                theOutput = "PROTOCOLCRISTOTUBE1.0#OK#REGISTERED";
            } else if(strings[1].equals("LOGIN")) {
                usuarioController controller = new usuarioController();
                String cadena = null;
                cadena = controller.verificarLogin(strings);

                if(cadena != null){
                    theOutput = "PROTOCOLCRISTOTUBE1.0#OK#USER_LOGGED#" + cadena;
                    userLogged = strings[2];
                } else{
                    theOutput = "PROTOCOLCRISTOTUBE1.0#ERROR#BAD_LOGIN";
                }
            } else if(strings[2].equals("GET_ALL")){
                usuarioController controller = new usuarioController();
                String cadena = controller.obtenerVideos();              
                theOutput = "PROTOCOLCRISTOTUBE1.0#GET_ALL_RESPONSE#" + strings[1] + "#" + cadena;
            } else if (strings[2].equals("GETVIDEO")){
                videoController controller = new videoController();
                String ruta = controller.buscarVideo(strings[3]);
                File file = new File(ruta);
                if(ruta != null){
                    theOutput = "PROTOCOLCRISTOTUBE1.0#OK#VIDEO_FOUND#" + strings[3] + "#" + file.length() + "#1024";
                    idVideo = strings[3];
                }
            } else if(strings.length > 4 && strings[3].equals("PREPARED_TO_RECEIVE")){
                usuarioController controller = new usuarioController();
                try {
                    controller.enviarBytes(idVideo, out);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else if(strings[1].equals("VIDEO_UP")){
                usuarioController controller = new usuarioController();
                String ruta = "users/" + controller.buscarUsuario(strings[5]) + "/videos/" + strings[6] + ".mp4";
                totalSize = Integer.parseInt(strings[2]);
                sizePackage = Integer.parseInt(strings[3]);
                videoModel = new videoModel(controller.buscarUsuario(strings[5]), ruta, strings[6], strings[7]);
                theOutput = "PROTOCOLCRISTOTUBE1.0#OK#VIDEO_UP#PREPARED_TO_RECEIVE#" + strings[3];
            } else if(strings[1].equals("OK")){
                usuarioController userController = new usuarioController();
                try {
                    userController.recibirBytes(in, theInput, totalSize, sizePackage, videoModel);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else if(strings[1].equals("DELETE_VIDEO")){
                videoController controller = new videoController();
                controller.borrarVideo(strings[2]);
            } else if(strings[2].equals("DELETE")){
                UsuarioModel.dejarEnviar = true;
            }
        }
        return theOutput;
    }
}

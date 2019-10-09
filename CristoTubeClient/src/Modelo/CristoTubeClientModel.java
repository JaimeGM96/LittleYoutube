/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Vista.Login;
import Vista.Metadatos;
import Vista.ReproducirVideo;
import Vista.Videos;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Base64;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jaime
 */
public class CristoTubeClientModel {
    String hostName;
    int portNumber;
    Socket cristoTubeClientSocket;
    String mensajeDeServer;
    String mensajeParaServer;
    String conectado;
    String login = null;
    String idVideo = null;
    int totalSize;
    int sizePackage;
    PrintWriter out;
    BufferedReader in;
    String[] cadenaDeServer = null;
    Metadatos metadatos;
    String nombreVideo = null;
    Videos videos;
    String userName = null;
    boolean frameAbierto = false;
    boolean reproduciendo = false;

    public CristoTubeClientModel(String hostName, int portNumber) throws IOException {
        this.hostName = hostName;
        this.portNumber = portNumber;
        cristoTubeClientSocket = new Socket(this.hostName, this.portNumber);
        out = new PrintWriter(cristoTubeClientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(cristoTubeClientSocket.getInputStream()));
    }

    public String getMensajeDeServer() {
        return mensajeDeServer;
    }

    public void setMensajeDeServer(String mensajeDeServer) {
        this.mensajeDeServer = mensajeDeServer;
    }

    public String getMensajeParaServer() {
        return mensajeParaServer;
    }

    public void setMensajeParaServer(String mensajeParaServer) {
        this.mensajeParaServer = mensajeParaServer;
    }

    public String getConectado() {
        return conectado;
    }

    public void setConectado(String conectado) {
        this.conectado = conectado;
    }
    
    public void conectarServer(JTextArea textAreaDebug, String cadenaLogin) throws SQLException{
        if(cadenaLogin != null){
            textAreaDebug.append(cadenaLogin + "\n");
            out.println(cadenaLogin);
        } else{
            textAreaDebug.append("Error al conectar.\n");
        }
        
        try {
            while ((mensajeDeServer = in.readLine()) != null) {
                System.out.println(mensajeDeServer);
                String[] strings = cadenaLogin.split("#");
                String[] cadena = mensajeDeServer.split("#");
                if(cadena[2].equals("USER_LOGGED")) {
                    userName = strings[2];
                    textAreaDebug.append("Conectado.\n");
                    textAreaDebug.append("Mensaje del servidor: " + mensajeDeServer + "\n");
                    textAreaDebug.append("Cadena: PROTOCOLCRISTOTUBE1.0#" + strings[2] + "#GET_ALL\n");
                    out.println("PROTOCOLCRISTOTUBE1.0#" + strings[2] + "#GET_ALL");
                    textAreaDebug.append("Mensaje enviado\n");
                } else if(cadena[1].equals("GET_ALL_RESPONSE")){
                    if (frameAbierto == false){
                        iniciarTablaVideos();
                    } else{
                        videos.mostrar(mensajeDeServer);
                    }
                } else if(cadena[2].equals("VIDEO_FOUND")) {
                    totalSize = Integer.parseInt(cadena[4]);
                    sizePackage = Integer.parseInt(cadena[5]);
                    idVideo = cadena[3];
                    textAreaDebug.append("Cadena: PROTOCOLCRISTOTUBE1.0#OK#" + login + "#PREPARED_TO_RECEIVE#" + sizePackage + "\n");
                    out.println("PROTOCOLCRISTOTUBE1.0#OK#" + login + "#PREPARED_TO_RECEIVE#" + sizePackage);
                    textAreaDebug.append("Mensaje enviado\n");
                } else if(cadena[1].equals("OK") && cadena[2].equals(idVideo)){
                    recibirBytes();
                    //ReproducirVideo reproductor = new ReproducirVideo(nombreVideo);
                    //reproductor.setVisible(true);
                } else if(cadena[2].equals("VIDEO_UP")){
                    enviarBytes(textAreaDebug);
                    out.println("PROTOCOLCRISTOTUBE1.0#" + login + "#GET_ALL");
                } else if(cadena[1].equals("BROADCAST")){
                    Login.terminarRecibir = true;
                    out.println("PROTOCOLCRISTOTUBE1.0#" + login + "#DELETE#" + idVideo + "#BROADCASTING#OK");
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void iniciarTablaVideos(){
        videos = new Videos(mensajeDeServer);
        videos.setVisible(true);
        frameAbierto = true;
    }
    
    public void recibirBytes(){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(nombreVideo + ".mp4");
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        boolean terminado = false;
        byte[] decodedBytes;
        byte[] bytes;
        int cantidadPaquetes = 0;
        
        cadenaDeServer = mensajeDeServer.split("#");
        
        while(((totalSize - output.size()) >= 0) && (terminado == false) && (Login.terminarRecibir == false)){
            bytes = Base64.getDecoder().decode(cadenaDeServer[3].getBytes());
            try {
                out.write(bytes);
                cantidadPaquetes++;
                if((cantidadPaquetes >= ((totalSize/1024) * 0.50)) && reproduciendo == false){
                    reproduciendo = true;
                    ReproducirVideo reproductor = new ReproducirVideo(nombreVideo);
                    reproductor.setVisible(true);
                }
                System.out.println(bytes);
            } catch (IOException ex) {
                System.out.println(ex);
            }
            if((totalSize - sizePackage) <= 0){
                terminado = true;
            } else{
                try {
                    mensajeDeServer = in.readLine();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                cadenaDeServer = mensajeDeServer.split("#");           
            }
            
        }

        try {
            //FileOutputStream out = new FileOutputStream(nombreVideo + ".mp4");
            //decodedBytes = output.toByteArray();
            //out.write(decodedBytes);
            out.close();
            output.close();
        } catch (IOException e) {
        }
        
    }
    
    public void enviarBytes(JTextArea textAreaDebug){
        File tempFile = new File(Login.fileSubirVideo.getAbsolutePath());
        String encodedString = null;
        InputStream inputStream = null;
        byte[] buffer = new byte[1024];
        int bytesRead;
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            inputStream = new FileInputStream(tempFile);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer);
                encodedString = Base64.getEncoder().encodeToString(buffer);
                textAreaDebug.append("PROTOCOLCRISTOTUBE1.0#OK#" + encodedString + "\n");
                out.println("PROTOCOLCRISTOTUBE1.0#OK#" + encodedString);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void registrar(String cadenaRegistro) throws IOException {
        System.out.println("Cadena: " + cadenaRegistro);
        out.println(cadenaRegistro);
    }
    
    public void establecerTabla(JTextArea debug, String cadena, JTable listaVideos){
        DefaultTableModel modelo = (DefaultTableModel) listaVideos.getModel();
        String[] strings = cadena.split("#");
        String [] datosUsuario = new String[strings.length];
        
        for(int i = 0; i < strings.length; i++){
            datosUsuario = strings[i].split("@");
            for(int j = 1; j < datosUsuario.length; j++){
                String[] datosVideo = datosUsuario[j].split("&");
                String[] conjuntoDatos = {datosUsuario[0], datosVideo[0], datosVideo[1], datosVideo[2]};
                modelo.addRow(conjuntoDatos);
            }
        }
    }
    
    public void buscarVideo(String cadena, JTextArea textAreaDebug, String nombreVideo){
        textAreaDebug.append(cadena + "\n");
        String[] login = cadena.split("#");
        this.nombreVideo = nombreVideo;
        this.login = login[1];
        this.idVideo = login[3];
        out.println(cadena);
    }
    
    public void addMetadatos(JFileChooser elegirVideo, JTextArea textAreaDebug){
        Login.fileSubirVideo = elegirVideo.getSelectedFile();  
        metadatos = new Metadatos(textAreaDebug);
        metadatos.setVisible(true);
    }
    
    public void subirVideo(JTextArea textAreaDebug){
        String cadena;
        //textAreaDebug.append("PROTOCOLCRISTOTUBE1.0#VIDEO_UP#" + fileSubirVideo.length() + "#1024#METADATOS_VIDEO#" + Login.loginUsuario + "#" + metadatos.getTituloVideo() + "#" + metadatos.getDescripcion() + "\n");
        cadena = "PROTOCOLCRISTOTUBE1.0#VIDEO_UP#" + Login.fileSubirVideo.length() + "#1024#METADATOS_VIDEO#" + Login.loginUsuario + "#" + metadatos.getTituloVideo() + "#" + metadatos.getDescripcion();
        try {
            Login.controller.conectar(textAreaDebug, cadena);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        //out.println("PROTOCOLCRISTOTUBE1.0#VIDEO_UP#" + fileSubirVideo.length() + "#1024#METADATOS_VIDEO#" + Login.loginUsuario + "#" + metadatos.getTituloVideo() + "#" + metadatos.getDescripcion());
    }
    
    public void borrarVideo(String idVideo, JTextArea textAreaDebug){
        String cadena;
        //textAreaDebug.append("PROTOCOLCRISTOTUBE1.0#DELETE_VIDEO#" + idVideo + "\n");
        cadena = "PROTOCOLCRISTOTUBE1.0#DELETE_VIDEO#" + idVideo;
        try {
            Login.controller.conectar(textAreaDebug, cadena);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        //out.println("PROTOCOLCRISTOTUBE1.0#DELETE_VIDEO#" + idVideo);
    }
    
    public void actualizarTabla(JTextArea textAreaDebug){
        String cadena;
        //textAreaDebug.append("PROTOCOLCRISTOTUBE1.0#" + userName + "#GET_ALL\n");
        cadena = "PROTOCOLCRISTOTUBE1.0#" + userName + "#GET_ALL";
        try {
            Login.controller.conectar(textAreaDebug, cadena);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        //out.println("PROTOCOLCRISTOTUBE1.0#" + userName + "#GET_ALL");
    }
}

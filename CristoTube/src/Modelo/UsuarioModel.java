/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Controlador.videoController;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Vista.Menu;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 *
 * @author Jaime
 */
public class UsuarioModel {
    private String DNI;
    private String Nombre;
    private String Apellido1;
    private String Apellido2;
    private String Login;
    private String Password;
    public static boolean dejarEnviar = false;

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getApellido1() {
        return Apellido1;
    }

    public void setApellido1(String Apellido1) {
        this.Apellido1 = Apellido1;
    }

    public String getApellido2() {
        return Apellido2;
    }

    public void setApellido2(String Apellido2) {
        this.Apellido2 = Apellido2;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public UsuarioModel(){
        this.DNI = "0";
        this.Nombre = "0";
        this.Apellido1 = "0";
        this.Apellido2 = "0";
        this.Login = "0";
        this.Password = "0";
    }
    
    public UsuarioModel(String DNI, String Nombre, String Apellido1, String Apellido2, String Login, String Password) {
        this.DNI = DNI;
        this.Nombre = Nombre;
        this.Apellido1 = Apellido1;
        this.Apellido2 = Apellido2;
        this.Login = Login;
        this.Password = Password;
    }
    
    public ArrayList<UsuarioModel> getUsuarios(Connection con) throws SQLException{
        Statement stmt = null;
        String query = "SELECT * FROM usuarios";
        ArrayList<UsuarioModel> vectorUsuarios = new ArrayList();
        
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String DNI = rs.getString("DNI");
                String Nombre = rs.getString("NAME");
                String Apellido1 = rs.getString("LASTNAME1");
                String Apellido2 = rs.getString("LASTNAME2");
                String Login = rs.getString("LOGIN");
                String Password = rs.getString("PASSWORD");
                vectorUsuarios.add(new UsuarioModel(DNI, Nombre, Apellido1, Apellido2, Login, Password));
            }
        } catch (SQLException eTablaUsuarios ) {
            Logger.getLogger(UsuarioModel.class.getName()).log(Level.SEVERE, null, eTablaUsuarios);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return vectorUsuarios;
    }
    
    public String getAll(Connection con) throws SQLException{
        Statement stmt = null;
        String query = "SELECT login, idvideo, titulo, descripcion FROM usuarios t, videos v WHERE t.ID=v.idusuario";
        String cadena = null;
        
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String login = rs.getString("login");
                String idvideo = rs.getString("idvideo");
                String titulo = rs.getString("titulo");
                String descripcion = rs.getString("descripcion");
                if(cadena != null)
                    cadena += login + "@" + idvideo + "&" + titulo + "&"  + descripcion + "#";
                else
                    cadena = login + "@" + idvideo + "&" + titulo + "&"  + descripcion + "#";
            }
        } catch (SQLException eTablaUsuarios ) {
            Logger.getLogger(UsuarioModel.class.getName()).log(Level.SEVERE, null, eTablaUsuarios);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return cadena;
    }
     
    public String verificar(String[] strings) throws SQLException{
        Connection con = null;
        Statement stmt= null;
        String cadena = null;
        String query = "SELECT * FROM usuarios WHERE LOGIN = '" + strings[2] + "'";
        con = DriverManager.getConnection("jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!");
        stmt = con.createStatement();      
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()){
            if (strings[2].equals(rs.getString("LOGIN")) && strings[3].equals(rs.getString("PASSWORD"))){
                cadena = rs.getString("DNI") + "#" + rs.getString("NAME") + "#" + rs.getString("LASTNAME1") + "#" + rs.getString("LASTNAME2") + "#" + rs.getString("LOGIN");
            }
        }
        con.close();
        return cadena;
    }
     
    public void insertarUsuario(Connection con, UsuarioModel nuevoUsuario) throws SQLException {
        PreparedStatement stmt = null;
        String insercion = "INSERT INTO usuarios (DNI, NAME, LASTNAME1, LASTNAME2, LOGIN, PASSWORD) VALUES (?, ?, ?, ?, ?, ?)";
        
        try{
            stmt = con.prepareStatement(insercion);
            
            stmt.setString(1, nuevoUsuario.getDNI());
            stmt.setString(2, nuevoUsuario.getNombre());
            stmt.setString(3, nuevoUsuario.getApellido1());
            stmt.setString(4, nuevoUsuario.getApellido2());
            stmt.setString(5, nuevoUsuario.getLogin());
            stmt.setString(6, nuevoUsuario.getPassword());
            
            stmt.executeUpdate();
        } catch (SQLException eInsertarTupla){
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, eInsertarTupla);
            
        }
    }
    
    public void eliminarUsuario(Connection con, String DNIUsuarioEliminado, javax.swing.JTextArea textAreaDebug) throws SQLException{
        PreparedStatement stmt = null;
        String borrar = "DELETE FROM usuarios WHERE DNI=?";
        textAreaDebug.append("Eliminando usuario de la tabla de usuarios.\n");
        
        try{
            stmt = con.prepareStatement(borrar);
            
            stmt.setString(1, DNIUsuarioEliminado);
            
            stmt.executeUpdate();
        } catch (SQLException eDeleteTupla){
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, eDeleteTupla);
            textAreaDebug.append(eDeleteTupla.toString() + "\n");
        }
    }
    
    public String buscarUsuario(String userLogged) throws SQLException{
        Connection con = null;
        Statement stmt= null;
        int id = 0;
        String cadena = null;
        String query = "SELECT ID FROM usuarios WHERE LOGIN = '" + userLogged + "'";
        con = DriverManager.getConnection("jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!");//@,2,golfoPSP1234abcd!
        stmt = con.createStatement();      
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()){
            id = rs.getInt("ID");
        }
        con.close();
        return cadena = String.valueOf(id);
    }
    
    public void enviarBytes(String idVideo, PrintWriter out){
        dejarEnviar = false;
        videoModel videModel = new videoModel();
        File tempFile = null;
        try {
            tempFile = new File(videModel.buscarVideo(idVideo));
        } catch (SQLException ex) {
            System.out.println(ex);
        }
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
            while ((dejarEnviar == false) && ((bytesRead = inputStream.read(buffer)) != -1)) {
                output.write(buffer);
                encodedString = java.util.Base64.getEncoder().encodeToString(buffer);
                System.out.println("PROTOCOLCRISTOTUBE1.0#OK#" + idVideo + "#" + encodedString);
                out.println("PROTOCOLCRISTOTUBE1.0#OK#" + idVideo + "#" + encodedString);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void recibirBytes(BufferedReader in, String bytes, int totalSize, int sizePackage, videoModel videoModel){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        videoController controller = new videoController();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("users/" + videoModel.getIdUsuario() + "/videos/" + videoModel.getTitulo() + ".mp4");
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        boolean terminado = false;
        byte[] decodedBytes;
        byte[] arrayBytes;
        String mensajeDeCliente;
        String[] cadenaDeCliente;
        cadenaDeCliente = bytes.split("#");
        try {
            controller.subirVideo(videoModel);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        while(((totalSize - output.size()) >= 0) && terminado == false){
            try {
                out.write(java.util.Base64.getDecoder().decode(cadenaDeCliente[2].getBytes()));
                System.out.println(java.util.Base64.getDecoder().decode(cadenaDeCliente[2].getBytes()));
            } catch (IOException ex) {
                System.out.println(ex);
            }
            if((totalSize - sizePackage) <= 0){
                terminado = true;
            } else{
                try {
                    mensajeDeCliente = in.readLine();
                    cadenaDeCliente = mensajeDeCliente.split("#");
                } catch (IOException ex) {
                    System.out.println(ex);
                }         
            }
        }
        System.out.println("terminado");
        try {
            //FileOutputStream out = new FileOutputStream("users/" + videoModel.getIdUsuario() + "/videos/" + videoModel.getTitulo() + ".mp4");
            //decodedBytes = output.toByteArray();
            //out.write(decodedBytes);
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        
    }
}

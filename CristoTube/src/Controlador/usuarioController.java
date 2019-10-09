/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.UsuarioModel;
import Modelo.conexionBD;
import Modelo.videoModel;
import Vista.Menu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jaime
 */
public class usuarioController {
    public ArrayList<UsuarioModel> conectar(){
        UsuarioModel userModel = new UsuarioModel();
        ArrayList<UsuarioModel> vectorUsuarios = new ArrayList();

        try {
            vectorUsuarios = userModel.getUsuarios(conexionBD.conn);
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vectorUsuarios;
    }
    
    public String obtenerVideos(){
        UsuarioModel userModel = new UsuarioModel();
        String cadena = null;

        try {
            cadena = userModel.getAll(conexionBD.conn);
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cadena;
    }
    
    public DefaultTableModel establecerTabla(ArrayList<UsuarioModel> vectorUsuarios){
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object []{"DNI", "Nombre", "Apellido1", "Apellido2", "Login", "Password"});
        for (int i = 0; i < vectorUsuarios.size(); i++){
            tableModel.addRow(new Object[]{vectorUsuarios.get(i).getDNI(), vectorUsuarios.get(i).getNombre(), vectorUsuarios.get(i).getApellido1(), vectorUsuarios.get(i).getApellido2(), vectorUsuarios.get(i).getLogin(), vectorUsuarios.get(i).getPassword()});
        }
        return tableModel;
    }
    
    public String verificarLogin(String[] strings) throws SQLException{
        UsuarioModel userModel = new UsuarioModel(); 
        return userModel.verificar(strings);
    }
    
    public ArrayList<UsuarioModel> rellenarTabla(javax.swing.JTextArea textAreaDebug){
        UsuarioModel user = new UsuarioModel();
        ArrayList<UsuarioModel> vectorUsuarios = new ArrayList();
        try {
            textAreaDebug.append("Mostrando la tabla de usuarios.\n");
            vectorUsuarios = user.getUsuarios(conexionBD.conn);
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            textAreaDebug.append(ex.toString() + "\n");
        }
        return vectorUsuarios;
    }
    
    public void addUser(String nuevoDNI, String nuevoNombre, String nuevoApellido1, String nuevoApellido2, String nuevoLogin, String nuevoPassword/*, javax.swing.JTextArea textAreaDebug*/){
        UsuarioModel nuevoUsuario = new UsuarioModel(nuevoDNI, nuevoNombre, nuevoApellido1, nuevoApellido2, nuevoLogin, nuevoPassword);
        try {
            nuevoUsuario.insertarUsuario(conexionBD.conn, nuevoUsuario/*, textAreaDebug*/);
        } catch (SQLException ex) {
            Logger.getLogger(usuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteUser(String DNIUsuarioEliminado, javax.swing.JTextArea textAreaDebug) throws SQLException{
        UsuarioModel user = new UsuarioModel();
        user.eliminarUsuario(conexionBD.conn, DNIUsuarioEliminado, textAreaDebug);
    }
    
    public void enviarBytes(String idVideo, PrintWriter out) throws SQLException, IOException{
        UsuarioModel userModel = new UsuarioModel();
        userModel.enviarBytes(idVideo, out);
    }
    
    public void recibirBytes(BufferedReader in, String bytes, int totalSize, int sizePackage, videoModel videoModel) throws IOException{
        UsuarioModel userModel = new UsuarioModel();
        userModel.recibirBytes(in, bytes, totalSize, sizePackage, videoModel);
    }
    
    public String buscarUsuario(String userLogged) throws SQLException{
        UsuarioModel userModel = new UsuarioModel();
        return userModel.buscarUsuario(userLogged);
    }
}

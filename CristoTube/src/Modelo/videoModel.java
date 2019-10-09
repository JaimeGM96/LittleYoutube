/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import com.mysql.jdbc.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jaime
 */
public class videoModel {
    String idUsuario;
    String rutaServer;
    String titulo;
    String descripcion;

    public videoModel(String idUsuario, String rutaServer, String titulo, String descripcion) {
        this.idUsuario = idUsuario;
        this.rutaServer = rutaServer;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public videoModel() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRutaServer() {
        return rutaServer;
    }

    public void setRutaServer(String rutaServer) {
        this.rutaServer = rutaServer;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String buscarVideo(String idVideo) throws SQLException{
        Connection con = null;
        Statement stmt= null;
        String ruta = null;
        String query = "SELECT RUTASERVER FROM videos WHERE IDVIDEO = '" + idVideo + "'";
        con = DriverManager.getConnection("jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!");//"jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!"
        stmt = con.createStatement();      
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()){
            ruta = rs.getString("RUTASERVER");
        }
        con.close();
        
        return ruta;
    }
    
    public void subirVideo(videoModel videoModel) throws SQLException{
        Connection con = null;
        PreparedStatement stmt= null;
        int id = 0;
        String query = "INSERT INTO videos (idusuario, rutaserver, titulo, descripcion) VALUES (?, ?, ?, ?)";
        con = DriverManager.getConnection("jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!");

        stmt = (PreparedStatement) con.prepareStatement(query);
        stmt.setString(1, videoModel.getIdUsuario());
        stmt.setString(2, videoModel.getRutaServer());
        stmt.setString(3, videoModel.getTitulo());
        stmt.setString(4, videoModel.getDescripcion());
        stmt.executeUpdate();
        
        con.close();
    }
    
    public void borrarVideo(String idVideo) throws SQLException{
        Connection con = null;
        PreparedStatement stmt= null;
        
        String query = "DELETE FROM videos WHERE IDVIDEO = ?";
        con = DriverManager.getConnection("jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!");
        
        stmt = (PreparedStatement) con.prepareStatement(query);
        stmt.setString(1, idVideo);
        stmt.executeUpdate();
     
        con.close();
    }
}

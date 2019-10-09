/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JTextArea;

/**
 *
 * @author Jaime
 */
public class conexionBD {
    public static Connection conn;
    private String tipoBD = "mysql";
    private String nombreServer = "52.19.19.65";
    private String numeroPuerto = "3306";
    private String nombreBD = "cristotube";
    
    public Connection conectar(JTextArea textAreaDebug) throws SQLException {
        conn = DriverManager.getConnection("jdbc:" + tipoBD + "://" + nombreServer + ":" + numeroPuerto + "/" + nombreBD, "testpsp", "@,2,golfoPSP123abcd!");//"jdbc:mysql://52.19.19.65:3306/cristotube","testpsp","@,2,golfoPSP123abcd!"
        if (conn == null){
            textAreaDebug.append("Error al conectar a la base de datos\n");
        } else{
            textAreaDebug.append("Conectado a la base de datos\n");
        }
        
        return conn;
    }
    
    public void desconectar() throws SQLException{
        conn.close();
    }
}

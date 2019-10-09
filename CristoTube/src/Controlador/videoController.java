/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.videoModel;
import Vista.CristoTube;
import java.sql.SQLException;

/**
 *
 * @author Jaime
 */
public class videoController{
    public String buscarVideo(String idVideo) throws SQLException{
        videoModel videModel = new videoModel();
        return videModel.buscarVideo(idVideo);
    }
    
    public void subirVideo(videoModel videoModel) throws SQLException{
        videoModel.subirVideo(videoModel);
    }
    
    public void borrarVideo(String idVideo) throws SQLException{
        videoModel videModel = new videoModel();
        videModel.borrarVideo(idVideo);
        
        /*File file = new File(this.buscarVideo(idVideo));
        if(file.exists())
            file.delete();*/
        
        for(int i = 0; i < CristoTube.vectorHebrasClientes.size(); i++)
            CristoTube.vectorHebrasClientes.get(i).salida.println("PROTOCOLCRISTOTUBE1.0#BROADCAST#" + idVideo + "#DELETED");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistemas.distribuidos.chat;

/**
 *
 * @author Administrator
 */
public class Usuario {
    private String Usuario;
    private String inetAddress;
    private int port;

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String Usuario) {
        this.Usuario = Usuario;
    }

    
    public String getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(String inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Cliente{" + "Usuario=" + Usuario + ", inetAddress=" + inetAddress + ", port=" + port + '}';
    }

    
    
    
    
}

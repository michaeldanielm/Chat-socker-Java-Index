/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas.distribuidos.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public abstract class ClienteTcp implements Runnable {

    private String str_request = "";

    public ClienteTcp(String cmd) {
        this.str_request = cmd;
    }

    @Override
    public void run() {
        
        String host_server="/127.0.0.1";
        int port=15432;
        
        //en caso de enviar mensaje se cambia el host por el destinatario
        if (str_request.startsWith("EM-")||str_request.startsWith("EA-")) {
            host_server = str_request.split("-")[1];
            port = Integer.valueOf(str_request.split("-")[2]);
        }
        
        host_server = host_server.replace("/", "");
        
        try (Socket socket = new Socket(host_server, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(str_request);

            writer.println("-end-");

            if (str_request.startsWith("IS-")) {
                
                iniciarSession(socket);
                
            }else if (str_request.startsWith("CC-")) {
                
                consultaClientes(socket);
                
            }else if (str_request.startsWith("EM-")) {
                
                enviarMensaje(socket);
                
            
            }else if (str_request.startsWith("EA-")) {
                
                enviarAudio(socket);
                iniciarStreaming();
            }
            
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
        
        
        
    }

    abstract void consultaUsuarios(Map<String,Usuario> respuesta);
    abstract void iniciarStreaming();

    //Inicio de Seccion  de Usuario
    private void iniciarSession(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         String line;
            List<Usuario> usuarios=new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.equals("ok")) {
                    break;
                }
                System.out.println(line);
            }
    }
    //Consulta de cliente
    private void consultaClientes(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         String line;
            Map<String,Usuario> usuarios=new HashMap<>();
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("U-")) {
                    
                   String str_usuario = line.split("-")[1];
                   String str_InetAddress = line.split("-")[2];
                   String str_puerto = line.split("-")[3];

                   Usuario cliente = new Usuario();
                   cliente.setUsuario(str_usuario);
                   cliente.setInetAddress(str_InetAddress);
                   cliente.setPort(Integer.valueOf(str_puerto));
                   
                   usuarios.put(str_usuario,cliente);
                    
                }
                if (line.equals("ok")) {
                    consultaUsuarios(usuarios);
                    break;
                }
                System.out.println(line);

            }
    }
    //Envio de Mensaje 
    private void enviarMensaje(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("ok")) {
                    break;
                }
                System.out.println(line);
            }
    }
    //Envia solicitud a usuario para iniciar el servidor udp para recibir el audio
    private void enviarAudio(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("ok")) {
                    break;
                }
                System.out.println(line);
            }
    }
    
}

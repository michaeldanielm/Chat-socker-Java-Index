/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas.distribuidos.servidorindex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = 15432;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    HashMap<String, Usuario> clientes_map = null;

    public MultiThreadedServer(int port){
        this.serverPort = port;
        clientes_map = new HashMap<>();
    }

    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            new Thread(
                new WorkerRunnable(
                    clientSocket,clientes_map, "Multithreaded Server")
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 15432", e);
        }
    }

    private static class WorkerRunnable implements Runnable {

        private final Socket clientSocket;
        HashMap<String, Usuario> clientes_map = null;

        public WorkerRunnable(Socket clientSocket,HashMap<String, Usuario> clientes_map, String multithreaded_Server) {
            this.clientSocket  = clientSocket;
            this.clientes_map = clientes_map;
        }

        @Override
        public void run() {
            System.out.println("conexion establecida con " + clientSocket.getLocalAddress()+ "...");
        try {
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("IS-")) {
                    
                   String str_usuario = line.split("-")[1];
                   String str_puerto = line.split("-")[2];

                   Usuario cliente = new Usuario();
                   cliente.setUsuario(str_usuario);
                   cliente.setInetAddress(clientSocket.getInetAddress().toString());
                   cliente.setPort(Integer.valueOf(str_puerto));
                   
                   this.clientes_map.put(cliente.getUsuario(), cliente);
                    
                    System.out.println("nuevo cliente registrado "+cliente);
                    writer.println("ok");
                    
                }else if (line.startsWith("CC-")) {
                    
                    String str_usuario = line.split("-")[1];
                    for (Map.Entry<String, Usuario> entry : clientes_map.entrySet()) {
                        String key = entry.getKey();
                        Usuario value = entry.getValue();
                        
                        //se escluye el usuario que hace la peticion
                        if (!key.equals(str_usuario)) {
                            
                            writer.println("U-"+key+"-"+value.getInetAddress()+"-"+value.getPort());    
                            
                        }
                        
                    }
                    
                    writer.println("ok");
                }
            }

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
           
         
        }

       
        
        
    }

}
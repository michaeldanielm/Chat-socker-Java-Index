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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Administrator
 */
public class Chat implements Runnable {

    private ServerSocket serverSocket = null;
    private boolean waiting = false;
    private Map<String, Usuario> clientesMap = new HashMap<>();
    private String usuario;

    public static void main(String[] args) throws IOException {

        new Chat().ingresar();

    }

    private int puerto;

    private void ingresar() {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese usuario: ");
        this.usuario = scanner.nextLine();
        System.out.println("Ingrese puerto: ");
        String str_puerto = scanner.nextLine();

        if (str_puerto != null && !"".equals(str_puerto)) {
            //Crea el hilo para el comando
            this.puerto = Integer.parseInt(str_puerto);
            new Thread(new ClienteTcp("IS-" + this.usuario + "-" + str_puerto) {
                @Override
                void consultaUsuarios(Map<String, Usuario> respuesta) {
                    // N/A
                }

                @Override
                void iniciarStreaming() {
                    // N/A
                }
            }).start();

        }

        //INICIA SERVIDOR PARA RECIBIR PETICIONES DE OTROS CLIENTES
        try {
            this.puerto = Integer.valueOf(str_puerto);
            serverSocket = new ServerSocket(this.puerto);
            System.out.println("Servidor Corriendo en puerto " + this.puerto + "...");

            //crea hilo para procesar la conexion con el servidor
            new Thread(this).start();

            while (true) {

                System.out.println("MENU: ");
                System.out.println("1. Enviar Mensaje");
                System.out.println("2. Enviar Audio");
                System.out.println("5. Salir");

                String opt = scanner.nextLine();

                if (str_puerto == null || "".equals(str_puerto)) {
                    break;
                } else {
                    switch (opt) {
                        case "1":
                            System.out.println("Ingrese mensaje:");
                            String msg = scanner.nextLine();
                            int num_clientes = 0;
                            waiting = true;
                            new Thread(new ClienteTcp("CC-" + this.usuario) {
                                @Override
                                void consultaUsuarios(Map<String, Usuario> usuarios) {
                                    //termina la consulta
                                    consultaUsuariosFinalizada(usuarios);

                                }

                                @Override
                                void iniciarStreaming() {
                                    // N/A
                                }
                            }).start();

                            //Espera a que se procese de consulta de usuarios
                            while (waiting) {
                                try {
                                    System.out.println("...");
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                }
                            }

                            if (clientesMap.isEmpty()) {
                                System.out.println("no se hallaron usuarios conectados");
                            } else {
                                System.out.println("Usuarios conectados:");
                                for (Map.Entry<String, Usuario> entry : clientesMap.entrySet()) {
                                    String u = entry.getKey();
                                    Usuario c = entry.getValue();
                                    System.out.println(u);
                                }
                                System.out.println("usuario a enviar mensaje");
                                String str_usuario = scanner.nextLine();
                                if (clientesMap.containsKey(str_usuario)) {
                                    Usuario rcvCliente = clientesMap.get(str_usuario);

                                    new Thread(new ClienteTcp("EM-" + rcvCliente.getInetAddress() + "-" + rcvCliente.getPort() + "-" + msg) {
                                        @Override
                                        void consultaUsuarios(Map<String, Usuario> usuarios) {
                                            //  N/A
                                        }

                                        @Override
                                        void iniciarStreaming() {
                                            // N/A
                                        }
                                    }).start();
                                }
                            }

                            break;

                        case "2":

                            waiting = true;
                            new Thread(new ClienteTcp("CC-" + this.usuario) {
                                @Override
                                void consultaUsuarios(Map<String, Usuario> usuarios) {
                                    //termina la consulta
                                    consultaUsuariosFinalizada(usuarios);
                                }

                                @Override
                                void iniciarStreaming() {
                                    // N/A
                                }
                            }).start();

                            //Espera a que se procese de consulta de usuarios
                            while (waiting) {
                                try {
                                    System.out.println("...");
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                }
                            }

                            if (clientesMap.isEmpty()) {
                                System.out.println("no se hallaron usuarios conectados.");
                            } else {
                                System.out.println("Usuarios conectados:");
                                for (Map.Entry<String, Usuario> entry : clientesMap.entrySet()) {
                                    String u = entry.getKey();
                                    Usuario c = entry.getValue();
                                    System.out.println(u);
                                }
                                System.out.println("usuario a enviar audio");
                                String str_usuario = scanner.nextLine();
                                if (clientesMap.containsKey(str_usuario)) {
                                    //Espera por el usuario por la confirmación para envia audio
                                    waiting = true;
                                    Usuario rcvCliente = clientesMap.get(str_usuario);
                                    new Thread(new ClienteTcp("EA-" +  rcvCliente.getInetAddress() + "-" + rcvCliente.getPort()) {
                                        @Override
                                        void consultaUsuarios(Map<String, Usuario> usuarios) {
                                            // N/A
                                        }

                                        @Override
                                        void iniciarStreaming() {
                                            iniciaStreaming();
                                        }
                                    }).start();

                                    //Espera a que se confirme conexion
                                    while (waiting) {
                                        try {
                                            System.out.println("...");
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                        }
                                    }

                                    
                                    String path_audio = "D:\\review\\test1.mp3";
                                    waiting = true;
                                    new Thread(new ClienteUdp("EA-" + rcvCliente.getInetAddress() + "-" + rcvCliente.getPort() + "-" + path_audio) {
                                        @Override
                                        void endStreaming() {
                                            terminaStreaming();
                                        }
                                    }).start();
                                    //Espera a que se envie el audio
                                    while (waiting) {
                                        try {
                                            System.out.println("...");
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                        }
                                    }
                                    
                                }
                            }
                            break;
                        default:
                            System.exit(0);
                    }

                }

            }

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

    }

    public void consultaUsuariosFinalizada(Map<String, Usuario> usuarios) {

        this.clientesMap = usuarios;
        waiting = false;
    }
    public void iniciaStreaming() {

        waiting = false;
    }
    public void terminaStreaming() {

        waiting = false;
    }

    //Servidor
    @Override
    public void run() {

        try {

            while (true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("conexion establecida con cliente " + clientSocket.getLocalPort() + "...");
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                InputStream input = clientSocket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.startsWith("EA-")) {
                        
                        writer.println("ok");
                        new Thread(new ClienteUdp("RA-"+this.puerto) {
                            @Override
                            void endStreaming() {
                                
                            }
                        }).start();
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    
}

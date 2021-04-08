/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas.distribuidos.chat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.SocketException;


/**
 *
 * @author Administrator
 */
public abstract class ClienteUdp implements Runnable {

    private String str_request = "";

    public ClienteUdp(String request) {
        this.str_request = request;
    }

    @Override
    public void run() {

        String type = str_request.split("-")[0];

        //envio de audio
        if (type.equals("EA")) {
            try {
                String host_server = str_request.split("-")[1];
                int port = Integer.valueOf(str_request.split("-")[2]);
                
                host_server = host_server.replace("/", "");
                
                String str_file = str_request.split("-")[3];
                File myFile = new File(str_file);
                enviarAudio(myFile,host_server,port);
                endStreaming();
            } //recibir audio
            catch (IOException ex) {
                 System.out.println("I/O error: " + ex.getMessage());
            }
          
        }
        else if (type.endsWith("RA")) {
            int port = Integer.valueOf(str_request.split("-")[1]);
            recibirAudio(port);
        }

    }

    abstract void endStreaming();
    // Enviar audio 
    private void enviarAudio(File myFile,String host,int port) throws IOException {

        DatagramSocket ds = null;
        BufferedInputStream bis = null;
        try {
            ds = new DatagramSocket();
            DatagramPacket dp;
            int packetsize = 1024;
            double nosofpackets;
            nosofpackets = Math.ceil(((int) myFile.length()) / packetsize);

            bis = new BufferedInputStream(new FileInputStream(myFile));
            //Envio de Pacuetes de Datos de archivo
            for (double i = 0; i < nosofpackets + 1; i++) {
                byte[] mybytearray = new byte[packetsize];
                bis.read(mybytearray, 0, mybytearray.length);
                System.out.println("Packet:" + (i + 1));
                dp = new DatagramPacket(mybytearray, mybytearray.length, InetAddress.getByName(host), port);
                ds.send(dp);
                Thread.sleep(10L);
            }
        } catch (SocketException ex) {
            System.out.println("SocketException "+ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException");
        } catch (IOException ex) {
             System.out.println("I/O error: " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException");
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ds != null) {
                ds.close();
            }
        }

    }
    // Se establece la clase de recibir audio por medio de udp
    private void recibirAudio(int port) {
        try {
            DatagramSocket serverSocketUdp = new DatagramSocket(port);
            int packetsize = 1024;
            FileOutputStream fos = null;

            fos = new FileOutputStream("a.mp3");
            //Establece la ruta donde recibe el audio
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            double nosofpackets = Math.ceil(((int) (new File("D:\\review\\test1.mp3")).length()) / packetsize);
            byte[] mybytearray = new byte[packetsize];
            DatagramPacket receivePacket = new DatagramPacket(mybytearray, mybytearray.length);

            System.out.println(nosofpackets + " " + mybytearray + " " + packetsize);

            for (double i = 0; i < nosofpackets + 1; i++) {

                serverSocketUdp.receive(receivePacket);
                byte audioData[] = receivePacket.getData();
                System.out.println("Packet:" + (i + 1));
                bos.write(audioData, 0, audioData.length);
            }
        } catch (SocketException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

}

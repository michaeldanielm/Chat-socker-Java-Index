/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemas.distribuidos.servidorindex;

/**
 *
 * @author Administrator
 */
public class ServidorIndex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MultiThreadedServer server = new MultiThreadedServer(15432);
        new Thread(server).start();

        while (true) {
            try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            break;
        }  
        }
      
        System.out.println("Stopping Server");
        server.stop();
    }

}

/*

 * JAVA GPS Simulator
 * ISO Raid Project
 * 
 * From Perl oPhone GPS Simulator

 * Author: Alexis DUQUE - alexisd61@gmail.com - 2013
 *
 */
package gps.simulator.modele;

import gps.simulator.GPSimulatorGUI;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author Alex
 */
public class Nomadic extends StandardGPS {

    public Nomadic(String address, int port, int period, int code, String file, GPSimulatorGUI sim) {
        super(address, port, period, code, file, sim);
        this.modele = "Nomadic";
    }

    @Override
    public void run() {
       try {
            socket = new Socket(sockAddress, sockPort);
            out = new PrintStream(socket.getOutputStream());
            this.timeZone = "Europe/Paris";
            this.listen = new ListenThread(socket, this);
            listen.start();
            InputStream stream = Nomadic.class.getResourceAsStream("/gps/resources/jeu_essai_positions.txt");
            System.out.println("**** Connexion start - "+this.modele+" GPS****");
            try (Scanner scanner = new Scanner(stream)) {
               String line = null;
               try {
                   while (scanner.hasNextLine()) {
                       // Check if stop thread msg send
                       testStop();
                       
                       if (filsBufer != null) {
                           sendTrame(filsBufer);
                           filsBufer = null;
                       }
                       
                       // Format and print Date
                       Date date = new Date();
                       TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
                       String dateString = dateFormat.format(date);
                       //Show formated Date
                       System.out.println(dateString);
                       // Read line frome file
                       line = scanner.nextLine();
                       String[] splits = line.split(",");
                       System.out.println("Param number : " + splits.length);
                       // Build trame
                       String gpsTrame = this.imei + "," + dateString + "," + splits[2]
                               + "," + splits[3] + "," + splits[4] + "," + splits[5] + ","
                               + splits[6] + "," + splits[7] + "," + splits[8] + "\r\n";
                       
                       // Try to connect to server and send data
                       sendTrame(gpsTrame);
                       stopIsSend();
                       //Sleep
                       sleep();
                   }
                   
               } catch (InterruptedException e) {
                   out.close();
                   //socket.close();
                   scanner.close();
                   System.err.println("GPS Stopped by user");
               }
           } catch(Exception e) {
               System.err.println("Oops ! Coudn't read frome file");
           }
                
        } catch (IOException e) {
            System.err.println("Oops ! Coudn't send data");
            //e.printStackTrace();
        }
    }
}
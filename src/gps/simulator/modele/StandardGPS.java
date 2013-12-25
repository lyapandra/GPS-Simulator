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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

public class StandardGPS implements Runnable{

    protected String timeZone;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    protected int sockPort;
    protected String sockAddress;
    protected int sendPeriod;
    protected int imei;
    protected String fileGPS;
    protected boolean stopGPS;
    protected GPSimulatorGUI simulator;
    
    public StandardGPS(){
        Random rand = new Random(); 
        this.sockAddress = "localhost";
        this.sockPort = 42400;
        this.sendPeriod = 20;
        this.imei = 2000000001 + rand.nextInt(1000);
        this.fileGPS = "D:\\gps_collect\\perl\\jeu_essai_positions.txt";
        this.stopGPS = false;
        
    }

    public StandardGPS(String address, int port, int period, int code, String file, GPSimulatorGUI simulator) {
        this.sockAddress = address;
        this.sockPort = port;
        this.sendPeriod = period;
        this.imei = code;
        this.fileGPS = file;
        this.stopGPS = false;
        this.simulator = simulator;
    }

    public void run() {
    }

    public synchronized void stopGPS() {
        simulator.stopGPS = true;
        this.stopGPS = true;
    }

    public synchronized void testStop() throws InterruptedException {
        if (simulator.stopGPS) {
            this.stopGPS = true;
            throw new InterruptedException();
        }
    }
    
    public synchronized void stopIsSend() {
        if (simulator.stopGPS) {
            this.stopGPS = true;
        }
    }

    public void setAddress(String address) {
        this.sockAddress = address;
    }

    public String getAddress() {

        return this.sockAddress;
    }

    public void setPort(int port) {
        this.sockPort = port;
    }

    public int getPort() {
        return this.sockPort;
    }
    
    public void sleep() {
        try {
            Thread.currentThread().sleep(sendPeriod * 1000);
        } catch (InterruptedException e) {
        }
    }

    public void sendTrame(String gpsTrame) {
        try {
            Socket socket = new Socket(sockAddress, sockPort);
            System.out.println("**** Connexion start - NomadicGPS****");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            // Send Trame throught socket
            out.println(gpsTrame);
            System.out.print("Sended : " + gpsTrame);
            // Waiting for ACK message
            //System.out.println(in.readLine());
            socket.close();

        } catch (IOException e) {
            System.err.println("Oops ! Coudn't send data");
            //e.printStackTrace();
        }
    }
}


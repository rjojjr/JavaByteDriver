package kirchnersolutions.javabyte.driver.common.driver;

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * ServerClient v1.0.00b
 * 
 * @author Robert Kirchner Jr.
 * 2018 Kirchner Solutions
 */
class ServerClient {
    
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private volatile AtomicBoolean connected = new AtomicBoolean(false);

    private String host, add;
    private int p;
 
    boolean startConnection(String hostName, String ip, int port) throws IOException, IllegalArgumentException {
        this.host = hostName;
        this.add = ip;
        this.p = port;
        if(ip.equals(" ") && !hostName.equals(" ") && port > 0){
            ip = InetAddress.getByName(hostName).toString();
        } else if(ip.equals(" ") && hostName.equals(" ")){
            throw new IllegalArgumentException("Invalid host arguments");
        }
        if(port <= 0){
            throw new IllegalArgumentException("Invalid host port");
        }
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        connected.set(true);
        return true;
    }

    boolean isConnected(){
        return connected.get();
    }
 
    String sendMessage(String msg) throws IOException {
        try{
            if(isConnected()){
                out.println(msg);
                out.flush();
                String resp = in.readLine();

                if(resp.contains("-close")){
                    stopConnection();
                    connected.set(false);

                    return "closed";
                }
                return resp;
            }
        }catch (Exception e){
            e.printStackTrace();
            connected.set(false);
        }
        stopConnection();
        return null;
    }
 
    void stopConnection() throws IOException {
        connected.set(false);
        in.close();
        out.close();
        clientSocket.close();
    }
}

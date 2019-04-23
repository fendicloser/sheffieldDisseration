package com.cart.manager;

import com.cart.observer.SocketEventObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientTask implements Runnable {
    private String host;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private PrintWriter outputStream;
    private boolean isRunning;

    public ClientTask(String host, int port) {
        this.host = host;
        this.port = port;
        isRunning = true;
    }

    @Override
    public void run() {
        try {
            System.out.println("start connect the server " + host + ":" + port);
            socket = new Socket();
            //create a socket,
            //get host and port of the Raspberry Pi
            socket.connect(new InetSocketAddress(host, port), 3000);
            inputStream = socket.getInputStream();//get socket input
            outputStream = new PrintWriter(socket.getOutputStream());//generate socket output
            SocketEventObserver.getInstance().notifyConnectObserverSuccess();
            while (isRunning && socket.isConnected()) {
                //recieve data, decoding,
                Thread.sleep(100);
                byte[] data = new byte[1024]; //set the max length of Socket information
                int length = inputStream.read(data, 0, data.length);
                String s = new String(data, 0, length, "utf-8");
                System.out.println("the receive is " + s + " and length is " + length);
                SocketEventObserver.getInstance().notifyReceviedData(s);



            }





        } catch (IOException e) {
            SocketEventObserver.getInstance().notifyConnectObserverFailed();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            SocketEventObserver.getInstance().notifyConnectObserverFailed();
        }
    }
//Each function sends the request independently
//Receiving is to decode the received information first,
    public void sendData(final String data) {
        if (socket != null && socket.isConnected()) {
            new Thread() {
                @Override
                public void run() {
                    System.out.println("send the data is " + data);
                    outputStream.write(data);
                    outputStream.flush();
                }
            }.start();
        }
    }

    //end of socket connection, release resource, close streams
    public void stop() {
        try {
            isRunning = false;
            inputStream.close();
            outputStream.close();
            socket.close();
            outputStream = null;
            inputStream = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

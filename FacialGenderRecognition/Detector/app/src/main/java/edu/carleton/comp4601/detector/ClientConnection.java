package edu.carleton.comp4601.detector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;


public class ClientConnection {
    public static final String mIPAddress = "172.17.156.162"; // Any other place need to change
    public static final int mPort = 8080;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    BufferedReader inStream;
    PrintWriter outStream;

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

    public ClientConnection(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public void sendMessage(String message){
        try {
            outStream.println(message);
            outStream.flush();
        } catch (Exception exception) { }
    }

    public void run() throws ConnectException {
        mRun = true;
        try {
            InetAddress serverAddr = InetAddress.getByName(mIPAddress);
            Socket socket = new Socket(serverAddr, mPort);
            try {
                inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                while (mRun) {
                    String message = inStream.readLine();
                    System.out.println(message);
                    if (message != null && mMessageListener !=null) {
                        mMessageListener.messageReceived(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }

        } catch (ConnectException e) {
            throw e;
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

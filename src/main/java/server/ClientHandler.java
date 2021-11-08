package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    public void run() {
        try{
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String command = null;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}

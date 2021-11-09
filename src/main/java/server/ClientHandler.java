package server;

import models.User;
import server.command.Command;
import server.command.CommandFactory;
import server.command.exceptions.NoSuchCommandException;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    private User currentUser;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    public void run() {
        try{
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            String line = inputStream.readLine();
            while(line != null && !line.equals("/quit")){
                System.out.println("Server received command: " + line);
                String[] args = line.split(" ");
                try{
                    Command command = CommandFactory.getInstance().getCommand(args[0]);
                    outputStream.println(command.runCommand(currentUser, args));
                }
                catch (NoSuchCommandException ex){
                    outputStream.println("Command does not exist\nDone");
                }
                line = inputStream.readLine();
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}

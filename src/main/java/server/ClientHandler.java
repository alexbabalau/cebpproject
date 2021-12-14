package server;

import models.User;
import server.command.Command;
import server.command.CommandFactory;
import server.command.LoginCommand;
import server.command.exceptions.NoSuchCommandException;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket socket;

    private User currentUser;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private static final String DB_USER = "stock-market";
    private static final String DB_PASS = "password";


    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>(){
        public Connection initialValue(){
            try{
                return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
            catch (SQLException ex){
                ex.printStackTrace();
                return null;
            }
        }
    };

    private static Connection getConnection(){
        return connectionHolder.get();
    }

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
                    if(command instanceof LoginCommand) {
                        ((LoginCommand) command).setClientHandler(this);
                    }
                    String response = command.runCommand(getConnection(), currentUser, args);
                    outputStream.println(response + "\nDone\n");
                }
                catch (NoSuchCommandException ex){
                    outputStream.println("Command does not exist\nDone");
                }
                line = inputStream.readLine();
            }
            getConnection().close();
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

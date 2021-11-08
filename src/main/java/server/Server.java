package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final Integer MAX_THREADS = 10;

    public static void main(String[] args) {

    }

    private void run(){
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        try{
            ServerSocket serverSocket = new ServerSocket(8082);
            while (true){
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                pool.execute(clientHandler);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

}

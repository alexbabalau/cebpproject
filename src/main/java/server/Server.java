package server;

import server.event.StockPriceProducer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final Integer MAX_THREADS = 10;

    public static void main(String[] args) {
        run();
    }

    private static void run(){
        Thread eventThread = new Thread(new StockPriceProducer());
        eventThread.start();
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        try{
            ServerSocket serverSocket = new ServerSocket(8082);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.print("Socket connected to the server\n");
                Runnable clientHandler = new ClientHandler(socket);
                pool.execute(clientHandler);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

}

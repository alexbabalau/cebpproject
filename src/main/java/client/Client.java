package client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Client {

    private static final String EXCHANGE_NAME = "stock_prices";

    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;
    private static String queueName;
    private static Set<String> subscriptions = new HashSet<String>();

    private static void subscribe(String companyCode) throws IOException{
        if(subscriptions.contains(companyCode))
            return;
        channel.queueBind(queueName, EXCHANGE_NAME, companyCode);
        subscriptions.add(companyCode);
    }

    private static void unsubscribe(String companyCode) throws IOException {
        if(subscriptions.contains(companyCode)){
            channel.queueUnbind(queueName, EXCHANGE_NAME, companyCode);
            subscriptions.remove(companyCode);
        }
    }

    private static void handleUnsubscribeCommand(String line) throws IOException {
        String[] args = line.split(" ");
        if(args.length != 2){
            System.out.println("Wrong number of parameters: 1 parameter expected");
            return;
        }
        if(subscriptions.contains(args[1])) {
            unsubscribe(args[1]);
            System.out.println("Successful");
        }
        else{
            System.out.println("Subscription not found");
        }
    }

    private static void listenToEvents() throws Exception{
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        queueName = channel.queueDeclare().getQueue();

        String bindingKey = "";

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Price update: " + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private static void handleSubscribeCommand(String line) throws IOException{
        String[] args = line.split(" ");
        if(args.length != 2){
            System.out.println("Wrong number of parameters: 1 parameter expected");
            return;
        }
        subscribe(args[1]);
        System.out.println("Successful");
    }

    private static void unsubscribeAll() throws IOException{
        for(String companyCode: subscriptions){
            channel.queueUnbind(queueName, EXCHANGE_NAME, companyCode);
        }
        subscriptions.clear();
    }

    public static void main(String[] args) throws Exception{
        try {
            Socket socket = new Socket("127.0.0.1", 8082);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            listenToEvents();
            PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner scanner = new Scanner(System.in);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                outputStream.println(line);
                if(line.equals("/quit"))
                    break;
                if(line.startsWith("/subscribe ")){
                    handleSubscribeCommand(line);
                    continue;
                }

                if(line.startsWith("/unsubscribe ")){
                    handleUnsubscribeCommand(line);
                    continue;
                }

                if(line.startsWith("/login ")){
                    unsubscribeAll();
                }

                String response = inputStream.readLine();
                while(!response.equals("Done")) {
                    System.out.println(response);
                    response = inputStream.readLine();
                }
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

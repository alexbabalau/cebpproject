package client;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class RandomClient {

    private static String[] commands = {"/add-money", "/withdraw-money", "/add-buy-order", "/add-sell-order"};

    private static String[] companies = {"MMM", "AOS", "AAPL", "AMZN", "DIS", "EBAY", "IBM", "NFLX", "NVDA", "PFE"};

    private static Random random = new Random();

    private static String getCommand(){

        String command = commands[random.nextInt(commands.length)];
        String line = null;

        switch (command){
            case "/add-money":{
                line = command + " " + random.nextInt(1000000);
                break;
            }
            case "/withdraw-money":{
                line = command + " " + random.nextInt(1000000);
                break;
            }

            case "/add-buy-order":{
                String companyCode = companies[random.nextInt(companies.length)];
                Integer numberOfUnits = random.nextInt(10);
                Double pricePerUnit = (double)random.nextInt(1000000);
                line = command + " " + companyCode + " " + numberOfUnits + " " + pricePerUnit;
                break;
            }

            case "/add-sell-order": {
                String companyCode = companies[random.nextInt(companies.length)];
                Integer numberOfUnits = random.nextInt(10);
                Double pricePerUnit = (double) random.nextInt(1000000);
                line = command + " " + companyCode + " " + numberOfUnits + " " + pricePerUnit;
                break;
            }

        }
        return line;
    }

    public static void main(String[] args) throws IOException, InterruptedException {


        Socket socket = new Socket("127.0.0.1", 8082);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        String line = "/login user" + random.nextInt(1000000);

        outputStream.println(line);
        String response = inputStream.readLine();
        while(!response.equals("Done")) {
            System.out.println(response);
            response = inputStream.readLine();
        }
        System.out.println("\n\n");

        while(true){
            line = getCommand();

            System.out.println("Sent: " + line);

            outputStream.println(line);
            response = inputStream.readLine();
            while(!response.equals("Done")) {
                System.out.println(response);
                response = inputStream.readLine();
            }
            System.out.println("\n");
            Thread.sleep(1000);
        }
    }
}

package client;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class RandomCompanyClient {
    private static String[] commands = {"/login", "/add-stocks", "/add-sell-order", "/add-buy-order"};

    private static String currentCompany;

    private static String[] companies = {"MMM", "AOS", "AAPL", "AMZN", "DIS", "EBAY", "IBM", "NFLX", "NVDA", "PFE"};

    private static Random random = new Random();

    private static String getCommand(){

        String command = commands[random.nextInt(commands.length)];
        String line = null;

        switch (command){
            case "/login":{
                currentCompany = companies[random.nextInt(companies.length)];
                line = command + " " + currentCompany;
                break;
            }
            case "/add-stocks":{
                line = command + " " + random.nextInt(10000);
                break;
            }
            case "/add-sell-order":{

                String companyCode = currentCompany;
                if(companyCode == null)
                    return null;
                Integer numberOfUnits = random.nextInt(10);
                Double pricePerUnit = (double) random.nextInt(1000000);
                line = command + " " + companyCode + " " + numberOfUnits + " " + pricePerUnit;
                break;
            }

            case "/add-buy-order":{

                String companyCode = companies[random.nextInt(companies.length)];
                Integer numberOfUnits = random.nextInt(10);
                Double pricePerUnit = (double)random.nextInt(1000000);
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

        while(true){
            String line = getCommand();
            if(line == null)
                continue;
            System.out.println("Sent: " + line);

            outputStream.println(line);
            String response = inputStream.readLine();
            while(!response.equals("Done")) {
                System.out.println(response);
                response = inputStream.readLine();
            }
            System.out.println("\n\n");
            Thread.sleep(1000);
        }
    }
}

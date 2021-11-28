package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8082);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner scanner = new Scanner(System.in);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                outputStream.println(line);
                if(line.equals("/quit"))
                    break;
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

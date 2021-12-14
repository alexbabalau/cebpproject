package server.event;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import dao.TransactionService;
import models.transientModels.StockPrice;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StockPriceProducer implements Runnable{

    private static final String EXCHANGE_NAME = "stock_prices";

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    public void run() {
        java.sql.Connection dbConnection = null;
        try{
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return;
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            while(true){
                List<StockPrice> stockPrices = TransactionService.getInstance().getStockPrices(dbConnection);
                for(StockPrice stockPrice: stockPrices){
                    String routingKey = stockPrice.getCompanyCode();
                    String message = stockPrice.getCompanyCode() + "   " + stockPrice.getPrice();
                    channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
                }
                Thread.sleep(10000);
            }

        }
        catch (IOException | TimeoutException | SQLException | InterruptedException ex){
            ex.printStackTrace();
        }
    }
}

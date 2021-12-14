package server.event;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dao.TransactionService;
import models.transientModels.StockPrice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StockPriceProducer implements Runnable{

    private static final String EXCHANGE_NAME = "stock_prices";

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            while(true){
                List<StockPrice> stockPrices = TransactionService.getInstance().getStockPrices();
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

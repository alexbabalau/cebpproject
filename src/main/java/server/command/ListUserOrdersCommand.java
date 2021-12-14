package server.command;

import dao.OrderService;
import models.BuyOrder;
import models.SellOrder;
import models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListUserOrdersCommand implements Command {

    private OrderService orderService = OrderService.getInstance();

    public String runCommand(Connection connection, User currentUser, String[] args) {
        List<SellOrder> sellOrders = new ArrayList<>();
        List<BuyOrder> buyOrders = new ArrayList<>();
        try {
            sellOrders = orderService.getUserSellOrders(connection, args[1]);
            buyOrders = orderService.getUserBuyOrders(connection, args[1]);
        } catch (SQLException | InterruptedException e) {
            return "Error while listing stocks";
        }
        StringBuilder result = new StringBuilder();

        result.append("SELL ORDERS\n");
        for (SellOrder sellOrder : sellOrders) {
            result.append("Price: " + sellOrder.getPricePerUnit() + ", Shares: " + sellOrder.getNumberOfUnits() + "\n");
        }

        if(!buyOrders.isEmpty()){
            result.append("\nBUY ORDERS\n");
            for (BuyOrder buyOrder : buyOrders) {
                result.append("Price: " + buyOrder.getPricePerUnit() + ", Shares: " + buyOrder.getNumberOfUnits() + "\n");
            }
        }

        return result.toString();

    }
}
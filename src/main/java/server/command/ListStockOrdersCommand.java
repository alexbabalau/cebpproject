package server.command;

import dao.OrderService;
import models.SellOrder;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListStockOrdersCommand implements Command {

    private OrderService orderService = OrderService.getInstance();

    public String runCommand(User currentUser, String[] args) {
        List<SellOrder> sellOrders = new ArrayList<>();
        try{
            sellOrders = orderService.getCompanySellOrders(args[1]);
        }
        catch (SQLException | InterruptedException e){
            return "Error while listing stocks";
        }
        StringBuilder result = new StringBuilder();

        result.append("SELL ORDERS\n");
        for(SellOrder sellOrder: sellOrders){
            result.append("Price: " + sellOrder.getPricePerUnit() + ", Shares: " + sellOrder.getNumberOfUnits() + "\n");
        }
        result.append("Done\n");

        return result.toString();

    }
}

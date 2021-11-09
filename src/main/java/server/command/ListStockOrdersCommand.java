package server.command;

import dao.OrderDao;
import models.SellOrder;
import models.User;

import java.util.List;

public class ListStockOrdersCommand implements Command {

    private OrderDao orderDao = OrderDao.getInstance();

    public String runCommand(User currentUser, String[] args) {
        List<SellOrder> sellOrders = orderDao.getCompanySellOrders(args[1]);
        StringBuilder result = new StringBuilder();

        result.append("SELL ORDERS\n");
        for(SellOrder sellOrder: sellOrders){
            result.append("Price: " + sellOrder.getPricePerUnit() + ", Shares: " + sellOrder.getNumberOfUnits() + "\n");
        }
        result.append("Done\n");

        return result.toString();

    }
}

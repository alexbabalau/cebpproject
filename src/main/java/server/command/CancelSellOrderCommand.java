package server.command;

import dao.OrderService;
import models.User;

import java.sql.Connection;

public class CancelSellOrderCommand implements Command {
    private OrderService orderService = OrderService.getInstance();

    @Override
    public String runCommand(Connection con, User currentUser, String[] args) {
        // orderService.deleteSellOrderWithIdWithConnection(Integer.getInteger(args[1]), );
        return null;
    }
}

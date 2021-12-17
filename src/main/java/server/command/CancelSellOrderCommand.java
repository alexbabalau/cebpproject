package server.command;

import dao.OrderService;
import models.User;

import java.sql.Connection;
import java.sql.SQLException;

public class CancelSellOrderCommand implements Command {
    private OrderService orderService = OrderService.getInstance();

    @Override
    public String runCommand(Connection con, User currentUser, String[] args) {
        try {
            orderService.deleteSellOrderWithId(Integer.parseInt(args[1]), con);
        } catch(SQLException | InterruptedException ex) {
            return "Error in deleting sell order : " + ex.getMessage() + "\n";
        }

        return "Success in deleting the sell order\n";
    }
}

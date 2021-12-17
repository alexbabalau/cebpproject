package server.command;

import dao.OrderService;
import models.User;

import java.sql.Connection;
import java.sql.SQLException;

public class CancelBuyOrderCommand implements Command {
    private OrderService orderService = OrderService.getInstance();

    @Override
    public String runCommand(Connection con, User currentUser, String[] args) {
        try {
            orderService.deleteBuyOrderWithId(Integer.parseInt(args[1]), con);
        } catch(SQLException | InterruptedException ex) {
            return "Error in deleting buy order : " + ex.getMessage() + "\n";
        }

        return "Success in deleting the buy order\n";
    }
}

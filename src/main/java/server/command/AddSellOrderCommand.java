package server.command;

import dao.OrderService;
import models.User;

import java.sql.SQLException;

public class AddSellOrderCommand implements Command{
    @Override
    public String runCommand(User currentUser, String[] args) {

        StringBuilder result = new StringBuilder("");
        try{
            String response = OrderService.getInstance().addSellOrder(args[1], Integer.parseInt(args[2]), Double.parseDouble(args[3]), currentUser);
            result.append(response);
        }
        catch (SQLException ex){
            result.append(ex.getMessage());
        }
        return result.toString();
    }
}

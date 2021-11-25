package server.command;

import dao.OrderService;
import models.User;

import java.sql.SQLException;

public class AddSellOrderCommand implements Command{
    @Override
    public String runCommand(User currentUser, String[] args) {

        StringBuilder result = new StringBuilder("");
        try{
            String response = OrderService.getInstance().addSellOrder(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]), currentUser);
            result.append(response + "\n");
        }
        catch (SQLException ex){
            result.append(ex.getMessage() + "\n");
        }
        result.append("Done\n");
        return result.toString();
    }
}

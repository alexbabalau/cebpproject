package server.command;

import dao.OrderService;
import models.User;
import server.command.exceptions.NotEnoughUnitsException;

import java.sql.Connection;
import java.sql.SQLException;

public class AddSellOrderCommand implements Command{
    @Override
    public String runCommand(Connection connection, User currentUser, String[] args) {

        StringBuilder result = new StringBuilder("");
        try{
            String response = OrderService.getInstance().addSellOrder(connection, args[1], Integer.parseInt(args[2]), Double.parseDouble(args[3]), currentUser);
            result.append(response);
        }
        catch (SQLException ex){
            result.append(ex.getMessage());
        }
        catch(NotEnoughUnitsException ex){
            result.append(ex.getMessage());
        }
        catch (NumberFormatException ex){
            result.append("Wrong argument format: " + ex.getMessage());
        }
        return result.toString();
    }
}

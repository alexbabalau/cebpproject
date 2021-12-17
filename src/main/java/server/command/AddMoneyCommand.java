package server.command;

import dao.UserService;
import models.User;

import java.sql.Connection;

public class AddMoneyCommand implements Command{
    @Override
    public String runCommand(Connection connection, User currentUser, String[] args) {
        Double amount = Double.parseDouble(args[1]);
        UserService userService = UserService.getInstance();

        if(currentUser == null)
            return "Please login!";

        return userService.addMoney(connection, currentUser, amount);
    }
}

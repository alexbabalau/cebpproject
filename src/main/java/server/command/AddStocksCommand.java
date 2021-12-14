package server.command;

import dao.CompanyService;
import models.User;

import java.sql.Connection;

public class AddStocksCommand implements Command{
    @Override
    public String runCommand(Connection connection, User currentUser, String[] args) {
        return CompanyService.getInstance().addStocks(connection, currentUser, Integer.parseInt(args[1]));
    }
}

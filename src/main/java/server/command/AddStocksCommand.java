package server.command;

import dao.CompanyService;
import dao.CompanyShareService;
import models.User;

public class AddStocksCommand implements Command{
    @Override
    public String runCommand(User currentUser, String[] args) {
        return CompanyService.getInstance().addStocks(currentUser, Integer.parseInt(args[1]));
    }
}

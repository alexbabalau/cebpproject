package server.command;

import dao.UserService;
import models.User;

public class WithdrawMoneyCommand implements Command{
    @Override
    public String runCommand(User currentUser, String[] args) {
        Double amount = Double.parseDouble(args[1]);
        UserService userService = UserService.getInstance();

        if(currentUser == null)
            return "Please login!";

        return userService.withdrawMoney(currentUser, amount);
    }
}

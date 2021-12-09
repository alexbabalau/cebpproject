package server.command;

import dao.OrderService;
import dao.UserService;
import models.Transaction;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListUserTransactionHistoryCommand implements Command {

    private OrderService orderService = OrderService.getInstance();
    private UserService userService = UserService.getInstance();

    public String runCommand(User currentUser, String[] args) {
        List<Transaction> transactions = new ArrayList<>();
        Integer userId = 1;

        try {
            transactions = orderService.getTransactionHistory(args[1]);
            userId = userService.getIdForUsername(args[1]);
        } catch ( Exception e) {
            return "Error while listing transaction history";
        }
        StringBuilder result = new StringBuilder();

        result.append("TRANSACTION HISTORY\n");

        for (Transaction transaction : transactions) {
            if(transaction.getBuyerId().compareTo(userId)==0)
                result.append("Bought  ");
            else result.append("Sold    ");
            result.append("on " + transaction.getDate() + " Price: " + transaction.getPricePerUnit() + ", Shares: " + transaction.getNumberOfUnits() + "\n");
        }

        return result.toString();

    }
}
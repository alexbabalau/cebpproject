package server.command;

import dao.TransactionService;
import models.Transaction;
import models.User;
import models.transientModels.StockPrice;

import java.sql.SQLException;
import java.util.List;

public class ListStockPricesCommand implements Command{
    @Override
    public String runCommand(User currentUser, String[] args) {
        List<StockPrice> stockPrices = null;

        try{
            stockPrices = TransactionService.getInstance().getStockPrices();
        }
        catch (SQLException ex){
            return "Error in listing stock prices: " + ex.getMessage();
        }
        final StringBuilder result = new StringBuilder("");
        stockPrices.stream().forEach((stockPrice) -> {
            result.append(stockPrice.getCompanyName() + "       " + stockPrice.getPrice() + "\n");
        });
        return result.toString();
    }
}
package server.command;

import server.ClientHandler;
import server.command.exceptions.NoSuchCommandException;

public class CommandFactory {
    private static CommandFactory instance;

    private CommandFactory(){

    }

    public static CommandFactory getInstance() {
        if(instance == null)
            instance = new CommandFactory();
        return instance;
    }

    public Command getCommand(String commandName){
        switch (commandName){
            case "/list-stock-orders": {
                return new ListStockOrdersCommand();
            }
            case "/list-stock-prices":{
                return new ListStockPricesCommand();
            }
            case "/add-sell-order": {
                return new AddSellOrderCommand();
            }
            case "/list-user-orders": {
                return new ListUserOrdersCommand();
            }
            case "/list-user-transaction-history": {
                return new ListUserTransactionHistoryCommand();
            }
            case "/add-buy-order":{
                return new AddBuyOrderCommand();
            }
            case "/add-money": {
                return new AddMoneyCommand();
            }
            case "/withdraw-money": {
                return new WithdrawMoneyCommand();
            }
            case "/login": {
                return new LoginCommand();
            }
            case "/add-stocks":{
                return new AddStocksCommand();
            }
            default:
                throw new NoSuchCommandException();
        }
    }
}

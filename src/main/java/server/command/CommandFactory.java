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
            case "/add-money": {
                return new AddMoneyCommand();
            }
            case "/withdraw-money": {
                return new WithdrawMoneyCommand();
            }
            case "/login": {
                return new LoginCommand();
            }
            default:
                throw new NoSuchCommandException();
        }
    }
}

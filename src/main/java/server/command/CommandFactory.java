package server.command;

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
            default:
                throw new NoSuchCommandException();
        }
    }
}

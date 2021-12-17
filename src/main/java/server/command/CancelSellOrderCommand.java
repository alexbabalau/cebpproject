package server.command;

import models.User;

public class CancelSellOrderCommand implements Command {
    @Override
    public String runCommand(User currentUser, String[] args) {
        return null;
    }
}

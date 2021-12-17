package server.command;

import dao.OrderService;
import models.User;

public class CancelSellOrderCommand implements Command {
    private OrderService orderService = OrderService.getInstance();

    @Override
    public String runCommand(User currentUser, String[] args) {
        // orderService.deleteSellOrderWithIdWithConnection(Integer.getInteger(args[1]), );
        return null;
    }
}

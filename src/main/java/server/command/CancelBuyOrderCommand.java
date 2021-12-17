package server.command;

import dao.OrderService;
import models.User;

public class CancelBuyOrderCommand implements Command {
    private OrderService orderService = OrderService.getInstance();

    @Override
    public String runCommand(User currentUser, String[] args) {
        // orderService.deleteBuyOrderWithIdWithConnection(Integer.getInteger(args[1]), );
        return null;
    }
}

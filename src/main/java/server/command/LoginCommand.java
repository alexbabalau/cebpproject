package server.command;

import dao.UserService;
import models.User;
import server.ClientHandler;

public class LoginCommand implements Command{
    private ClientHandler clientHandler;

    @Override
    public String runCommand(User currentUser, String[] args) {
        String username = args[1];
        UserService userService = UserService.getInstance();

        try {
            currentUser = userService.login(username);
            clientHandler.setCurrentUser(currentUser);
        }
        catch (Exception e) {
            return "Login error";
        }

        return "Successful";
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
}

package server.command;

import dao.UserService;
import models.User;
import server.ClientHandler;

import java.sql.Connection;

public class LoginCommand implements Command{
    private ClientHandler clientHandler;

    @Override
    public String runCommand(Connection connection, User currentUser, String[] args) {
        String username = args[1];
        UserService userService = UserService.getInstance();

        try {
            currentUser = userService.login(connection, username);
            clientHandler.setCurrentUser(currentUser);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Login error";
        }

        return "Successful";
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
}

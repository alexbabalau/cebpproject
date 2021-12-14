package server.command;

import models.User;

import java.sql.Connection;

public interface Command {
    String runCommand(Connection connection, User currentUser, String[] args);
}

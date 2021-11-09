package server.command;

import models.User;

public interface Command {
    String runCommand(User currentUser, String[] args);
}

package server.command;

import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import models.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface Command {
    String runCommand(Connection connection, User currentUser, String[] args) throws MySQLTransactionRollbackException;
}

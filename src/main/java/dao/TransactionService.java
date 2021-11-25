package dao;

import models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionService {
    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }

    private TransactionService(){

    }

    public void insertTransactionWithConnection(Transaction transaction, Connection con) throws SQLException{
        String insertTransactionSql = "INSERT INTO transaction VALUES (?, ?)";
        try(PreparedStatement statement = con.prepareStatement(insertTransactionSql)){
            statement.setInt(1, transaction.getBuyerId());
            statement.setInt(2, transaction.getSellerId());
            statement.setInt(3, transaction.getCompanyId());
            statement.setInt(4, transaction.getNumberOfUnits());
            statement.setDouble(5, transaction.getPricePerUnit());
            statement.setInt(6, 1);
            statement.setDate(7, new java.sql.Date(transaction.getDate().getTime()));
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }


}

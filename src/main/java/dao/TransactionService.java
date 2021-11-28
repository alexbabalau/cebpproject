package dao;

import models.Transaction;
import models.transientModels.StockPrice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private List<StockPrice> getLastTransactionsWithConnection(Connection con) throws SQLException{
        List<StockPrice> stockPrices = new ArrayList<>();
        String listStockSql =
                "SELECT C.company_id as company_id, C.name as name, T.date, T.price_per_unit as price_per_unit FROM transactions T JOIN company C ON T.company_id = C.id " +
                "WHERE T.date >= ALL(" +
                        "SELECT date FROM transactions " +
                        "WHERE company_id = C.company_id)";
        try(PreparedStatement preparedStatement = con.prepareStatement(listStockSql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                stockPrices.add(StockPrice.getStockPriceFromResultSet(resultSet));
            }
        }
        catch (SQLException ex) {
            throw ex;
        }
        return stockPrices;
    }

    public List<StockPrice> getStockPrices() throws SQLException{
        Connection con = null;
        List<StockPrice> stockPrices = null;
        try{
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);
            stockPrices = getLastTransactionsWithConnection(con);
            con.commit();
        }
        catch (SQLException ex){
            if(con != null)
                con.rollback();
            throw ex;
        }
        return stockPrices;
    }


}

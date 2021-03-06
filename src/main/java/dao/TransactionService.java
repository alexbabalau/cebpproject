package dao;

import models.Transaction;
import models.transientModels.StockPrice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TransactionService {
    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private Semaphore mutex = new Semaphore(1, true);
    private Semaphore writeLock = new Semaphore(1, true);
    private Integer numberReads = 0;

    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }

    private void startRead() throws InterruptedException{
        mutex.acquire();
        numberReads += 1;
        if(numberReads == 1)
            writeLock.acquire();
        mutex.release();
    }

    private void endRead() throws InterruptedException{
        mutex.acquire();
        numberReads -= 1;
        if(numberReads == 0)
            writeLock.release();
        mutex.release();
    }

    private TransactionService(){

    }

    public void insertTransaction(Transaction transaction, Connection con) throws SQLException, InterruptedException{
        String insertTransactionSql = "INSERT INTO transaction(buyer_id, seller_id, company_id, number_of_units, price_per_unit, date) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = con.prepareStatement(insertTransactionSql)){
            writeLock.acquire();
            statement.setInt(1, transaction.getBuyerId());
            statement.setInt(2, transaction.getSellerId());
            statement.setInt(3, transaction.getCompanyId());
            statement.setInt(4, transaction.getNumberOfUnits());
            statement.setDouble(5, transaction.getPricePerUnit());
            statement.setTimestamp(6, new java.sql.Timestamp(transaction.getDate().getTime()));
            statement.executeUpdate();

        } catch (SQLException | InterruptedException ex) {
            ex.printStackTrace();
            throw ex;
        }
        finally {
            writeLock.release();
        }
    }

    private List<StockPrice> getLastTransactionsWithConnection(Connection con) throws SQLException, InterruptedException{
        List<StockPrice> stockPrices = new ArrayList<>();
        String listStockSql =
                "SELECT C.id as company_id, C.code as company_code, T.date, T.price_per_unit as price_per_unit FROM transaction T JOIN company C ON T.company_id = C.id " +
                "WHERE T.date >= ALL(" +
                        "SELECT date FROM transaction " +
                        "WHERE company_id = C.id)" +
                        "ORDER BY C.code";
        boolean released = true;
        try(PreparedStatement preparedStatement = con.prepareStatement(listStockSql)){
            startRead();
            released = false;
            ResultSet resultSet = preparedStatement.executeQuery();
            endRead();
            released = true;
            while(resultSet.next()){
                StockPrice stockPrice = StockPrice.getStockPriceFromResultSet(resultSet);
                if(stockPrices.size() > 0 && stockPrice.getCompanyCode().equals(stockPrices.get(stockPrices.size() - 1).getCompanyCode()))
                    continue;
                stockPrices.add(StockPrice.getStockPriceFromResultSet(resultSet));
            }

        }
        catch (SQLException | InterruptedException ex) {
            throw ex;
        }
        finally {
            if(!released)
                endRead();
        }
        return stockPrices;
    }

    public List<StockPrice> getStockPrices(Connection connection) throws SQLException, InterruptedException{
        List<StockPrice> stockPrices = null;
        try{

            connection.setAutoCommit(false);
            stockPrices = getLastTransactionsWithConnection(connection);
            connection.commit();
        }
        catch (SQLException | InterruptedException ex){
            if(connection != null)
                connection.rollback();
            throw ex;
        }
        return stockPrices;
    }

    public List<Transaction> getTransactionHistory(Connection connection, String username) throws SQLException, InterruptedException {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transaction " +
                "WHERE id IN (" +
                "   SELECT T.id from transaction T JOIN User U ON (T.buyer_id = U.id OR T.seller_id = U.id)" +
                "       WHERE  U.username = ?)" +
                "ORDER BY date;";
        boolean released = true;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startRead();
            released = false;
            pstmt.setString(1, username);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                endRead();
                released = true;
                while (resultSet.next()) {
                    Transaction transaction = Transaction.getTransactionFromResultSet(resultSet);
                    transactions.add(transaction);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }
        finally {
            if(!released)
                endRead();
        }
        return transactions;
    }

    private List<Transaction> getTransactionsByCompanyIdWithConnection(Connection con, Integer id) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String listTransactionsSql =
                "SELECT * FROM transaction WHERE company_id = ? " +
                        "ORDER BY date;";

        try(PreparedStatement preparedStatement = con.prepareStatement(listTransactionsSql)){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                transactions.add(Transaction.getTransactionFromResultSet(resultSet));
            }
        }
        catch (SQLException ex) {
            throw ex;
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByCompanyId(Connection con, Integer id) throws SQLException{
        List<Transaction> transactions = null;
        try {
            con.setAutoCommit(false);
            transactions = getTransactionsByCompanyIdWithConnection(con, id);
            con.commit();
        }
        catch (SQLException ex) {
            if(con != null)
                con.rollback();
            throw ex;
        }

        return transactions;
    }

}

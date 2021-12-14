package dao;

import exceptions.NegativeBalanceException;
import models.User;

import java.sql.*;
import java.util.concurrent.Semaphore;

public class UserService {
    private static UserService instance;

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private Semaphore mutex = new Semaphore(1, true);
    private Semaphore writeLock = new Semaphore(1, true);
    private Integer numberReads = 0;

    private UserService(){ }

    public static UserService getInstance() {
        if(instance == null)
            instance = new UserService();
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

    public void updateMoneyWithId(Integer id, Double amount, Connection connection) throws SQLException, InterruptedException {
        String sql = "UPDATE user SET amount = amount + ? WHERE id = ? AND amount + ? >= 0;";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            writeLock.acquire();
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.setDouble(3, amount);
            Integer updatedUsers = pstmt.executeUpdate();
            writeLock.release();
            if(updatedUsers == 0)
                throw new NegativeBalanceException("User does not have enough money");

        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        catch (InterruptedException e){
            throw e;
        }

    }

    public String addMoney(Connection connection, User user, Double amount) {
        Integer userId = user.getId();
        try {
            connection.setAutoCommit(false);
            updateMoneyWithId(userId, amount, connection);
            connection.commit();
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Rollback error";
            }
            return "Error updating money amount";
        }

        return "Successful";
    }

    public String withdrawMoney(Connection connection, User user, Double amount) {

        if(user == null){
            return "Please login!";
        }

        Integer userId = user.getId();

        try {

            connection.setAutoCommit(false);



            try{
                updateMoneyWithId(userId, -amount, connection);
            }
            catch (NegativeBalanceException ex){
                return "Not enough money!";
            }
            connection.commit();
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Rollback error";
            }
            return "Error updating money amount";
        }

        return "Successful";
    }

    private User getUserWithId(Integer id, Connection con) throws SQLException, InterruptedException{
        String sql = "SELECT * FROM user WHERE id=?";
        User user = null;

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, id);
            startRead();
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    user = User.getUserFromResultSet(resultSet);
                }
            }
            endRead();
        }
        catch (SQLException | InterruptedException e){
            e.printStackTrace();
            throw e;
        }

        return user;
    }

    private User getUserWithUsername(String username, Connection con) throws SQLException, InterruptedException {
        String sql = "SELECT * FROM user WHERE username=?";
        User user = null;

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            startRead();
            pstmt.setString(1, username);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    user = User.getUserFromResultSet(resultSet);
                }
            }
            endRead();
        }
        catch (SQLException | InterruptedException e){
            e.printStackTrace();
            throw e;
        }

        return user;
    }

    private User createUser(String username, Connection con) throws SQLException, InterruptedException {
        User user = new User(username, 0.0);
        String sql = "INSERT INTO user(username, amount) VALUES(?, ?)";

        try(PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            writeLock.acquire();
            pstmt.setString(1, username);
            pstmt.setDouble(2, 0.0);
            Integer insertedCount = pstmt.executeUpdate();
            writeLock.release();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        catch (SQLException | InterruptedException e){
            e.printStackTrace();
            throw e;
        }

        return user;
    }

    public User login(Connection connection, String username) throws Exception{
        User user;

        try {
            connection.setAutoCommit(false);

            user = getUserWithUsername(username, connection);

            if(user == null)
                user = createUser(username, connection);

            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            throw throwables;
        }

        return user;
    }


    public Integer getIdForUsername(Connection connection, String username) throws Exception{
        User user;

        try {
            connection.setAutoCommit(false);

            user = getUserWithUsername(username, connection);

            if(user == null)
                user = createUser(username, connection);

            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            throw throwables;
        }

        return user.getId();
    }

}

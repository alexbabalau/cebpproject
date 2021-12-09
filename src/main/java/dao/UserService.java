package dao;

import models.User;

import java.sql.*;

public class UserService {
    private static UserService instance;

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private UserService(){ }

    public static UserService getInstance() {
        if(instance == null)
            instance = new UserService();
        return instance;
    }

    private void updateMoneyWithId(Integer id, Double amount, Connection connection) throws SQLException {
        String sql = "UPDATE user SET amount = amount + ? WHERE id = ?;";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public String addMoney(User user, Double amount) {
        Connection con = null;
        Integer userId = user.getId();

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);
            updateMoneyWithId(userId, amount, con);
            con.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Rollback error";
            }
            return "Error updating money amount";
        }

        return "Successful";
    }

    public String withdrawMoney(User user, Double amount) {
        Connection con = null;
        Integer userId = user.getId();

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);

            user = getUserWithId(userId, con);
            if(user == null) {
                return "Please login!";
            }
            Double currentMoneyAmount = user.getAmount();
            if(currentMoneyAmount < amount)
                return "Not enough money";

            updateMoneyWithId(userId, -amount, con);
            con.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Rollback error";
            }
            return "Error updating money amount";
        }

        return "Successful";
    }

    private User getUserWithId(Integer id, Connection con) {
        String sql = "SELECT * FROM user WHERE id=?";
        User user = null;

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, id);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    user = User.getUserFromResultSet(resultSet);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return user;
    }

    public User getUserWithUsername(String username, Connection con) {
        String sql = "SELECT * FROM user WHERE username=?";
        User user = null;

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, username);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    user = User.getUserFromResultSet(resultSet);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return user;
    }

    private User createUser(String username, Connection con) throws SQLException {
        User user = new User(username, 0.0);
        String sql = "INSERT INTO user(username, amount) VALUES(?, ?)";

        try(PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, username);
            pstmt.setDouble(2, 0.0);
            Integer insertedCount = pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }

        return user;
    }

    public User login(String username) throws Exception{
        Connection con = null;
        User user;

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);

            user = getUserWithUsername(username, con);

            if(user == null)
                user = createUser(username, con);

            con.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            throw throwables;
        }

        return user;
    }


    public Integer getIdForUsername(String username) throws Exception{
        Connection con = null;
        User user;

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);

            user = getUserWithUsername(username, con);

            if(user == null)
                user = createUser(username, con);

            con.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            throw throwables;
        }

        return user.getId();
    }

}

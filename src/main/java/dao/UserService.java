package dao;

import models.BuyOrder;
import models.SellOrder;
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
        String sql = "UPDATE user SET amount=amount+? WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate(sql);
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
        user = getUserWithId(userId, con);
        Double currentMoneyAmount = user.getAmount();

        if(currentMoneyAmount < amount)
            return "Not enough money";

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

    public User getUserWithId(Integer id, Connection con) {
        String sql = "SELECT amount FROM user WHERE id=?";
        User user = null;

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, id);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    user = User.getBuyOrderFromResultSet(resultSet);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return user;
    }

}

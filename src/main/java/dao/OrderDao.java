package dao;

import com.sun.org.apache.xpath.internal.operations.Or;
import models.SellOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    private static OrderDao instance;

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private OrderDao(){

    }

    public static OrderDao getInstance() {
        if(instance == null)
            instance = new OrderDao();
        return instance;
    }

    public List<SellOrder> getCompanySellOrders(String companyCode){
        List<SellOrder> sellOrders = new ArrayList<>();
        try (Connection con = DriverManager
                .getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM sell_order " +
                    "WHERE id IN (" +
                    "   SELECT S.id from sell_order S JOIN Company C ON S.company_id = C.id" +
                    "       WHERE C.code = ?)" +
                    "ORDER BY price_per_unit;";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, companyCode);
                try(ResultSet resultSet = pstmt.executeQuery()){
                    while(resultSet.next()){
                        SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                        sellOrders.add(sellOrder);
                    }
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sellOrders;
    }

    /*public String addSellOrder(String companyCode, Integer numberOfUnits, Double price_per_unit, Integer ownerId){
        Integer companyId;
        try (Connection con = DriverManager
                .getConnection(DB_URL, DB_USER, DB_PASS)) {
            con.setAutoCommit(false);
            String sql = "SELECT id FROM company WHERE code = ?";

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, companyCode);
                try(ResultSet resultSet = pstmt.executeQuery()){
                    if(resultSet.next()){
                        SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                        sellOrders.add(sellOrder);
                    }
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}


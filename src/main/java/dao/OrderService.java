package dao;

import models.*;
import server.command.exceptions.NotEnoughUnitsException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private static OrderService instance;

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private OrderService(){

    }

    public static OrderService getInstance() {
        if(instance == null)
            instance = new OrderService();
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

    private List<BuyOrder> getBuyOrdersGreaterThanFromCompanyIdForUpdateWithConnection(Integer companyId, Double minPrice, Connection con){
        String sql = "SELECT * FROM buy_orders WHERE company_id = ? AND price_per_unit >= ? ORDER BY price_per_unit DESC FOR UPDATE";
        List<BuyOrder> buyOrders = new ArrayList<>();

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, companyId);
            pstmt.setDouble(2, minPrice);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    BuyOrder buyOrder = BuyOrder.getBuyOrderFromResultSet(resultSet);
                    buyOrders.add(buyOrder);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return buyOrders;
    }

    public void deleteBuyOrderWithIdWithConnection(Integer id, Connection connection){
        String sql = "DELETE FROM buy_orders WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate(sql);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateBuyOrderWithIdWithConnection(Integer id, BuyOrder buyOrder, Connection connection){
        String sql = "UPDATE buy_orders SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, buyOrder.getNumberOfUnits());
            pstmt.setDouble(2, buyOrder.getPricePerUnit());
            pstmt.executeUpdate(sql);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteSellOrderWithIdWithConnection(Integer id, Connection connection){
        String sql = "DELETE FROM sell_orders WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate(sql);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateSellOrderWithIdWithConnection(Integer id, SellOrder sellOrder, Connection connection){
        String sql = "UPDATE sell_orders SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, sellOrder.getNumberOfUnits());
            pstmt.setDouble(2, sellOrder.getPricePerUnit());
            pstmt.executeUpdate(sql);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private SellOrder insertSellOrderWithConnection(SellOrder sellOrder, Connection con) throws SQLException{
        String insertSellOrderSql = "INSERT INTO sell_order VALUES(?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = con.prepareStatement(insertSellOrderSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, sellOrder.getCompanyId());
            pstmt.setInt(2, sellOrder.getOwnerId());
            pstmt.setInt(3, sellOrder.getNumberOfUnits());
            pstmt.setDouble(4, sellOrder.getPricePerUnit());
            pstmt.setDate(5, new java.sql.Date(sellOrder.getDate().getTime()));
            pstmt.setInt(6, 1);
            Integer insertedCount = pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sellOrder.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            return sellOrder;
        }
        catch (SQLException ex){
            throw ex;
        }

    }

    public String addSellOrder(String companyCode, Integer numberOfUnits, Double pricePerUnit, User user) throws SQLException {
        Integer companyId;
        Integer ownerId = user.getId();
        Connection con = null;

        try {
            con = DriverManager
                    .getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);
            Company company = CompanyService.getInstance().findByCodeWithConnection(companyCode, con);
            CompanyShare companyShare = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(company.getId(), ownerId, con);
            if (companyShare.getNumberOfUnits() < numberOfUnits)
                throw new NotEnoughUnitsException("Not enough units to sell");
            List<BuyOrder> buyOrders = getBuyOrdersGreaterThanFromCompanyIdForUpdateWithConnection(company.getId(), pricePerUnit, con);
            Integer totalUnits = numberOfUnits;
            Double totalPrice = 0.0;
            for (BuyOrder buyOrder : buyOrders) {
                if (numberOfUnits >= buyOrder.getNumberOfUnits()) {
                    Transaction transaction = new Transaction();
                    transaction.setBuyerId(buyOrder.getOwnerId());
                    transaction.setSellerId(ownerId);
                    transaction.setDate(new java.util.Date());
                    transaction.setCompanyId(company.getId());
                    transaction.setNumberOfUnits(buyOrder.getNumberOfUnits());
                    transaction.setPricePerUnit(buyOrder.getPricePerUnit());

                    TransactionService.getInstance().insertTransactionWithConnection(transaction, con);

                    deleteBuyOrderWithIdWithConnection(buyOrder.getId(), con);

                    numberOfUnits -= buyOrder.getNumberOfUnits();
                    totalPrice += pricePerUnit * buyOrder.getNumberOfUnits();
                } else {
                    buyOrder.setNumberOfUnits(buyOrder.getNumberOfUnits() - numberOfUnits);
                    numberOfUnits = 0;
                    updateBuyOrderWithIdWithConnection(buyOrder.getId(), buyOrder, con);
                    break;
                }
            }
            if (!numberOfUnits.equals(0)) {
                SellOrder sellOrder = new SellOrder();
                sellOrder.setNumberOfUnits(numberOfUnits);
                sellOrder.setCompanyId(company.getId());
                sellOrder.setOwnerId(ownerId);
                sellOrder.setDate(new java.util.Date());
                insertSellOrderWithConnection(sellOrder, con);
            }



            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new SQLException("Exception encountered");
        }


        return "Successful";
    }
}


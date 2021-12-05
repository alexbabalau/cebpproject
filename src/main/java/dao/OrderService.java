package dao;

import exceptions.NegativeBalanceException;
import exceptions.ResourceNotFoundException;
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

    public List<SellOrder> getCompanySellOrders(String companyCode) throws SQLException{
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
            throw e;
        }
        return sellOrders;
    }

    private List<BuyOrder> getBuyOrdersGreaterThanFromCompanyIdForUpdateWithConnection(Integer companyId, Double minPrice, Connection con) throws SQLException{
        String sql = "SELECT * FROM buy_order WHERE company_id = ? AND price_per_unit >= ? ORDER BY price_per_unit DESC FOR UPDATE";
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
            throw e;
        }
        return buyOrders;
    }

    public void deleteBuyOrderWithIdWithConnection(Integer id, Connection connection) throws SQLException{
        String sql = "DELETE FROM buy_order WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void updateBuyOrderWithIdWithConnection(Integer id, BuyOrder buyOrder, Connection connection) throws SQLException{
        String sql = "UPDATE buy_order SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, buyOrder.getNumberOfUnits());
            pstmt.setDouble(2, buyOrder.getPricePerUnit());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteSellOrderWithIdWithConnection(Integer id, Connection connection){
        String sql = "DELETE FROM sell_order WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateSellOrderWithIdWithConnection(Integer id, SellOrder sellOrder, Connection connection) throws SQLException{
        String sql = "UPDATE sell_order SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, sellOrder.getNumberOfUnits());
            pstmt.setDouble(2, sellOrder.getPricePerUnit());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    private SellOrder insertSellOrderWithConnection(SellOrder sellOrder, Connection con) throws SQLException{
        String insertSellOrderSql = "INSERT INTO sell_order(company_id, owner_id, number_of_units, price_per_unit,date)  VALUES(?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = con.prepareStatement(insertSellOrderSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, sellOrder.getCompanyId());
            pstmt.setInt(2, sellOrder.getOwnerId());
            pstmt.setInt(3, sellOrder.getNumberOfUnits());
            pstmt.setDouble(4, sellOrder.getPricePerUnit());
            pstmt.setDate(5, new java.sql.Date(sellOrder.getDate().getTime()));
            Integer insertedCount = pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sellOrder.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating sell order failed, no ID obtained.");
                }
            }
            return sellOrder;
        }
        catch (SQLException ex){
            throw ex;
        }

    }

    private BuyOrder insertBuyOrderWithConnection(BuyOrder buyOrder, Connection con) throws SQLException{
        String insertBuyOrderSql = "INSERT INTO buy_order(company_id, owner_id, number_of_units, price_per_unit,date)  VALUES(?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = con.prepareStatement(insertBuyOrderSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, buyOrder.getCompanyId());
            pstmt.setInt(2, buyOrder.getOwnerId());
            pstmt.setInt(3, buyOrder.getNumberOfUnits());
            pstmt.setDouble(4, buyOrder.getPricePerUnit());
            pstmt.setDate(5, new java.sql.Date(buyOrder.getDate().getTime()));
            Integer insertedCount = pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    buyOrder.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating buy order failed, no ID obtained.");
                }
            }
            return buyOrder;
        }
        catch (SQLException ex){
            throw ex;
        }

    }

    public String addBuyOrder(String companyCode, Integer numberOfUnits, Double pricePerUnit, User user) throws SQLException {

        if(user == null){
            return "Error: not authenticated";
        }

        Integer ownerId = user.getId();
        Connection con = null;

        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setNumberOfUnits(numberOfUnits);
        buyOrder.setOwnerId(ownerId);
        buyOrder.setDate(new java.util.Date());
        buyOrder.setPricePerUnit(pricePerUnit);

        try {
            con = DriverManager
                    .getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);
            //Double totalPrice =
            Company company = CompanyService.getInstance().findByCodeWithConnection(companyCode, con);
            buyOrder.setCompanyId(company.getId());
            List<SellOrder> sellOrders = getSellOrdersLessThanFromCompanyIdForUpdateWithConnection(company.getId(), pricePerUnit, con);
            Integer totalUnits = numberOfUnits;
            Double totalPrice = 0.0;
            for (SellOrder sellOrder : sellOrders) {
                if(sellOrder.getOwnerId().equals(ownerId))
                    continue;
                CompanyShare sellerCompanyShares = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(company.getId(), sellOrder.getOwnerId(), con);
                sellOrder.setNumberOfUnits(Math.min(sellerCompanyShares.getNumberOfUnits(), sellOrder.getNumberOfUnits()));
                if (numberOfUnits >= sellOrder.getNumberOfUnits()) {
                    try{
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), sellOrder.getOwnerId(), -sellOrder.getNumberOfUnits(), con);
                    }
                    catch (NegativeBalanceException ex){
                        continue;
                    }
                    CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), sellOrder.getNumberOfUnits(), con);
                    Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);
                    TransactionService.getInstance().insertTransactionWithConnection(transaction, con);
                    System.out.println(sellOrder.getId());
                    deleteSellOrderWithIdWithConnection(sellOrder.getId(), con);

                    numberOfUnits -= sellOrder.getNumberOfUnits();
                    buyOrder.setNumberOfUnits(numberOfUnits);
                    totalPrice += pricePerUnit * sellOrder.getNumberOfUnits();
                } else {
                    try{
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), sellOrder.getOwnerId(), -numberOfUnits, con);
                    }
                    catch (NegativeBalanceException ex){
                        continue;
                    }
                    CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), numberOfUnits, con);
                    Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);
                    TransactionService.getInstance().insertTransactionWithConnection(transaction, con);
                    sellOrder.setNumberOfUnits(sellOrder.getNumberOfUnits() - numberOfUnits);
                    numberOfUnits = 0;
                    updateSellOrderWithIdWithConnection(sellOrder.getId(), sellOrder, con);
                    break;
                }
            }
            if (!numberOfUnits.equals(0)) {

                buyOrder.setNumberOfUnits(numberOfUnits);
                insertBuyOrderWithConnection(buyOrder, con);
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

    private List<SellOrder> getSellOrdersLessThanFromCompanyIdForUpdateWithConnection(Integer companyId, Double maxPrice, Connection con) throws SQLException {

        String sql = "SELECT * FROM sell_order WHERE company_id = ? AND price_per_unit <= ? ORDER BY price_per_unit";
        List<SellOrder> sellOrders = new ArrayList<>();

        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setInt(1, companyId);
            pstmt.setDouble(2, maxPrice);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                    sellOrders.add(sellOrder);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        return sellOrders;

    }

    public String addSellOrder(String companyCode, Integer numberOfUnits, Double pricePerUnit, User user) throws SQLException {

        if(user == null){
            return "Error: not authenticated";
        }

        Integer ownerId = user.getId();
        Connection con = null;

        SellOrder sellOrder = new SellOrder();
        sellOrder.setNumberOfUnits(numberOfUnits);
        sellOrder.setOwnerId(ownerId);
        sellOrder.setDate(new java.util.Date());
        sellOrder.setPricePerUnit(pricePerUnit);


        try {
            con = DriverManager
                    .getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false);
            Company company = CompanyService.getInstance().findByCodeWithConnection(companyCode, con);
            CompanyShare companyShare = null;
            try{
                companyShare = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(company.getId(), ownerId, con);
            }
            catch (ResourceNotFoundException e){
                return "No shares found";
            }
            if (companyShare.getNumberOfUnits() < numberOfUnits)
                throw new NotEnoughUnitsException("Not enough units to sell");
            List<BuyOrder> buyOrders = getBuyOrdersGreaterThanFromCompanyIdForUpdateWithConnection(company.getId(), pricePerUnit, con);
            Integer totalUnits = numberOfUnits;
            Double totalPrice = 0.0;
            sellOrder.setCompanyId(company.getId());
            for (BuyOrder buyOrder : buyOrders) {
                if(buyOrder.getOwnerId().equals(ownerId))
                    continue;
                CompanyShare buyerCompanyShares = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(company.getId(), buyOrder.getOwnerId(), con);
                buyOrder.setNumberOfUnits(Math.min(buyerCompanyShares.getNumberOfUnits(), buyOrder.getNumberOfUnits()));
                if (numberOfUnits >= buyOrder.getNumberOfUnits()) {

                    try{
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, -buyOrder.getNumberOfUnits(), con);
                    }
                    catch (NegativeBalanceException ex){
                        deleteSellOrderWithIdWithConnection(buyOrder.getId(), con);
                        continue;
                    }
                    CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), buyOrder.getNumberOfUnits(), con);

                    Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);

                    TransactionService.getInstance().insertTransactionWithConnection(transaction, con);

                    deleteBuyOrderWithIdWithConnection(buyOrder.getId(), con);

                    numberOfUnits -= buyOrder.getNumberOfUnits();
                    sellOrder.setNumberOfUnits(numberOfUnits);
                    totalPrice += pricePerUnit * buyOrder.getNumberOfUnits();
                } else {
                    try{
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, -numberOfUnits, con);
                    }
                    catch (NegativeBalanceException ex){
                        continue;
                    }
                    CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), numberOfUnits, con);
                    buyOrder.setNumberOfUnits(buyOrder.getNumberOfUnits() - numberOfUnits);
                    numberOfUnits = 0;
                    updateBuyOrderWithIdWithConnection(buyOrder.getId(), buyOrder, con);
                    break;
                }
            }
            if (!numberOfUnits.equals(0)) {
                sellOrder.setNumberOfUnits(numberOfUnits);
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


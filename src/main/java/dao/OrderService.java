package dao;

import exceptions.NegativeBalanceException;
import exceptions.ResourceNotFoundException;
import models.*;
import server.command.exceptions.NotEnoughUnitsException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class OrderService {

    private static OrderService instance;

    private Semaphore sellMutex = new Semaphore(1, true);
    private Semaphore sellWriteLock = new Semaphore(1, true);

    private Semaphore buyMutex = new Semaphore(1, true);
    private Semaphore buyWriteLock = new Semaphore(1, true);

    private Integer sellReads = 0;
    private Integer buyReads = 0;

    private Map<String, Object> companyLocks = new HashMap<>();

    private OrderService(){
        List<String> codes = CompanyService.getInstance().getCompanyCodes();
        for(String code: codes){
            companyLocks.put(code, new Object());
        }
    }

    public static OrderService getInstance() {
        if(instance == null)
            instance = new OrderService();
        return instance;
    }

    public List<SellOrder> getCompanySellOrders(Connection connection, String companyCode) throws SQLException, InterruptedException{
        List<SellOrder> sellOrders = new ArrayList<>();

        String sql = "SELECT * FROM sell_order " +
                "WHERE id IN (" +
                "   SELECT S.id from sell_order S JOIN Company C ON S.company_id = C.id" +
                "       WHERE C.code = ?)" +
                "ORDER BY price_per_unit;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startSellRead();

            pstmt.setString(1, companyCode);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                    sellOrders.add(sellOrder);
                }
            }
            endSellRead();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (InterruptedException e){
            throw e;
        }
        finally {
            endSellRead();
        }
        return sellOrders;
    }

    private void endSellRead() throws InterruptedException {
        sellMutex.acquire();
        sellReads -= 1;
        if (sellReads == 0)
            sellWriteLock.release();
        sellMutex.release();
    }

    private void startSellRead() throws InterruptedException {
        sellMutex.acquire();
        sellReads += 1;
        if(sellReads == 1)
            sellWriteLock.acquire();
        sellMutex.release();
    }

    public List<BuyOrder> getCompanyBuyOrders(Connection connection, String companyCode) throws SQLException, InterruptedException {
        List<BuyOrder> buyOrders = new ArrayList<>();
        String sql = "SELECT * FROM buy_order " +
                "WHERE id IN (" +
                "   SELECT B.id from buy_order B JOIN Company C ON B.company_id = C.id" +
                "       WHERE C.code = ?)" +
                "ORDER BY price_per_unit;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startBuyRead();
            pstmt.setString(1, companyCode);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    BuyOrder buyOrder = BuyOrder.getBuyOrderFromResultSet(resultSet);
                    buyOrders.add(buyOrder);
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
            endBuyRead();
        }
        return buyOrders;
    }

    private void startBuyRead() throws InterruptedException {
        buyMutex.acquire();
        buyReads += 1;
        if (buyReads == 1)
            buyWriteLock.acquire();
        buyMutex.release();
    }

    public List<SellOrder> getUserSellOrders(Connection connection, String username) throws SQLException, InterruptedException {
        List<SellOrder> sellOrders = new ArrayList<>();
        
        String sql = "SELECT * FROM sell_order " +
                "WHERE id IN (" +
                "   SELECT S.id from sell_order S JOIN User U ON S.owner_id = U.id" +
                "       WHERE U.username = ?)" +
                "ORDER BY price_per_unit;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startSellRead();
            pstmt.setString(1, username);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                    sellOrders.add(sellOrder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }
        finally {
            endSellRead();
        }
        return sellOrders;
    }

    public List<BuyOrder> getUserBuyOrders(Connection connection, String username) throws SQLException, InterruptedException {
        List<BuyOrder> buyOrders = new ArrayList<>();
        String sql = "SELECT * FROM buy_order " +
                "WHERE id IN (" +
                "   SELECT B.id from buy_order B JOIN User U ON B.company_id = U.id" +
                "       WHERE U.username = ?)" +
                "ORDER BY price_per_unit;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startBuyRead();
            pstmt.setString(1, username);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    BuyOrder sellOrder = BuyOrder.getBuyOrderFromResultSet(resultSet);
                    buyOrders.add(sellOrder);
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
            endBuyRead();
        }
        return buyOrders;
    }


    private List<BuyOrder> getBuyOrdersGreaterThanFromCompanyId(Integer companyId, Double minPrice, Connection connection) throws SQLException, InterruptedException {
        String sql = "SELECT * FROM buy_order WHERE company_id = ? AND price_per_unit >= ? ORDER BY price_per_unit DESC";
        List<BuyOrder> buyOrders = new ArrayList<>();

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            startBuyRead();

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
        catch (InterruptedException e){
            throw e;
        }
        finally {
            endBuyRead();
        }
        return buyOrders;
    }

    private void endBuyRead() throws InterruptedException {
        buyMutex.acquire();
        buyReads -= 1;
        if (buyReads == 0)
            buyWriteLock.release();
        buyMutex.release();
    }



    public void updateBuyOrderWithIdWithConnection(Integer id, BuyOrder buyOrder, Connection connection) throws SQLException, InterruptedException{
        String sql = "UPDATE buy_order SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            buyWriteLock.acquire();
            pstmt.setInt(1, buyOrder.getNumberOfUnits());
            pstmt.setDouble(2, buyOrder.getPricePerUnit());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        catch (InterruptedException e){
            throw e;
        }
        finally {
            buyWriteLock.release();
        }
    }

    private String findSellOrderCompanyCodeById(Integer id, Connection connection) throws SQLException, InterruptedException{
        boolean released = true;
        try{
            String sql = "SELECT C.code as code FROM sell_order S JOIN company C " +
                    "WHERE S.id = ?";
            startSellRead();
            released = false;
            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                pstmt.setInt(1, id);
                try(ResultSet resultSet = pstmt.executeQuery()){
                    endSellRead();
                    released = true;
                    if(!resultSet.next())
                        return null;
                    return resultSet.getString("code");
                }
            }
        }
        catch (SQLException | InterruptedException ex){
            throw ex;
        }
        finally {
            if(!released)
                endSellRead();
        }

    }

    private String findBuyOrderCompanyCodeById(Integer id, Connection connection) throws SQLException, InterruptedException{
        boolean released = true;
        try{
            String sql = "SELECT C.code as code FROM buy_order S JOIN company C " +
                    "WHERE S.id = ?";
            startBuyRead();
            released = false;
            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                pstmt.setInt(1, id);
                try(ResultSet resultSet = pstmt.executeQuery()){
                    endBuyRead();
                    released = true;
                    if(!resultSet.next())
                        return null;
                    return resultSet.getString("code");
                }
            }
        }
        catch (SQLException | InterruptedException ex){
            throw ex;
        }
        finally {
            if(!released)
                endBuyRead();
        }

    }



    public void updateSellOrderWithIdWithConnection(Integer id, SellOrder sellOrder, Connection connection) throws SQLException, InterruptedException{
        String sql = "UPDATE sell_order SET number_of_units = ?, price_per_unit = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            sellWriteLock.acquire();
            pstmt.setInt(1, sellOrder.getNumberOfUnits());
            pstmt.setDouble(2, sellOrder.getPricePerUnit());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        catch (InterruptedException e){
            throw e;
        }
        finally {
            sellWriteLock.release();
        }
    }

    private SellOrder insertSellOrder(SellOrder sellOrder, Connection connection) throws SQLException, InterruptedException{
        String insertSellOrderSql = "INSERT INTO sell_order(company_id, owner_id, number_of_units, price_per_unit,date)  VALUES(?, ?, ?, ?, ?);";
        boolean released = true;
        try (PreparedStatement pstmt = connection.prepareStatement(insertSellOrderSql, Statement.RETURN_GENERATED_KEYS)) {
            sellWriteLock.acquire();
            released = false;
            pstmt.setInt(1, sellOrder.getCompanyId());
            pstmt.setInt(2, sellOrder.getOwnerId());
            pstmt.setInt(3, sellOrder.getNumberOfUnits());
            pstmt.setDouble(4, sellOrder.getPricePerUnit());
            pstmt.setTimestamp(5, new java.sql.Timestamp(sellOrder.getDate().getTime()));
            Integer insertedCount = pstmt.executeUpdate();
            sellWriteLock.release();
            released = true;
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
        catch (InterruptedException ex){
            throw ex;
        }
        finally {
            if(!released)
                sellWriteLock.release();
        }

    }

    private BuyOrder insertBuyOrderWithConnection(BuyOrder buyOrder, Connection connection) throws SQLException, InterruptedException{
        String insertBuyOrderSql = "INSERT INTO buy_order(company_id, owner_id, number_of_units, price_per_unit,date)  VALUES(?, ?, ?, ?, ?);";
        boolean released = true;
        try (PreparedStatement pstmt = connection.prepareStatement(insertBuyOrderSql, Statement.RETURN_GENERATED_KEYS)) {
            buyWriteLock.acquire();
            released = false;
            pstmt.setInt(1, buyOrder.getCompanyId());
            pstmt.setInt(2, buyOrder.getOwnerId());
            pstmt.setInt(3, buyOrder.getNumberOfUnits());
            pstmt.setDouble(4, buyOrder.getPricePerUnit());
            pstmt.setTimestamp(5, new java.sql.Timestamp(buyOrder.getDate().getTime()));
            Integer insertedCount = pstmt.executeUpdate();
            buyWriteLock.release();
            released = true;
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    buyOrder.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating buy order failed, no ID obtained.");
                }
            }
            return buyOrder;
        } catch (SQLException ex) {
            throw ex;
        } catch (InterruptedException e) {
            throw e;
        }
        finally {
            buyWriteLock.release();
        }
    }

    public String addBuyOrder(Connection connection, String companyCode, Integer numberOfUnits, Double pricePerUnit, User user) throws SQLException {
        if(!companyLocks.containsKey(companyCode))
            return "Company not found";
        synchronized (companyLocks.get(companyCode)) {
            if (user == null) {
                return "Error: not authenticated";
            }

            Integer ownerId = user.getId();

            BuyOrder buyOrder = new BuyOrder();
            buyOrder.setNumberOfUnits(numberOfUnits);
            buyOrder.setOwnerId(ownerId);
            buyOrder.setDate(new java.util.Date());
            buyOrder.setPricePerUnit(pricePerUnit);

            try {

                connection.setAutoCommit(false);
                //Double totalPrice =
                Company company = CompanyService.getInstance().findByCodeWithConnection(companyCode, connection);
                buyOrder.setCompanyId(company.getId());
                List<SellOrder> sellOrders = getSellOrdersLessThanFromCompanyIdForUpdateWithConnection(company.getId(), pricePerUnit, connection);
                Integer totalUnits = numberOfUnits;
                Double totalPrice = 0.0;
                for (SellOrder sellOrder : sellOrders) {
                    if (sellOrder.getOwnerId().equals(ownerId))
                        continue;
                    CompanyShare sellerCompanyShares = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerId(company.getId(), sellOrder.getOwnerId(), connection);
                    sellOrder.setNumberOfUnits(Math.min(sellerCompanyShares.getNumberOfUnits(), sellOrder.getNumberOfUnits()));
                    if (numberOfUnits >= sellOrder.getNumberOfUnits()) {
                        try {
                            UserService.getInstance().updateMoneyWithId(ownerId, -sellOrder.getPricePerUnit() * sellOrder.getNumberOfUnits(), connection);
                        } catch (NegativeBalanceException ex) {
                            numberOfUnits = 0;
                            break;
                        }
                        try {
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), sellOrder.getOwnerId(), -sellOrder.getNumberOfUnits(), connection);
                        } catch (NegativeBalanceException ex) {
                            UserService.getInstance().updateMoneyWithId(ownerId, sellOrder.getPricePerUnit() * sellOrder.getNumberOfUnits(), connection);
                            deleteSellOrderWithId(sellOrder.getId(), connection);
                            continue;
                        }
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), sellOrder.getNumberOfUnits(), connection);
                        UserService.getInstance().updateMoneyWithId(sellOrder.getOwnerId(), sellOrder.getPricePerUnit() * sellOrder.getNumberOfUnits(), connection);
                        Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);
                        TransactionService.getInstance().insertTransaction(transaction, connection);
                        System.out.println(sellOrder.getId());
                        deleteSellOrderWithId(sellOrder.getId(), connection);

                        numberOfUnits -= sellOrder.getNumberOfUnits();
                        buyOrder.setNumberOfUnits(numberOfUnits);
                        totalPrice += pricePerUnit * sellOrder.getNumberOfUnits();
                    } else {
                        try {
                            UserService.getInstance().updateMoneyWithId(ownerId, -sellOrder.getPricePerUnit() * numberOfUnits, connection);
                        } catch (NegativeBalanceException ex) {
                            numberOfUnits = 0;
                            break;
                        }
                        try {
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), sellOrder.getOwnerId(), -numberOfUnits, connection);
                        } catch (NegativeBalanceException ex) {
                            UserService.getInstance().updateMoneyWithId(ownerId, sellOrder.getPricePerUnit() * numberOfUnits, connection);
                            deleteSellOrderWithId(sellOrder.getId(), connection);
                            continue;
                        }
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), numberOfUnits, connection);
                        UserService.getInstance().updateMoneyWithId(sellOrder.getOwnerId(), sellOrder.getPricePerUnit() * numberOfUnits, connection);
                        Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);
                        TransactionService.getInstance().insertTransaction(transaction, connection);
                        sellOrder.setNumberOfUnits(sellOrder.getNumberOfUnits() - numberOfUnits);
                        numberOfUnits = 0;
                        updateSellOrderWithIdWithConnection(sellOrder.getId(), sellOrder, connection);
                        break;
                    }
                }
                if (!numberOfUnits.equals(0)) {

                    buyOrder.setNumberOfUnits(numberOfUnits);
                    insertBuyOrderWithConnection(buyOrder, connection);
                }


                connection.commit();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                try {
                    if (connection != null)
                        connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                throw new SQLException("Exception encountered");
            }


            return "Successful";
        }
    }

    public void deleteBuyOrderWithId(Integer id, Connection connection) throws SQLException, InterruptedException{
        String code = findBuyOrderCompanyCodeById(id, connection);
        if(code == null){
            throw new ResourceNotFoundException("Buy order not found");
        }
        synchronized (companyLocks.get(code)){
            String sql = "DELETE FROM buy_order WHERE id = ?";
            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                buyWriteLock.acquire();
                System.out.println(pstmt);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            catch (SQLException e){
                e.printStackTrace();
                throw e;
            }
            catch (InterruptedException e){
                throw e;
            }
            finally {
                buyWriteLock.release();
            }
        }
    }

    public void deleteSellOrderWithId(Integer id, Connection connection)throws SQLException, InterruptedException{

        String code = findSellOrderCompanyCodeById(id, connection);
        if(code == null){
            throw new ResourceNotFoundException("Sell order not found");
        }
        synchronized (companyLocks.get(code)){
            String sql = "DELETE FROM sell_order WHERE id = ?";
            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                sellWriteLock.acquire();
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            catch (SQLException e){
                e.printStackTrace();
                throw e;
            }
            catch (InterruptedException e){
                throw e;
            }
            finally {
                sellWriteLock.release();
            }
        }

    }

    private List<SellOrder> getSellOrdersLessThanFromCompanyIdForUpdateWithConnection(Integer companyId, Double maxPrice, Connection connection) throws SQLException, InterruptedException {

        String sql = "SELECT * FROM sell_order WHERE company_id = ? AND price_per_unit <= ? ORDER BY price_per_unit";
        List<SellOrder> sellOrders = new ArrayList<>();
        boolean released = true;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startSellRead();
            released = false;
            pstmt.setInt(1, companyId);
            pstmt.setDouble(2, maxPrice);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                endSellRead();
                released = true;
                while (resultSet.next()) {
                    SellOrder sellOrder = SellOrder.getSellOrderFromResultSet(resultSet);
                    sellOrders.add(sellOrder);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }
        finally {
            if(!released)
                endSellRead();
        }
        return sellOrders;

    }

    public String addSellOrder(Connection connection, String companyCode, Integer numberOfUnits, Double pricePerUnit, User user) throws SQLException {

        if(!companyLocks.containsKey(companyCode))
            return "Company not found";
        synchronized (companyLocks.get(companyCode)){
            if (user == null) {
                return "Error: not authenticated";
            }

            Integer ownerId = user.getId();

            SellOrder sellOrder = new SellOrder();
            sellOrder.setNumberOfUnits(numberOfUnits);
            sellOrder.setOwnerId(ownerId);
            sellOrder.setDate(new java.util.Date());
            sellOrder.setPricePerUnit(pricePerUnit);


            try {

                connection.setAutoCommit(false);
                Company company = CompanyService.getInstance().findByCodeWithConnection(companyCode, connection);
                CompanyShare companyShare = null;

                companyShare = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerId(company.getId(), ownerId, connection);

                if (companyShare == null || companyShare.getNumberOfUnits() < numberOfUnits)
                    throw new NotEnoughUnitsException("Not enough units to sell");
                List<BuyOrder> buyOrders = getBuyOrdersGreaterThanFromCompanyId(company.getId(), pricePerUnit, connection);
                Integer totalUnits = numberOfUnits;
                Double totalPrice = 0.0;
                sellOrder.setCompanyId(company.getId());
                for (BuyOrder buyOrder : buyOrders) {
                    if (buyOrder.getOwnerId().equals(ownerId))
                        continue;
                    //CompanyShare buyerCompanyShares = CompanyShareService.getInstance().getCompanyShareByCompanyIdAndOwnerId(company.getId(), buyOrder.getOwnerId(), connection);
                    //buyOrder.setNumberOfUnits(Math.min(buyerCompanyShares.getNumberOfUnits(), buyOrder.getNumberOfUnits()));
                    if (numberOfUnits >= buyOrder.getNumberOfUnits()) {

                        try {
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, -buyOrder.getNumberOfUnits(), connection);

                        } catch (NegativeBalanceException ex) {
                            numberOfUnits = 0;
                            break;
                        }

                        try{
                            UserService.getInstance().updateMoneyWithId(buyOrder.getOwnerId(), -buyOrder.getNumberOfUnits() * sellOrder.getPricePerUnit(), connection);
                        }
                        catch (NegativeBalanceException ex){
                            deleteBuyOrderWithId(buyOrder.getId(), connection);
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, buyOrder.getNumberOfUnits(), connection);
                            continue;
                        }
                        CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), buyOrder.getNumberOfUnits(), connection);
                        UserService.getInstance().updateMoneyWithId(ownerId, buyOrder.getNumberOfUnits() * sellOrder.getPricePerUnit(), connection);
                        Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);

                        TransactionService.getInstance().insertTransaction(transaction, connection);

                        deleteBuyOrderWithId(buyOrder.getId(), connection);

                        numberOfUnits -= buyOrder.getNumberOfUnits();
                        sellOrder.setNumberOfUnits(numberOfUnits);
                        totalPrice += pricePerUnit * buyOrder.getNumberOfUnits();
                    } else {
                        try {
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, -numberOfUnits, connection);
                        } catch (NegativeBalanceException ex) {
                            numberOfUnits = 0;
                            break;
                        }
                        try{
                            UserService.getInstance().updateMoneyWithId(buyOrder.getOwnerId(), -numberOfUnits * sellOrder.getPricePerUnit(), connection);
                        }
                        catch (NegativeBalanceException ex){
                            CompanyShareService.getInstance().addCompanyShares(company.getId(), ownerId, numberOfUnits, connection);
                            deleteBuyOrderWithId(buyOrder.getId(), connection);
                            continue;
                        }
                        Transaction transaction = Transaction.getTransactionFromBuyAndSellOrder(buyOrder, sellOrder);

                        TransactionService.getInstance().insertTransaction(transaction, connection);

                        CompanyShareService.getInstance().addCompanyShares(company.getId(), buyOrder.getOwnerId(), numberOfUnits, connection);
                        UserService.getInstance().updateMoneyWithId(ownerId, sellOrder.getPricePerUnit() * numberOfUnits, connection);
                        buyOrder.setNumberOfUnits(buyOrder.getNumberOfUnits() - numberOfUnits);
                        numberOfUnits = 0;
                        updateBuyOrderWithIdWithConnection(buyOrder.getId(), buyOrder, connection);
                        break;
                    }
                }
                if (!numberOfUnits.equals(0)) {
                    sellOrder.setNumberOfUnits(numberOfUnits);
                    insertSellOrder(sellOrder, connection);
                }

                connection.commit();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                try {
                    if (connection != null)
                        connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                throw new SQLException("Exception encountered");
            }


            return "Successful";
        }


    }


}


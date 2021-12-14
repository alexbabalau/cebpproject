package dao;

import exceptions.NegativeBalanceException;
import models.CompanyShare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class CompanyShareService {
    private static CompanyShareService instance = new CompanyShareService();

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    private Semaphore mutex = new Semaphore(1, true);
    private Semaphore writeLock = new Semaphore(1, true);
    private Integer numberReads = 0;

    public static CompanyShareService getInstance() {
        return instance;
    }

    private CompanyShareService(){

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

    public CompanyShare getCompanyShareByCompanyIdAndOwnerId(Integer companyId,
                                                             Integer ownerId,
                                                             Connection connection) throws SQLException, InterruptedException{
        String sql = "SELECT * FROM company_share where company_id = ? AND owner_id = ?";
        CompanyShare companyShare = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            startRead();
            pstmt.setInt(1, companyId);
            pstmt.setInt(2, ownerId);
            try(ResultSet resultSet = pstmt.executeQuery()){
                if(!resultSet.next()){
                    endRead();
                    return null;
                }
                companyShare = CompanyShare.getCompanyShareFromResultSet(resultSet);
            }
            endRead();
        }
        catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        return companyShare;
    }

    public void addCompanyShares(Integer companyId, Integer ownerId, Integer numberOfUnits, Connection connection) throws SQLException, InterruptedException{
        try{
            CompanyShare companyShare = getCompanyShareByCompanyIdAndOwnerId(companyId, ownerId, connection);
            String sql = "UPDATE company_share SET number_of_units = number_of_units + ? where company_id = ? AND owner_id = ?";
            if(companyShare == null){
                sql = "INSERT INTO company_share(number_of_units, company_id, owner_id) VALUES(?, ?, ?)";
            }
            if(companyShare != null && companyShare.getNumberOfUnits() + numberOfUnits < 0){
                throw new NegativeBalanceException("Not enough units to sell");
            }


            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                writeLock.acquire();
                pstmt.setInt(1, numberOfUnits);
                pstmt.setInt(2, companyId);
                pstmt.setInt(3, ownerId);
                Integer modifiedValues = pstmt.executeUpdate();
                writeLock.release();
                if(modifiedValues.equals(0)){
                    throw new SQLException("Company Shares adding failed");
                }
            }
        }
        catch (SQLException | InterruptedException ex){
            ex.printStackTrace();
            throw ex;
        }


    }

}

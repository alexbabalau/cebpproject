package dao;

import exceptions.NegativeBalanceException;
import exceptions.ResourceNotFoundException;
import models.CompanyShare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyShareService {
    private static CompanyShareService instance = new CompanyShareService();

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    public static CompanyShareService getInstance() {
        return instance;
    }

    private CompanyShareService(){

    }

    public CompanyShare getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(Integer companyId,
                                                                                    Integer ownerId,
                                                                                    Connection connection) throws SQLException{
        String sql = "SELECT * FROM company_share where company_id = ? AND owner_id = ?";
        CompanyShare companyShare = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            pstmt.setInt(2, ownerId);
            try(ResultSet resultSet = pstmt.executeQuery()){
                if(!resultSet.next()){
                    return null;
                }
                companyShare = CompanyShare.getCompanyShareFromResultSet(resultSet);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return companyShare;
    }

    public void addCompanyShares(Integer companyId, Integer ownerId, Integer numberOfUnits, Connection connection) throws SQLException{
        try{
            CompanyShare companyShare = getCompanyShareByCompanyIdAndOwnerIdForUpdateWithConnection(companyId, ownerId, connection);
            String sql = "UPDATE company_share SET number_of_units = number_of_units + ? where company_id = ? AND owner_id = ?";
            if(companyShare == null){
                sql = "INSERT INTO company_share(number_of_units, company_id, owner_id) VALUES(?, ?, ?)";
            }
            if(companyShare != null && companyShare.getNumberOfUnits() + numberOfUnits < 0){
                throw new NegativeBalanceException("Not enough units to sell");
            }


            try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                pstmt.setInt(1, numberOfUnits);
                pstmt.setInt(2, companyId);
                pstmt.setInt(3, ownerId);
                Integer modifiedValues = pstmt.executeUpdate();
                if(modifiedValues.equals(0)){
                    throw new SQLException("Company Shares adding failed");
                }
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            throw ex;
        }


    }

}

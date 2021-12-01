package dao;

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
        String sql = "SELECT * FROM company_share where company_id = ? AND owner_id = ? FOR UPDATE";
        CompanyShare companyShare = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            pstmt.setInt(2, ownerId);
            try(ResultSet resultSet = pstmt.executeQuery()){
                if(!resultSet.next()){
                    throw new ResourceNotFoundException("Company Shares not found");
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

}

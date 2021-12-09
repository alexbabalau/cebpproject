package dao;

import exceptions.ResourceNotFoundException;
import models.Company;
import models.User;

import java.sql.*;

public class CompanyService {

    private static CompanyService instance = new CompanyService();

    private final String DB_URL = "jdbc:mysql://localhost:3306/stock-market?useSSL=false";
    private final String DB_USER = "stock-market";
    private final String DB_PASS = "password";

    public static CompanyService getInstance() {
        return instance;
    }

    private CompanyService(){

    }

    public String addStocks(User currentUser, Integer numberOfUnits){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            connection.setAutoCommit(false);
            String companyCode = currentUser.getUsername();
            Company company = null;
            try{
                company = findByCodeWithConnection(companyCode, connection);
            }
            catch (ResourceNotFoundException e){
                return "Error " + e.getMessage();
            }
            CompanyShareService.getInstance().addCompanyShares(company.getId(), currentUser.getId(), numberOfUnits, connection);
            connection.commit();
        }
        catch (SQLException | InterruptedException e){
            try{
                connection.rollback();
            }
            catch (SQLException e1){
                return "Exception encountered: " + e1.getMessage();
            }
            return "Exception encountered: " + e.getMessage();
        }
        return "Successful";
    }

    public Company findByCodeWithConnection(String code, Connection connection){
        String sql = "SELECT * FROM company where code = ?";
        Company company = null;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try(ResultSet resultSet = pstmt.executeQuery()){
                if(!resultSet.next()){
                    throw new ResourceNotFoundException("Company not found");
                }
                company = Company.getCompanyFromResultSet(resultSet);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return company;
    }

    public Company findByCode(String code){
        Company company = null;
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            company = findByCodeWithConnection(code, connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } ;
        return company;
    }

}

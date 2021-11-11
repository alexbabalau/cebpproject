package dao;

import exceptions.ResourceNotFoundException;
import models.Company;

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

    public Company findByCodeWithConnection(String code, Connection connection){
        String sql = "SELECT * FROM company where id = ?";
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

package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Company extends VersionedEntity{

    private Integer id;
    private String code;
    private String name;
    private String sector;

    public Company(Integer version, Integer id, String code, String name, String sector) {
        super(version);
        this.id = id;
        this.code = code;
        this.name = name;
        this.sector = sector;
    }

    public Company(){

    }

    public static Company getCompanyFromResultSet(ResultSet resultSet) throws SQLException {
        Company company = new Company();
        company.setCode(resultSet.getString("code"));
        company.setName(resultSet.getString("name"));
        company.setSector(resultSet.getString("sector"));
        company.setId(resultSet.getInt("id"));
        company.setVersion(resultSet.getInt("version"));
        return company;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

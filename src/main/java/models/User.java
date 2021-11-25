package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends VersionedEntity{
    private Integer id;
    private String username;
    private Double amount;

    public User(Integer version, Integer id, String username, Double amount) {
        super(version);
        this.id = id;
        this.username = username;
        this.amount = amount;
    }

    public static User getBuyOrderFromResultSet(ResultSet resultSet) throws SQLException {
        User user = null;

        user.setId(resultSet.getInt("id"));
        user.setAmount(resultSet.getDouble("amount"));
        user.setUsername(resultSet.getString("username"));

        return user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

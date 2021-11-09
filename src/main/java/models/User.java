package models;

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

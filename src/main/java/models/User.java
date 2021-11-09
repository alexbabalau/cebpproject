package models;

public class User extends VersionedEntity{
    private Integer id;
    private String username;

    public User(Integer version, Integer id, String username) {
        super(version);
        this.id = id;
        this.username = username;
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

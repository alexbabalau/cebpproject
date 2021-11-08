package models;

public class Company {

    private Integer id;
    private String code;
    private String name;
    private String sector;

    public Company(Integer id, String code, String name, String sector) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sector = sector;
    }

    public Company(){

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

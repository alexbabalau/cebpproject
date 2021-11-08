package models;

public class CompanyShare {

    private Integer id;

    private String owner;

    private Integer numberOfUnits;

    private Integer companyId;

    public CompanyShare(Integer id, String owner, Integer companyId, Integer numberOfUnits) {
        this.id = id;
        this.owner = owner;
        this.companyId = companyId;
        this.numberOfUnits = numberOfUnits;
    }

    public CompanyShare(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }
}

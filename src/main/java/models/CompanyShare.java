package models;

public class CompanyShare extends VersionedEntity{

    private Integer id;

    private Integer ownerId;

    private Integer numberOfUnits;

    private Integer companyId;

    public CompanyShare(Integer version, Integer id, Integer ownerId, Integer companyId, Integer numberOfUnits) {
        super(version);
        this.id = id;
        this.ownerId = ownerId;
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

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }
}

package models;

public class Transaction {
    private Integer id;

    private String buyer;

    private String seller;

    private Integer companyId;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    public Transaction(Integer id, String buyer, String seller, Integer companyId, Integer numberOfUnits, Double pricePerUnit) {
        this.id = id;
        this.buyer = buyer;
        this.seller = seller;
        this.companyId = companyId;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
    }

    public Transaction(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}

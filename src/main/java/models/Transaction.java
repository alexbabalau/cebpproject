package models;

public class Transaction {
    private String buyer;

    private String seller;

    private String companyCode;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    public Transaction(String buyer, String seller, String companyCode, Integer numberOfUnits, Double pricePerUnit) {
        this.buyer = buyer;
        this.seller = seller;
        this.companyCode = companyCode;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
    }

    public Transaction(){

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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

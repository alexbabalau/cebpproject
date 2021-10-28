package models;

import java.util.Date;

public class BuyOrder {

    private String companyCode;

    private String owner;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    private Date date;

    public BuyOrder(String companyCode, String owner, Integer numberOfUnits, Double pricePerUnit, Date date) {
        this.companyCode = companyCode;
        this.owner = owner;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
    }

    public BuyOrder(){

    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public Date getDate(){
        return this.date;
    }
}

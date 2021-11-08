package models;

import java.util.Date;

public class BuyOrder {

    private Integer id;

    private Integer companyId;

    private String owner;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    private Date date;

    public BuyOrder(Integer id, Integer companyId, String owner, Integer numberOfUnits, Double pricePerUnit, Date date) {
        this.id = id;
        this.companyId = companyId;
        this.owner = owner;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
    }

    public BuyOrder(){

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

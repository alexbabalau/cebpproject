package models;

import java.util.Date;

public class Transaction extends VersionedEntity{
    private Integer id;

    private Integer buyerId;

    private Integer sellerId;

    private Integer companyId;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    private Date date;

    public Transaction(Integer version, Integer id, Integer buyerId, Integer sellerId, Integer companyId, Integer numberOfUnits, Double pricePerUnit, Date date) {
        super(version);
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.companyId = companyId;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
    }

    public Transaction(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

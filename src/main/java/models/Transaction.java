package models;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static Transaction getTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setDate(resultSet.getDate("date"));
        transaction.setNumberOfUnits(resultSet.getInt("number_of_units"));
        transaction.setPricePerUnit(resultSet.getDouble("price_per_unit"));
        transaction.setCompanyId(resultSet.getInt("company_id"));
        transaction.setSellerId(resultSet.getInt("seller_id"));
        transaction.setBuyerId(resultSet.getInt("buyer_id"));

        return transaction;
    }

    public static Transaction getTransactionFromBuyAndSellOrder(BuyOrder buyOrder, SellOrder sellOrder){
        Transaction transaction = new Transaction();
        transaction.setBuyerId(buyOrder.getOwnerId());
        transaction.setSellerId(sellOrder.getOwnerId());
        transaction.setDate(new java.util.Date(System.currentTimeMillis()));
        System.out.println(transaction.getDate());
        transaction.setCompanyId(buyOrder.getCompanyId());
        transaction.setNumberOfUnits(Math.min(buyOrder.getNumberOfUnits(), sellOrder.getNumberOfUnits()));
        transaction.setPricePerUnit(Math.min(buyOrder.getPricePerUnit(), sellOrder.getPricePerUnit()));

        return transaction;
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

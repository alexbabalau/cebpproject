package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class BuyOrder extends VersionedEntity{

    private Integer id;

    private Integer companyId;

    private Integer ownerId;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    private Date date;

    public BuyOrder(Integer version, Integer id, Integer companyId, Integer ownerId, Integer numberOfUnits, Double pricePerUnit, Date date) {
        super(version);
        this.id = id;
        this.companyId = companyId;
        this.ownerId = ownerId;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
    }

    public BuyOrder(){

    }

    public static BuyOrder getBuyOrderFromResultSet(ResultSet resultSet) throws SQLException {
        BuyOrder buyOrder = new BuyOrder();

        buyOrder.setId(resultSet.getInt("id"));
        buyOrder.setCompanyId(resultSet.getInt("company_id"));
        buyOrder.setDate(resultSet.getDate("date"));
        buyOrder.setOwnerId(resultSet.getInt("owner_id"));
        buyOrder.setNumberOfUnits(resultSet.getInt("number_of_units"));
        buyOrder.setPricePerUnit(resultSet.getDouble("price_per_unit"));

        return buyOrder;
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

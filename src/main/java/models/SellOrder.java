package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SellOrder extends VersionedEntity{

    private Integer id;

    private Integer companyId;

    private Integer ownerId;

    private Integer numberOfUnits;

    private Double pricePerUnit;

    private Date date;

    public SellOrder(Integer version, Integer id, Integer companyId, Integer ownerId, Integer numberOfUnits, Double pricePerUnit, Date date) {
        super(version);
        this.id = id;
        this.companyId = companyId;
        this.ownerId = ownerId;
        this.numberOfUnits = numberOfUnits;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
    }

    public SellOrder(){

    }

    public static SellOrder getSellOrderFromResultSet(ResultSet resultSet) throws SQLException {
        SellOrder sellOrder = new SellOrder();
        sellOrder.setId(resultSet.getInt("id"));
        sellOrder.setCompanyId(resultSet.getInt("company_id"));
        sellOrder.setDate(resultSet.getDate("date"));
        sellOrder.setOwnerId(resultSet.getInt("owner_id"));
        sellOrder.setNumberOfUnits(resultSet.getInt("number_of_units"));
        sellOrder.setPricePerUnit(resultSet.getDouble("price_per_unit"));
        sellOrder.setVersion(resultSet.getInt("version"));

        return sellOrder;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SellOrder{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", ownerId=" + ownerId +
                ", numberOfUnits=" + numberOfUnits +
                ", pricePerUnit=" + pricePerUnit +
                ", date=" + date +
                '}';
    }
}

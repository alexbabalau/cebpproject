package models.transientModels;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockPrice {
    private String companyCode;
    private Double price;

    public StockPrice(){

    }

    public StockPrice(String companyCode, Double price){
        this.companyCode = companyCode;
        this.price = price;
    }

    public static StockPrice getStockPriceFromResultSet(ResultSet resultSet) throws SQLException {
        StockPrice stockPrice = new StockPrice();

        stockPrice.setPrice(resultSet.getDouble("price_per_unit"));
        stockPrice.setCompanyCode(resultSet.getString("company_code"));

        return stockPrice;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

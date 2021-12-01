package models.transientModels;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockPrice {
    private String companyName;
    private Double price;

    public StockPrice(){

    }

    public StockPrice(String companyName, Double price){
        this.companyName = companyName;
        this.price = price;
    }

    public static StockPrice getStockPriceFromResultSet(ResultSet resultSet) throws SQLException {
        StockPrice stockPrice = new StockPrice();

        stockPrice.setPrice(resultSet.getDouble("price_per_unit"));
        stockPrice.setCompanyName(resultSet.getString("company_name"));

        return stockPrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

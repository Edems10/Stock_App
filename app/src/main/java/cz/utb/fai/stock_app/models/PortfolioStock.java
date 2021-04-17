package cz.utb.fai.stock_app.models;

public class PortfolioStock {


    public PortfolioStock(String ticker, Integer amount, Double averagePrice) {
        this.ticker = ticker;
        this.amount = amount;
        this.averagePrice = averagePrice;
    }

    public String getTicker() {
        return ticker;
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    private String ticker;
    private Integer amount;
    private Double averagePrice;
}

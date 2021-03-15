package cz.utb.fai.stock_app.Models;

public class Portfolio {


    public Portfolio(String ticker, Integer amount, Double avaragePrice) {
        this.ticker = ticker;
        this.amount = amount;
        this.avaragePrice = avaragePrice;
    }

    public String getTicker() {
        return ticker;
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getAvaragePrice() {
        return avaragePrice;
    }

    private String ticker;
    private Integer amount;
    private Double avaragePrice;
}

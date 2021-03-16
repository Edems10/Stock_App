package cz.utb.fai.stock_app.Models;

public class PortfolioMoney {
    public PortfolioMoney(Double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private Double amount;
    private String currency;
}

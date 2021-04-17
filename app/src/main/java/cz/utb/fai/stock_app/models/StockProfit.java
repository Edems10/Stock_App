package cz.utb.fai.stock_app.models;

public class StockProfit {
    public StockProfit(String symbol, int amount, double averagePrice, double currentPrice, float percentageChange, double profit, double value) {
        this.symbol = symbol;
        this.amount = amount;
        this.averagePrice = averagePrice;
        this.currentPrice = currentPrice;
        this.percentageChange = percentageChange;
        this.profit = profit;
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getAmount() {
        return amount;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public float getPercentageChange() {
        return percentageChange;
    }

    public double getProfit() {
        return profit;
    }

    public double getValue() {
        return value;
    }

    private final String symbol;
    private final int amount;
    private final double averagePrice;
    private final double currentPrice;
    private final float percentageChange;
    private final double profit;
    private final double value;
}

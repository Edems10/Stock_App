package cz.utb.fai.stock_app.models;

public class Prediction {

    private double price;
    private String symbol;
    private double mean_ab_er;
    private double accuracy;
    private String date;


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getMean_ab_er() {
        return mean_ab_er;
    }

    public void setMean_ab_er(double mean_ab_er) {
        this.mean_ab_er = mean_ab_er;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

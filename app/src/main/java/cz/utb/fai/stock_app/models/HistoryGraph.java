package cz.utb.fai.stock_app.models;

import java.util.ArrayList;

public class HistoryGraph {
    private ArrayList<Double> price;
    private String symbol;

    public ArrayList<Double> getPrice() {
        return price;
    }

    public void setPrice(ArrayList<Double> price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

package cz.utb.fai.stock_app.Models;

import cz.utb.fai.stock_app.Enums.Trade;

public class History {


    public History(String date, String name, String price, String amount, Trade operation) {
        this.date=date;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.operation=operation;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getAmount() {
        return amount;
    }

    public Trade getOperation() {
        return operation;
    }


    public String getDate() {
        return date;
    }
    String date;
    String name;
    String price;
    String amount;
    Trade operation ;

}

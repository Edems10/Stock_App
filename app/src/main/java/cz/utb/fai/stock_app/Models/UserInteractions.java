package cz.utb.fai.stock_app.Models;

public class UserInteractions {


    public UserInteractions(String date,String nameOfSymbol, String priceOfSymbol, String amount,String operation) {
        this.date=date;
        this.nameOfSymbol = nameOfSymbol;
        this.priceOfSymbol = priceOfSymbol;
        this.amount = amount;
        this.operation=operation;
    }

    public String getNameOfSymbol() {
        return nameOfSymbol;
    }

    public String getPriceOfSymbol() {
        return priceOfSymbol;
    }

    public String getAmount() {
        return amount;
    }

    public String getOperation() {
        return operation;
    }


    public String getDate() {
        return date;
    }
    String date;
    String nameOfSymbol;
    String priceOfSymbol;
    String amount;
    String operation ;

}

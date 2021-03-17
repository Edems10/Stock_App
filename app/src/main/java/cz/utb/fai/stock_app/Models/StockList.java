package cz.utb.fai.stock_app.Models;

import java.util.ArrayList;
import java.util.List;

public class StockList {

    private static StockList instance;

    private StockList(){}

    public static synchronized StockList getInstance(){
        if(instance == null){
            instance = new StockList();
        }
        return instance;
    }
    private List<Stock> CurrentStocks;


    public List<Stock> getCurrentStocks() {
        return CurrentStocks;
    }

    public void setCurrentStocks(Stock stock) {
        if(CurrentStocks==null)
        {
            CurrentStocks= new ArrayList<>();
        }

        CurrentStocks.add(stock);
    }
}


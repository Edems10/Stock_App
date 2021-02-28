package cz.utb.fai.stock_app.ui.Stocks;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.Models.Stock;

public class StockViewModel extends ViewModel implements Serializable {


//    public List<Stock> getStockList() {
//        return stockList;
//    }

    public boolean setStockList(Stock stock) {
        boolean exists=false;
        for(int i =0;i<stockList.size();i++)
        {
            if(stockList.get(i).Symbol.equals(stock.Symbol))
                exists=true;
        }
        if(!exists) {
            this.stockList.add(stock);
            return true;
        }
        return false;
    }

    ArrayList<Stock> stockList = new ArrayList<>();
    public StockViewModel() {

    }

}
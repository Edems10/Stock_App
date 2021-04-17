package cz.utb.fai.stock_app.ui.Stocks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.fileH.FileHelper;
import cz.utb.fai.stock_app.models.PortfolioStock;
import cz.utb.fai.stock_app.models.Stock;
import cz.utb.fai.stock_app.repo.StockRepository;

public class StockViewModel extends ViewModel implements Serializable {


    private MutableLiveData<List<Stock>> mStock;
    private StockRepository stockRepository;

    public void init (Context context)
    {
        if(mStock!=null)
        {
            return;
        }
        try {
            FileHelper fileHelper =new FileHelper(context.getApplicationContext());
            ArrayList<PortfolioStock> portfolioStocks;
            ArrayList<String> listOfSymbols = new ArrayList<>();
            portfolioStocks=fileHelper.loadFromPortfolio();
            if(portfolioStocks!=null) {
                for (int i = 0; i < portfolioStocks.size(); i++) {
                    listOfSymbols.add(portfolioStocks.get(i).getTicker());
                }
            }
            stockRepository = StockRepository.getInstance();
            mStock=stockRepository.getStocksAdd(context,listOfSymbols);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewStock(final String stock,Context context)
    {
        stockRepository.GetSymbolBasicInfo(stock,context);
        mStock=stockRepository.getStocks();
    }

    public LiveData<List<Stock>> getStocks()
    {
        return mStock;
    }
}
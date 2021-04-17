package cz.utb.fai.stock_app.ui.Personal;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.fileH.FileHelper;
import cz.utb.fai.stock_app.models.History;
import cz.utb.fai.stock_app.models.PortfolioMoney;
import cz.utb.fai.stock_app.models.PortfolioStock;
import cz.utb.fai.stock_app.models.Stock;
import cz.utb.fai.stock_app.models.StockProfit;
import cz.utb.fai.stock_app.repo.StockRepository;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class PersonalViewModel extends ViewModel {

    private List<History> historyList = new ArrayList<>();
    private MutableLiveData<List<Stock>> mStock;
    private ArrayList<PortfolioStock> portfolioStocks;
    private List<StockProfit> stockProfits = new ArrayList<>();
    private PortfolioMoney portfolioMoney;
    private double accountValue;
    private String currency;

    public void init (Context context) throws IOException {
        FileHelper fileHelper = new FileHelper(context.getApplicationContext());
        StockRepository stockRepository = StockRepository.getInstance();
        mStock= stockRepository.getStocks();
        portfolioStocks = fileHelper.loadFromPortfolio();
        historyList = fileHelper.loadFromFileUserInteractions();
        portfolioMoney = fileHelper.loadCurrentMoney();
        accountValue=accountValue();
        currency = portfolioMoney.getCurrency();
    }

    public void pieTooltip(Entry e, Context context, View view){
        String symbol = "Ticker:", amount = "Amount:", averagePrice = "Avg Cost:", percentageChange = "Change:",
                value = "Value:", profit = "Gain:", currentValue = "Price:", percent = "%";
        PieEntry pieEntry = (PieEntry) e;

        if (portfolioMoney != null) {
            if (pieEntry.getLabel().equals("CASH")) {
                symbol = String.format("%-15s$%s", symbol, portfolioMoney.getCurrency());
                amount = String.format("%-15s%.2f", amount, portfolioMoney.getAmount());
            } else {
                for (int i = 0; i < stockProfits.size(); i++) {
                    StockProfit stockProfit = stockProfits.get(i);
                    if (pieEntry.getLabel().equals(stockProfit.getSymbol())) {

                        symbol = String.format("%-15s$%s", symbol, stockProfit.getSymbol());
                        amount = String.format("%-15s%d", amount, stockProfit.getAmount());
                        averagePrice = String.format("%-15s%.2f", averagePrice, stockProfit.getAveragePrice());
                        percentageChange = String.format("%-15s%.2f%s", percentageChange, stockProfit.getPercentageChange(), percent);
                        currentValue = String.format("%-15s%.2f", currentValue, stockProfit.getCurrentPrice());
                        profit = String.format("%-15s%.2f$", profit, stockProfit.getProfit());
                        value = String.format("%-15s%.2f$", value, stockProfit.getValue());
                        break;
                    }
                }
            }
        }
        new SimpleTooltip.Builder(context)
                .anchorView(view)
                .text(symbol + System.lineSeparator() +
                        amount + System.lineSeparator() +
                        averagePrice + System.lineSeparator() +
                        currentValue + System.lineSeparator() +
                        percentageChange + System.lineSeparator() +
                        profit + System.lineSeparator() +
                        value)
                .gravity(Gravity.CENTER)
                .onDismissListener(new SimpleTooltip.OnDismissListener() {
                    @Override
                    public void onDismiss(SimpleTooltip tooltip) {

                    }
                })
                .onShowListener(new SimpleTooltip.OnShowListener() {
                    @Override
                    public void onShow(SimpleTooltip tooltip) {

                    }
                })
                .build()
                .show();


    }
    public ArrayList<PortfolioStock> getPortfolioStocks()
    {
        return portfolioStocks;
    }
    public List<History> getHistory()
    {
        return historyList;
    }
    public double accountValue() {
        if(portfolioStocks!=null) {
            for (int i = 0; i < portfolioStocks.size(); i++) {
                calculateProfit( mStock, portfolioStocks.get(i));
            }
        }
        double accountValue = 0;
        for (int i = 0; i < stockProfits.size(); i++) {
            accountValue += stockProfits.get(i).getValue();
        }
        return accountValue + portfolioMoney.getAmount();
    }

    public String getCurrency() {
        return currency;
    }

    public PortfolioMoney getCurrentMoney() throws IOException {
        return portfolioMoney;
    }

    public double getAccountValue()
    {
        return accountValue;
    }

    private void calculateProfit(@NotNull MutableLiveData<List<Stock>> currentStocks, PortfolioStock portfolioStock) {
        for (int i = 0; i < currentStocks.getValue().size(); i++) {
            Stock stock = currentStocks.getValue().get(i);

            if (portfolioStock.getTicker().equals(stock.getSymbol())) {
                float percentageChange = (float) ((stock.getPrice() - portfolioStock.getAveragePrice()) / stock.getPrice()) * 100;
                double profit = (stock.getPrice() * portfolioStock.getAmount()) - (portfolioStock.getAveragePrice() * portfolioStock.getAmount());
                double value = profit + (portfolioStock.getAveragePrice() * portfolioStock.getAmount());
                StockProfit stockProfit = new StockProfit(stock.getSymbol(),
                        portfolioStock.getAmount(),
                        portfolioStock.getAveragePrice(),
                        stock.getPrice(), percentageChange,
                        profit,
                        value);
                stockProfits.add(stockProfit);
                return;
            }
        }
    }
    public LiveData<List<Stock>> getStocks()
    {
        return mStock;
    }




}

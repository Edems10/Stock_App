package cz.utb.fai.stock_app.ui.Personal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.utb.fai.stock_app.FileHelper;
import cz.utb.fai.stock_app.Models.PortfolioMoney;
import cz.utb.fai.stock_app.Models.PortfolioStock;
import cz.utb.fai.stock_app.Models.StockList;
import cz.utb.fai.stock_app.Models.StockProfit;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.Models.Stock;
import cz.utb.fai.stock_app.Models.History;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class PersonalFragment extends Fragment implements View.OnClickListener {

    FileHelper fileHelper;
    Context context;
    ArrayList<String> itemsForListViewHistory = new ArrayList<>();
    ListView listViewHistory;
    TextView textViewDashboard;
    ArrayAdapter<String> adapter;
    List<History> historyList = new ArrayList<>();
    StockList stockList;
    PieChart pieChart;
    ArrayList<PortfolioStock> portfolioStocks;
    List<StockProfit> stockProfits = new ArrayList<>();
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal, container, false);

        initUI(view);
        initModels();

        try {
            loadFiles();
            createPortfolioChart();
            setTextViewDashboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initHistory();
        return view;
    }

    private void setTextViewDashboard() throws IOException {

        @SuppressLint("DefaultLocale") String format = String.format("%.2f%s", accountValue(),
                fileHelper.loadCurrentMoney().getCurrency());
        textViewDashboard.setText(format);
    }

    private void loadFiles() throws IOException {
        portfolioStocks = fileHelper.loadFromPortfolio();
        historyList = fileHelper.loadFromFileUserInteractions();
    }

    private void initUI(View view) {
        context = getContext();
        textViewDashboard = view.findViewById(R.id.text_dashboard);
        listViewHistory = view.findViewById(R.id.listViewHistory);
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setOnChartValueSelectedListener(new pieChartOnChartValueSelectedListener());
    }

    private void initModels() {
        fileHelper = new FileHelper();
        stockList = StockList.getInstance();
    }

    private void initHistory() {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, itemsForListViewHistory);
        listViewHistory.setAdapter(adapter);
        for (int i = historyList.size() - 1; i >= 0; i--) {
            History interactions = historyList.get(i);
            itemsForListViewHistory.add(interactions.getDate() + "  "
                    + interactions.getOperation() + " "
                    + interactions.getAmount() + "  $"
                    + interactions.getName() + " for "
                    + interactions.getPrice() + "$");

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

    }


    //shows value after clicking on pie chart
    private class pieChartOnChartValueSelectedListener implements OnChartValueSelectedListener {

        @SuppressLint("DefaultLocale")
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            String symbol = "Ticker:", amount = "Amount:", averagePrice = "Avg Cost:", percentageChange = "Change:",
                    value = "Value:", profit = "Gain:", currentValue = "Price:", percent = "%";
            PieEntry pieEntry = (PieEntry) e;
            PortfolioMoney portfolioMoney = null;
            try {
                portfolioMoney = fileHelper.loadCurrentMoney();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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

        @Override
        public void onNothingSelected() {

        }
    }


    private void initPieChart() {
        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);

        //disabling the legend
        pieChart.getLegend().setEnabled(false);
        //remove the description label on the lower left corner, default true if not set
        pieChart.getDescription().setEnabled(false);

        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0);

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
        pieChart.animateY(1400, Easing.EaseOutQuad);
        //setting the color of the hole in the middle, default white
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);

    }


    private void createPortfolioChart() throws IOException {
        initPieChart();

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        PortfolioMoney portfolioMoney = fileHelper.loadCurrentMoney();
        String label = "";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("CASH", (int) Math.round(portfolioMoney.getAmount()));

        if(portfolioStocks!=null)
        {
        for (int i = 0; i < portfolioStocks.size(); i++) {
            if(portfolioStocks.get(i).getAmount()>0) {
                typeAmountMap.put(
                        portfolioStocks.get(i).getTicker(),
                        (portfolioStocks.get(i).getAmount()) *
                                (int) Math.round(portfolioStocks.get(i).getAveragePrice()));
            }
        }
        }

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));

        //input data and fit data into pie chart entry
        for (String type : typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        //setting text size of the value
        pieDataSet.setValueTextSize(20f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);

        pieChart.setData(pieData);
        pieChart.invalidate();


    }

    private double accountValue() throws IOException {

        List<Stock> currentStocks = stockList.getCurrentStocks();

        if(portfolioStocks!=null) {
            for (int i = 0; i < portfolioStocks.size(); i++) {
                calculateProfit(currentStocks, portfolioStocks.get(i));
            }

        }
        double accountValue = 0;
        for (int i = 0; i < stockProfits.size(); i++) {
            accountValue += stockProfits.get(i).getValue();
        }
        return accountValue + fileHelper.loadCurrentMoney().getAmount();

    }

    private void calculateProfit(@NotNull List<Stock> currentStocks, PortfolioStock portfolioStock) {
       if(currentStocks!=null) {
           for (int i = 0; i < currentStocks.size(); i++) {
               Stock stock = currentStocks.get(i);

               if (portfolioStock.getTicker().equals(stock.Symbol)) {
                   float percentageChange = (float) ((stock.Price - portfolioStock.getAveragePrice()) / stock.Price) * 100;
                   //zmena here
                   double profit = (stock.Price * portfolioStock.getAmount()) - (portfolioStock.getAveragePrice() * portfolioStock.getAmount());
                   double value = profit + (portfolioStock.getAveragePrice() * portfolioStock.getAmount());
                   StockProfit stockProfit = new StockProfit(stock.Symbol,
                           portfolioStock.getAmount(),
                           portfolioStock.getAveragePrice(),
                           stock.Price, percentageChange,
                           profit,
                           value);
                   stockProfits.add(stockProfit);
                   return;
               }
           }
       }
    }

}
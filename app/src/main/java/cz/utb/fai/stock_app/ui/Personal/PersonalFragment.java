package cz.utb.fai.stock_app.ui.Personal;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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

public class PersonalFragment extends Fragment {

    FileHelper fileHelper;
    Context context;
    ArrayList<String> itemsForListViewHistory = new ArrayList<>();
    ListView listViewHistory;
    TextView textViewDashboard, textViewDetails;
    ArrayAdapter<String> adapter;
    List<History> historyList = new ArrayList();
    StockList stockList = StockList.getInstance();
    PieChart pieChart;
    ArrayList<PortfolioStock> portfolioStocks;
    List<StockProfit> stockProfits = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        textViewDashboard = view.findViewById(R.id.text_dashboard);
        listViewHistory = view.findViewById(R.id.listViewHistory);
        textViewDetails = view.findViewById(R.id.textdetails);
        context = getContext();
        fileHelper = new FileHelper();
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setOnChartValueSelectedListener(new pieChartOnChartValueSelectedListener());

        try {
            portfolioStocks = fileHelper.loadFromPortfolio();
            historyList = fileHelper.loadFromFileUserInteractions();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, itemsForListViewHistory);
        listViewHistory.setAdapter(adapter);
        for (int i = historyList.size() - 1; i >= 0; i--) {
            History interactions = historyList.get(i);
            itemsForListViewHistory.add(interactions.getDate() + "  " + interactions.getOperation() + " " + interactions.getAmount() + "  $" + interactions.getName() + " for " + interactions.getPrice() + "$");

        }
        adapter.notifyDataSetChanged();

        try {
            createPortfolioChart();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            String formater = String.format("Current Value: %.2f%s",accountValue(),fileHelper.loadCurrentMoney().getCurrency());
            textViewDashboard.setText(formater);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }


    private class pieChartOnChartValueSelectedListener implements OnChartValueSelectedListener {

        //todo dodelat po kliknuti aby se ukazal detail stock
        @Override
        public void onValueSelected(Entry e, Highlight h) {
//            PieEntry pieEntry = (PieEntry) e;
//            int profit = 0;
//            for (int i = 0; i < portfolioStocks.size(); i++) {
//                PortfolioStock portfolioStock = portfolioStocks.get(i);
//                if (pieEntry.getLabel() == portfolioStock.getSymbol()) {
//                    for (int j = 0; j < stocks.size(); j++) {
//                    }
//                    profit = (portfolioStock.getAveragePrice() * portfolioStock.getAmount()) *
//                }
//            }

        }

        @Override
        public void onNothingSelected() {

        }
    }


    private void initPieChart() {
        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);

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

        for (int i = 0; i < portfolioStocks.size(); i++) {
            typeAmountMap.put(
                    portfolioStocks.get(i).getTicker(),
                    (portfolioStocks.get(i).getAmount()) * (int) Math.round(portfolioStocks.get(i).getAveragePrice()));

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

        for(int i=0;i<portfolioStocks.size();i++)
        {
            calculateProfit(currentStocks,portfolioStocks.get(i));
        }


        double accountValue=0;
        for(int i=0;i<stockProfits.size();i++)
        {
            accountValue+=stockProfits.get(i).getValue();
        }
        return accountValue+fileHelper.loadCurrentMoney().getAmount();

    }

    private void calculateProfit(List<Stock> currentStocks,PortfolioStock portfolioStock)
    {
        for (int i = 0; i < currentStocks.size(); i++) {
            Stock stock = currentStocks.get(i);


            if(portfolioStock.getTicker().equals(stock.Symbol))
            {
                float percentageChange= (float) ((portfolioStock.getAveragePrice()-stock.Price)/stock.Price)*100;
                double profit=(portfolioStock.getAveragePrice()*portfolioStock.getAmount())-(stock.Price*portfolioStock.getAmount());
                double value = profit+(portfolioStock.getAveragePrice()*portfolioStock.getAmount());
                StockProfit stockProfit = new StockProfit(stock.Symbol,portfolioStock.getAmount(),portfolioStock.getAveragePrice(),stock.Price,percentageChange,profit, value);
                stockProfits.add(stockProfit);
                return;
            }
        }
    }

}
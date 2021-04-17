package cz.utb.fai.stock_app.ui.Personal;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProviders;


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

import cz.utb.fai.stock_app.models.History;
import cz.utb.fai.stock_app.models.PortfolioMoney;
import cz.utb.fai.stock_app.models.PortfolioStock;
import cz.utb.fai.stock_app.R;

public class PersonalFragment extends Fragment  {

    private Context context;
    private ArrayList<String> itemsListViewHistory = new ArrayList<>();
    private ListView listViewHistory;
    private TextView textViewDashboard;
    private ArrayAdapter<String> adapter;
    private PieChart pieChart;
    private View view;
    private PersonalViewModel personalViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal, container, false);
        personalViewModel = ViewModelProviders.of(this).get(PersonalViewModel.class);
        try {
            personalViewModel.init(view.getContext());
            initUI(view);
            createPortfolioChart();
            setTextViewDashboard();
            initHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setTextViewDashboard() throws IOException {
        @SuppressLint("DefaultLocale") String format = String.format("%.2f%s", personalViewModel.getAccountValue(),
                personalViewModel.getCurrency());
        textViewDashboard.setText(format);
    }


    private void initUI(View view) {
        context = getContext();
        textViewDashboard = view.findViewById(R.id.text_dashboard);
        listViewHistory = view.findViewById(R.id.listViewHistory);
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setOnChartValueSelectedListener(new pieChartOnChartValueSelectedListener());
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, itemsListViewHistory);
        listViewHistory.setAdapter(adapter);
    }


    private void initHistory() {

        List<History> historyList = personalViewModel.getHistory();
        for (int i = historyList.size() - 1; i >= 0; i--) {
            History interactions = historyList.get(i);
            itemsListViewHistory.add(interactions.getDate() + "  "
                    + interactions.getOperation() + " "
                    + interactions.getAmount() + "  $"
                    + interactions.getName() + " for "
                    + interactions.getPrice() + "$");
        }
        adapter.notifyDataSetChanged();
    }

    //shows value after clicking on pie chart
    private class pieChartOnChartValueSelectedListener implements OnChartValueSelectedListener {

        @SuppressLint("DefaultLocale")
        @Override
        public void onValueSelected(Entry e, Highlight h) {
           personalViewModel.pieTooltip(e,context,view);
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

        PortfolioMoney portfolioMoney =  personalViewModel.getCurrentMoney();
        ArrayList<PortfolioStock> portfolioStocks = personalViewModel.getPortfolioStocks();
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
}
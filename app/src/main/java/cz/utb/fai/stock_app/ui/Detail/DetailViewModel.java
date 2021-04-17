package cz.utb.fai.stock_app.ui.Detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.utb.fai.stock_app.enums.Trade;
import cz.utb.fai.stock_app.fileH.FileHelper;
import cz.utb.fai.stock_app.models.History;
import cz.utb.fai.stock_app.models.HistoryGraph;
import cz.utb.fai.stock_app.models.Prediction;
import cz.utb.fai.stock_app.models.Stock;

public class DetailViewModel extends ViewModel {
    private FileHelper fileHelper;
    Context context;
    private Calendar cal2;
    private SimpleDateFormat dateFormat;
    private ArrayList<String> dateBack = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private Stock stock;
    private RequestQueue queue ;
    private HistoryGraph historyGraph;
    private Prediction prediction;
    private boolean called;
    TextView current,open,high, low,volume, change, changePercentage;

    public void init(Context context, Stock stock,TextView current,TextView open,TextView high,TextView low,TextView volume,TextView change,TextView changePercentage) {
        historyGraph = new HistoryGraph();
        prediction = new Prediction();
        fileHelper = new FileHelper(context);
        this.context = context;
        this.stock = stock;
        queue = Volley.newRequestQueue(context);
        called=false;
        this.current=current;
        this.open=open;
        this.high=high;
        this.low=low;
        this.volume=volume;
        this.change=change;
        this.changePercentage=changePercentage;
    }

    @SuppressLint("SimpleDateFormat")
    private void getDateForAPI(int time, Calendar cal) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i <= time; i++) {
            cal.add(Calendar.DATE, -1);
            String date = dateFormat.format(cal.getTime());
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                dateBack.add(date);
            }
        }
    }

    public void onSellClicked(TextView amount) {
        if (Integer.parseInt(String.valueOf(amount.getText())) > 0) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cal2 = Calendar.getInstance();
            cal2.add(Calendar.DATE, 0);
            String date = dateFormat.format(cal2.getTime());
            History history = new History(date, stock.getSymbol(), String.valueOf(stock.getPrice()), String.valueOf(amount.getText()), Trade.SELL);
            try {
                if (fileHelper.sellStockPortfolio(stock, Integer.valueOf(String.valueOf(amount.getText())))) {
                    fileHelper.storeToFileUserInteractions(history);
                    Toast.makeText(amount.getContext(), "You just Sold:" + amount.getText() + " of " + stock.getSymbol(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(amount.getContext(), "Cannot Sell " + amount.getText() + " of " + stock.getSymbol() +
                            "- Not enough Stock", Toast.LENGTH_SHORT).show();
                }
                amount.setText("1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBuyClicked(TextView amount) {
        if (Integer.parseInt(String.valueOf(amount.getText())) > 0) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cal2 = Calendar.getInstance();
            cal2.add(Calendar.DATE, 0);
            String date = dateFormat.format(cal2.getTime());
            History history = new History(date, stock.getSymbol(), String.valueOf(stock.getPrice()), String.valueOf(amount.getText()), Trade.BUY);
            try {
                if (fileHelper.buyStockPortfolio(stock, Integer.valueOf(String.valueOf(amount.getText())))) {
                    fileHelper.storeToFileUserInteractions(history);
                    Toast.makeText(amount.getContext(), "You just bought:" + amount.getText() + " of " + stock.getSymbol(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(amount.getContext(), "Cannot buy " + amount.getText() + " of " + stock.getSymbol() +
                            "- Not enough CASH", Toast.LENGTH_SHORT).show();
                }
                amount.setText("1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void setTextTextViews() {
        current.setText("Latest: " + stock.getPrice());
        open.setText("Open: " + stock.getOpen());
        high.setText("High: " + stock.getHigh());
        low.setText("Low: " + stock.getLow());
        volume.setText("Volume: " + String.format("%.0f", stock.getVolume()));
        change.setText("Change: " + stock.getChange());
        changePercentage.setText("Change: " + stock.getChangePercent());
    }

    public void getPredictionData( final TextView textView) {

        //     String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + getString(R.string.AlphaVantageKey);
        // works on emulator
        String url1 = "http://10.0.2.2:8080/edems_swag/stock_api/1.0.0/prediction?ticker=" + stock.getSymbol();
        //works for mobile on same network
        String url2 = "http://10.0.0.1:8080/edems_swag/stock_api/1.0.0/prediction?ticker=" + stock.getSymbol();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            prediction.setPrice(Double.parseDouble(jsonObject.getString("PRICE")));
                            prediction.setMean_ab_er(Double.parseDouble(jsonObject.getString("MEAN_ABSOLUTE_ERROR")));
                            prediction.setDate(jsonObject.getString("PRICE_DATE"));
                            prediction.setAccuracy(Double.parseDouble(jsonObject.getString("ACCURACY")));
                            prediction.setSymbol(stock.getSymbol());
                            setTextPrediction(prediction);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(stringRequest);
    }

    private void setTextPrediction(Prediction prediction) {
        current.setText(String.format("Predicted Price: %.2f", prediction.getPrice()));
        String formater = prediction.getDate();
        formater = formater.substring(0,10);
        open.setText(String.format("Date of Prediction: %10s", formater));
        high.setText(String.format("Avarage Error of prediction: %.2f", prediction.getMean_ab_er()));
        low.setText(String.format("Accuracy of prediction: %.2f", prediction.getAccuracy()*100));
        change.setVisibility(View.INVISIBLE);
        changePercentage.setVisibility(View.INVISIBLE);
        volume.setVisibility(View.INVISIBLE);
    }

    public void getIntradayData( final BarChart chart) {
        if (dateBack.size() == 0) {
            Calendar calendar = Calendar.getInstance();
            getDateForAPI(35, calendar);
        }
        //     String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + getString(R.string.AlphaVantageKey);
        // works on emulator
        String url1 = "http://10.0.2.2:8080/edems_swag/stock_api/1.0.0/history?ticker=" + stock.getSymbol();
        //works for mobile on same network
        String url2 = "http://10.0.0.1:8080/edems_swag/stock_api/1.0.0/history?ticker=" + stock.getSymbol();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseData = jsonObject.getJSONObject("Time Series (Daily)");
                            JSONObject responseData2 = null;
                            for (int i = 0; i < dateBack.size(); i++) {
                                try {
                                    responseData2 = responseData.getJSONObject(dateBack.get(i));
                                    price.add(responseData2.getString("5. adjusted close"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            createGraph(price, stock, chart);
                        } catch (JSONException e) {
                            Log.i("foos", "JSONException - " + e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("foos", "VolleyError - " + error);
                    }
                });
        queue.add(stringRequest);
    }

    private void createGraph(ArrayList<String> historyGraphData, Stock stock, BarChart chart) {
        ArrayList<BarEntry> stockDays = new ArrayList<>();
        if (price.size() > 0) {
            for (int i = 0; i < historyGraphData.size(); i++) {
                stockDays.add(new BarEntry(i, Float.parseFloat(String.valueOf(historyGraphData.get((historyGraphData.size() - 1) - i)))));
            }
            BarDataSet barDataSet = new BarDataSet(stockDays, stock.getSymbol());
            if (stock.getChange() <= 0)
                barDataSet.setColor(Color.rgb(255, 20, 20));
            else
                barDataSet.setColor(Color.rgb(50, 255, 100));
            barDataSet.setValueTextSize(16f);
            BarData barData = new BarData(barDataSet);
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);
            chart.getAxisLeft().setEnabled(false);
            chart.getXAxis().setEnabled(false);
            chart.setFitBars(true);
            chart.setData(barData);
            chart.setDrawValueAboveBar(false);
            chart.isDrawBordersEnabled();
            chart.setMaxVisibleValueCount(10);
            chart.setNoDataText("Couldn't load data from Server");
            chart.setDrawBarShadow(true);
            chart.getDescription().setText("Last " + price.size() + " Trading days");
            chart.animateY(800);
        }
    }
}

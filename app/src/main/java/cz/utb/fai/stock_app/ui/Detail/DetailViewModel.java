package cz.utb.fai.stock_app.ui.Detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.enums.Trade;
import cz.utb.fai.stock_app.helpers.FileHelper;
import cz.utb.fai.stock_app.models.History;
import cz.utb.fai.stock_app.models.HistoryGraph;
import cz.utb.fai.stock_app.models.Prediction;
import cz.utb.fai.stock_app.models.Stock;

public class DetailViewModel extends ViewModel {
    private FileHelper fileHelper;
    private Context context;
    private Calendar cal2;
    private SimpleDateFormat dateFormat;
    private ArrayList<String> dateBack = new ArrayList<>();
    private ArrayList<String> price = new ArrayList<>();
    private Stock stock;
    private RequestQueue queue ;
    private HistoryGraph historyGraph;
    private Prediction prediction;
    @SuppressLint("StaticFieldLeak")
    private TextView current,open,high, low,volume, change, changePercentage, predictionA,predictionP,predictionE,predictionD;
    private ProgressBar progressBar;

    public void init(Context context, Stock stock, TextView current, TextView open, TextView high, TextView low, TextView volume, TextView change, TextView changePercentage, TextView predictionAverage, TextView predictionDate, TextView predictionError, TextView predictionPrice, ProgressBar progressBar) {
        historyGraph = new HistoryGraph();
        prediction = new Prediction();
        fileHelper = new FileHelper(context);
        this.context = context;
        this.stock = stock;
        queue = Volley.newRequestQueue(context);
        this.current=current;
        this.open=open;
        this.high=high;
        this.low=low;
        this.volume=volume;
        this.change=change;
        this.changePercentage=changePercentage;
        predictionA=predictionAverage;
        predictionE=predictionError;
        predictionP=predictionPrice;
        predictionD=predictionDate;
        this.progressBar=progressBar;
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
                    fileHelper.storeToFileHistory(history);
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
                    fileHelper.storeToFileHistory(history);
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

    public void getPredictionData() {

        String predict = "http://"+context.getString(R.string.IP_ADDRESS)+":8080/edems_swag/stock_api/1.0.0/prediction?ticker=" + stock.getSymbol();
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, predict,
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setTextPrediction(Prediction prediction) {
        progressBar.setVisibility(View.INVISIBLE);
        predictionP.setVisibility(View.VISIBLE);
        predictionP.setText(String.format("%.2f", prediction.getPrice()));
        current.setText("Predicted price: ");

        String formater = prediction.getDate();
        formater = formater.substring(0,10);
        open.setText("Date of Prediction:");
        predictionD.setVisibility(View.VISIBLE);
        predictionD.setText(formater);

        predictionA.setVisibility(View.VISIBLE);
        predictionA.setText(String.format(" %.2f", prediction.getMean_ab_er()));
        high.setText("Mean absolute error:");

        formater = String.format("%.2f", prediction.getAccuracy()*100)+"%";
        predictionE.setVisibility(View.VISIBLE);
        predictionE.setText(formater);
        low.setText("Accuracy :");

        change.setVisibility(View.INVISIBLE);
        changePercentage.setVisibility(View.INVISIBLE);
        volume.setVisibility(View.INVISIBLE);
    }

    public void getIntradayData( final BarChart chart) {
        if (dateBack.size() == 0) {
            Calendar calendar = Calendar.getInstance();
            getDateForAPI(35, calendar);
        }

        String urlData = "http://"+context.getString(R.string.IP_ADDRESS)+":8080/edems_swag/stock_api/1.0.0/history?ticker=" + stock.getSymbol();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlData,
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
                barDataSet.setColor(Color.rgb(204, 0, 0));
            else
                barDataSet.setColor(Color.rgb(0, 102, 0));
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

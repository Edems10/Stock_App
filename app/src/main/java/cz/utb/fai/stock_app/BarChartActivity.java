package cz.utb.fai.stock_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BarChartActivity extends AppCompatActivity {

    SimpleDateFormat dateFormat;
    ArrayList<String> dateBack = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    BarChart chart;
    JSONObject responseData;
    Button btnBuy, btnSell;
    Stock stock;
    TextView open, high, low, current, volume, change, changePercentage;
    EditText amount;
    Calendar cal,cal2;
    FileHelper fileHelper;

    final static String appDir = "/StockAppDir/";
    final static String appDataFileName = "/stockData.txt";
    final static String pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String pathToDir = pathToStorage + appDir;
    final static String fullPathToFile = pathToStorage + appDir + appDataFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_stock);


        chart = findViewById(R.id.barChart);
        open = findViewById(R.id.open);
        high = findViewById(R.id.high);
        low = findViewById(R.id.low);
        current = findViewById(R.id.current);
        volume = findViewById(R.id.volume);
        change = findViewById(R.id.change);
        changePercentage = findViewById(R.id.changePercentage);
        amount = findViewById(R.id.amount);
        fileHelper=new FileHelper();
        cal = Calendar.getInstance();
        cal2 = Calendar.getInstance();
        Intent i = getIntent();
        stock = (Stock) i.getSerializableExtra("selected stock");
        btnSell = findViewById(R.id.buttonsell);
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cal2.add(Calendar.DATE, 0);
                String date = dateFormat.format(cal2.getTime());
                UserInteractions ui = new UserInteractions(date,stock.Symbol, String.valueOf(stock.Price),String.valueOf(amount.getText()),"Sold");
                try {
                    fileHelper.storeToFile(ui,pathToDir,fullPathToFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnBuy = findViewById(R.id.buttonbuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cal2.add(Calendar.DATE, 0);
                String date = dateFormat.format(cal2.getTime());
                UserInteractions ui = new UserInteractions(date,stock.Symbol,String.valueOf(stock.Price),String.valueOf(amount.getText()),"Bought");
                try {
                    fileHelper.storeToFile(ui,pathToDir,fullPathToFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        setTextTextViews();

        getDateForAPI(35, cal);
        getIntradayData(stock.Symbol);

    }



    private void setTextTextViews() {
        current.setText("Now:" + stock.Price);
        open.setText("Open:" + stock.Open);
        high.setText("High:" + stock.High);
        low.setText("Low:" + stock.Low);
        //double VolumeText = stock.Volume;
        volume.setText("Volume:" + String.format("%.0f", stock.Volume));
        change.setText("Change:" + stock.Change);
        changePercentage.setText("Change:" + stock.ChangePercent);

    }

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


    private void getIntradayData(String symbol) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + getString(R.string.AlphaVantageKey);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // ZPRACOVANI JSONu:
                        try {
                            //1. Z DAT, KTERA JSME OBDRZELI VYTVORIME JSONObject
                            JSONObject jsonObject = new JSONObject(response);
                            // JSONObject childObject = array.getAsJsonObject();

                            // 2. Z PROMENNE jsonObject ZISKAME "responseData" (viz struktura JSONu odpovedi)
                            responseData = jsonObject.getJSONObject("Time Series (Daily)");

                            JSONObject responsData2 = null;
                            for (int i = 0; i < dateBack.size(); i++) {

                                try {
                                    responsData2 = responseData.getJSONObject(dateBack.get(i));

                                    price.add(responsData2.getString("5. adjusted close"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            createGraph(price);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // txt.setText("That didn't work!");
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void createGraph(ArrayList<String> temp) {

        ArrayList<BarEntry> stockDays = new ArrayList<>();

        if (price.size() > 0) {
            for (int i = 0; i < temp.size(); i++) {
                stockDays.add(new BarEntry(i, Float.parseFloat(temp.get((temp.size() - 1) - i))));
            }
            BarDataSet barDataSet = new BarDataSet(stockDays, stock.Symbol);
            if (stock.Change <= 0)
                barDataSet.setColor(Color.rgb(255, 20, 20));
            else
                barDataSet.setColor(Color.rgb(50, 255, 50));
            barDataSet.setValueTextSize(16f);

            BarData barData = new BarData(barDataSet);

            chart.setFitBars(true);
            chart.setData(barData);
            chart.getDescription().setText("Last " + String.valueOf(price.size()) + " Trading days");
            chart.animateY(800);
        }
    }
}

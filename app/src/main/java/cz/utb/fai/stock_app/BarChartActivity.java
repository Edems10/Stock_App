package cz.utb.fai.stock_app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BarChartActivity extends AppCompatActivity {

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String date;
    ArrayList<String> dateBack = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    BarChart chart;
    JSONObject responseData;
    Button btn;
    Stock stock;
    TextView open,high,low,current,volume,change,changePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_stock);
        chart= findViewById(R.id.barChart);
        open = findViewById(R.id.open);
        high = findViewById(R.id.high);
        low = findViewById(R.id.low);
        current = findViewById(R.id.current);
        volume = findViewById(R.id.volume);
        change = findViewById(R.id.change);
        changePercentage = findViewById(R.id.changePercentage);


        Intent i = getIntent();
        stock = (Stock)i.getSerializableExtra("selected stock");
        btn = findViewById(R.id.buttonbuy);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGraph(price);
            }
        });
        setTextTextViews();

        Calendar cal = Calendar.getInstance();
        GetDateForAPI(21,cal);

        GetIntradayData(stock.Symbol);

    }


    private void setTextTextViews()
    {
        current.setText("Now:"+stock.Price);
        open.setText("Open:"+stock.Open);
        high.setText("High:"+stock.High);
        low.setText("Low:"+stock.Low);
        //double VolumeText = stock.Volume;
        volume.setText("Volume:"+String.format("%.0f",stock.Volume));
        change.setText("Change:"+stock.Change);
        changePercentage.setText("Change:"+stock.ChangePercent);

    }

    private void GetDateForAPI(int time,Calendar cal)
    {
        for (int i =0;i<=time;i++)
        {
            cal.add(Calendar.DATE, -1);
            date = dateFormat.format(cal.getTime());
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                dateBack.add(date);
            }
        }

    }




    private void GetIntradayData(String symbol)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+symbol+"&apikey="+ getString(R.string.AlphaVantageKey);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // ZPRACOVANI JSONu:
                        try
                        {
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
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
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
                       stockDays.add(new BarEntry(i, Float.parseFloat(temp.get((temp.size()-1)-i))));
                   }
                   BarDataSet barDataSet = new BarDataSet(stockDays, stock.Symbol);
                   barDataSet.setColor(ColorTemplate.getHoloBlue());
                   barDataSet.setValueTextSize(16f);

                   BarData barData = new BarData(barDataSet);

                   chart.setFitBars(true);
                   chart.setData(barData);
                   chart.getDescription().setText("Last "+String.valueOf(price.size())+" Trading days");
                   chart.animateY(800);
               }
           }
}

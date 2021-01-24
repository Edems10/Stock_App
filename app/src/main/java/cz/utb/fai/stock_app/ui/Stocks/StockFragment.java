package cz.utb.fai.stock_app.ui.Stocks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;

import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.Stock;

public class StockFragment extends Fragment implements View.OnClickListener {

    private StockViewModel stockViewModel;
    TextView txt;
    Button bt;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        View view = inflater.inflate(R.layout.fragment_stock, container, false);
        txt = (TextView) view.findViewById(R.id.editText);
        bt=(Button) view.findViewById(R.id.button2);
        bt.setOnClickListener(this);
        context = getContext();

        return view;
    }



    public void onClick(View v)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        final String stockTicker="NIO";
        String url ="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+stockTicker+"&apikey="+ getString(R.string.AlphaVantageKey);

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

                            // 2. Z PROMENNE jsonObject ZISKAME "responseData" (viz struktura JSONu odpovedi)
                            JSONObject responseData = jsonObject.getJSONObject("Global Quote");

                            // 3. Z PROMENNE responseData ZISKAME "translatedText" (viz struktura JSONu odpovedi)

                            Stock stock =new Stock(stockTicker);
                            stock.Open = Double.valueOf(responseData.getString(stock.Values[1]));
                            stock.High = Double.valueOf(responseData.getString(stock.Values[2]));
                            stock.Low = Double.valueOf(responseData.getString(stock.Values[3]));
                            stock.Price = Double.valueOf(responseData.getString(stock.Values[4]));
                            stock.Volume = Double.valueOf(responseData.getString(stock.Values[5]));
                            stock.LatestTradingDay = Date.valueOf(responseData.getString(stock.Values[6]));
                            stock.PreviousClose = Double.valueOf(responseData.getString(stock.Values[7]));
                            stock.Change = Double.valueOf(responseData.getString(stock.Values[8]));
                            stock.ChangePercent = responseData.getString(stock.Values[9]);



                            // 4. V textView ZOBRAZIME VYSLEDEK PREKLADU
                           // txt.setText("Response is: " + stock.High);
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
                        txt.setText("That didn't work!");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
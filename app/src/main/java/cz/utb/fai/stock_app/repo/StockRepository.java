package cz.utb.fai.stock_app.repo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.models.Stock;

public class StockRepository {

    private Stock stock;
    private static StockRepository instance;
    private ArrayList<Stock> dataSet = new ArrayList<>();
    private RequestQueue queue;

    public static StockRepository getInstance(){
        if(instance==null){
            instance = new StockRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Stock>> getStocks()
    {
        MutableLiveData<List<Stock>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    public MutableLiveData<List<Stock>> getStocksAdd(Context context, List<String> list)
    {
        for(int i = 0;i<list.size();i++)
        {
            GetSymbolBasicInfo(list.get(i),context);
        }
        MutableLiveData<List<Stock>> data = new MutableLiveData<>();
        data.setValue(dataSet);

        return data;
    }


    private boolean checkSymbolExists(String symbol) {
        if(dataSet!=null)
        for (int i = 0; i <dataSet.size() ; i++) {
            if(dataSet.get(i).getSymbol().equals(symbol))
            {
                return true;
            }
        }
        return false;
    }

    public void GetSymbolBasicInfo(final String symbol, final Context context)
    {
        if (!checkSymbolExists(symbol)) {
            final String[] Values = {"01. symbol", "02. open", "03. high", "04. low", "05. price", "06. volume", "07. latest trading day", "08. previous close", "09. change", "10. change percent"};
            if (queue == null) {
                queue = Volley.newRequestQueue(context);
            }
            String url1 = "http://"+context.getString(R.string.IP_ADDRESS)+":8080/edems_swag/stock_api/1.0.0/quote?ticker=" + symbol;


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject responseData = jsonObject.getJSONObject("Global Quote");

                                stock = new Stock();
                                stock.setSymbol(responseData.getString(Values[0]));
                                stock.setOpen(Double.parseDouble(responseData.getString(Values[1])));
                                stock.setHigh(Double.parseDouble(responseData.getString(Values[2])));
                                stock.setLow(Double.parseDouble(responseData.getString(Values[3])));
                                stock.setPrice(Double.parseDouble(responseData.getString(Values[4])));
                                stock.setVolume(Double.parseDouble(responseData.getString(Values[5])));
                                stock.setLatestTradingDay(Date.valueOf(responseData.getString(Values[6])));
                                stock.setPreviousClose(Double.parseDouble(responseData.getString(Values[7])));
                                stock.setChange(Double.parseDouble(responseData.getString(Values[8])));
                                String formater = responseData.getString(Values[9]);
                                formater = formater.substring(0,formater.indexOf(".")+3);
                                formater+="%";
                                stock.setChangePercent(formater);

                                if (stock != null) {
                                    dataSet.add(stock);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            /// TODO: 28.04.2021  
                            Toast.makeText(context,"Error with server",Toast.LENGTH_SHORT);
                            Log.e("tmp", "" + error + ""
                            );
                        }
                    });
            queue.add(stringRequest);
        }
    }



}

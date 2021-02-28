package cz.utb.fai.stock_app.ui.Stocks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

import cz.utb.fai.stock_app.ui.Graph.BarChartActivity;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.Models.Stock;


public class StockFragment extends Fragment implements View.OnClickListener, Serializable {
    ArrayList<String> itemsForListView = new ArrayList<>();
    private StockViewModel stockViewModel;
    ListView listViewStocks;
    EditText txt;
    Button bt;
    Context context;
    Stock stock =null;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel.class);

        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        txt = view.findViewById(R.id.editText);
        txt.setOnClickListener(this);
        bt= view.findViewById(R.id.button2);
        bt.setOnClickListener(this);
        context = getContext();
        listViewStocks = view.findViewById(R.id.listViewStocks);
        adapter = new ArrayAdapter<>(context,android.R.layout.simple_selectable_list_item,itemsForListView);
        listViewStocks.setAdapter(adapter);

        if(itemsForListView.size()==0)
        {
            GetSymbolBasicInfo("SPY");
        }

        listViewStocks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent I = new Intent(getActivity(), BarChartActivity.class);
                I.putExtra("selected stock",stockViewModel.stockList.get(position));
                getActivity().startActivity(I);
            }
        });
        return view;
    }

    public void onClick(View v)
    {
        if(R.id.button2==v.getId())
      GetSymbolBasicInfo(txt.getText().toString());

       else if(R.id.editText==v.getId())
            txt.getText().clear();
    }


    public void GetSymbolBasicInfo(final String symbol)
    {
        final String[] Values ={"01. symbol","02. open","03. high","04. low","05. price","06. volume","07. latest trading day","08. previous close","09. change","10. change percent"};
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+symbol+"&apikey="+ getString(R.string.AlphaVantageKey);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject responseData = jsonObject.getJSONObject("Global Quote");
                            stock =new Stock();
                            stock.Symbol = responseData.getString(Values[0]);
                            stock.Open = Double.parseDouble(responseData.getString(Values[1]));
                            stock.High = Double.parseDouble(responseData.getString(Values[2]));
                            stock.Low = Double.parseDouble(responseData.getString(Values[3]));
                            stock.Price = Double.parseDouble(responseData.getString(Values[4]));
                            stock.Volume = Double.parseDouble(responseData.getString(Values[5]));
                            stock.LatestTradingDay = Date.valueOf(responseData.getString(Values[6]));
                            stock.PreviousClose = Double.parseDouble(responseData.getString(Values[7]));
                            stock.Change = Double.parseDouble(responseData.getString(Values[8]));
                            stock.ChangePercent = responseData.getString(Values[9]);

                            if(stock!=null) {
                                if(stockViewModel.setStockList(stock)) {
                                    itemsForListView.add("$"+stock.Symbol +"\t\t\t"+stock.Price +"\t\t\tChange: "+stock.Change +"\t\t\t"+stock.ChangePercent);
                                    adapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(context,"Already exist " +"["+symbol+"]",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context,"Incorrect Symbol "+"["+symbol+"]",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(context,"Error With ["+symbol+"]",Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(stringRequest);
    }
}
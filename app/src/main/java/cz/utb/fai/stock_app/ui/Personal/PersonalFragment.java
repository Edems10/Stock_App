package cz.utb.fai.stock_app.ui.Personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.FileHelper;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.Stock;
import cz.utb.fai.stock_app.UserInteractions;

public class PersonalFragment extends Fragment {

    private PersonalViewModel personalViewModel;


    final static String appDir = "/StockAppDir/";
    final static String pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String fullPathToFileWithInteractions = pathToStorage + appDir + "/stockData.txt";
    //final static String fullPathToFileWithMoney = pathToStorage + appDir + "/AccountValue";

    FileHelper fileHelper;
    Context context;
    ArrayList<String> itemsForListView = new ArrayList<>();
    ListView listView ;
    TextView textView;
    ArrayAdapter<String> adapter;
    List<UserInteractions> userInteractionsList = new ArrayList();
    List<String> stocksInteractedWithList = new ArrayList<>();
    List<Stock> interactedStocksUpdatedPrice = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        personalViewModel = ViewModelProviders.of(this).get(PersonalViewModel.class);
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        textView = view.findViewById(R.id.text_dashboard);

        listView = view.findViewById(R.id.listViewHistory);
        context = getContext();
        fileHelper = new FileHelper();

        try {
            userInteractionsList = fileHelper.loadFromFile(fullPathToFileWithInteractions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, itemsForListView);
        listView.setAdapter(adapter);
        for (int i = userInteractionsList.size() - 1; i >= 0; i--) {
            UserInteractions interactions = userInteractionsList.get(i);
            itemsForListView.add(interactions.getDate() + "  " + interactions.getOperation() + " " + interactions.getAmount() + "  $" + interactions.getNameOfSymbol() + " for " + interactions.getPriceOfSymbol() + "$");

        }
        adapter.notifyDataSetChanged();

        stocksInteractedWithList = getSymbolsFromList();

        for (int i = 0; i < stocksInteractedWithList.size(); i++) {
            getSymbolBasicInfo(stocksInteractedWithList.get(i));
        }
        if(interactedStocksUpdatedPrice.size()<3){
       //textView.setText(String.valueOf(calculateProfit()));
    }
        return view;

    }


    private void calculateProfit()
    {
        double profit = 0;
        if(interactedStocksUpdatedPrice.size()!=0) {

            Stock stock = new Stock();
            for (int i = 0; i < userInteractionsList.size(); i++) {
                UserInteractions interactions = userInteractionsList.get(i);

                for (int j = 0; j < interactedStocksUpdatedPrice.size(); j++) {
                    if (interactedStocksUpdatedPrice.get(j).Symbol.equals(interactions.getNameOfSymbol())) {
                        stock = interactedStocksUpdatedPrice.get(j);
                    }
                }

                int amountOfShares = Integer.parseInt(interactions.getAmount());
                if (interactions.getOperation().equals("Bought")) {
                    profit += (Double.parseDouble(interactions.getPriceOfSymbol()) - stock.Price) * amountOfShares;
                } else {
                    profit += (stock.Price - Double.parseDouble(interactions.getPriceOfSymbol())) * amountOfShares;
                }

            }
        }
        textView.setText(String.format("Your Profit: %.2f$", profit));
    }

    private List<String> getSymbolsFromList()
    {
        List<String> symbols = new ArrayList();
        for(int i =0;i<userInteractionsList.size();i++)
        {
            if(symbols.size()>0) {
                boolean alreadyExists=false;
                for (int j = 0; j < symbols.size(); j++) {
                    if (symbols.get(j).equals(userInteractionsList.get(i).getNameOfSymbol())) {
                        alreadyExists=true;
                    }
                }
                if(!alreadyExists)symbols.add(userInteractionsList.get(i).getNameOfSymbol());
            }else
            {
                symbols.add(userInteractionsList.get(i).getNameOfSymbol());
            }
        }
        return symbols;
    }


    public void getSymbolBasicInfo(final String symbol)
    {
        final String[] Values ={"01. symbol","02. open","03. high","04. low","05. price","06. volume","07. latest trading day","08. previous close","09. change","10. change percent"};
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+symbol+"&apikey="+ getString(R.string.AlphaVantageKey);

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


                            Stock stock =new Stock();
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

                                interactedStocksUpdatedPrice.add(stock);
                            }
                            calculateProfit();
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
                        // txt.setText("That didn't work!");
                        Toast.makeText(context,"Error With ["+symbol+"]",Toast.LENGTH_SHORT).show();
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
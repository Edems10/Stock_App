package cz.utb.fai.stock_app.ui.Personal;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.FileHelper;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.UserInteractions;

public class PersonalFragment extends Fragment {

    private PersonalViewModel personalViewModel;


    final static String appDir = "/StockAppDir/";
    final static String pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String fullPathToFileWithInteractions = pathToStorage + appDir + "/stockData.txt";
    final static String fullPathToFileWithMoney = pathToStorage + appDir + "/AccountValue";

    FileHelper fileHelper;
    Context context;
    ArrayList<String> itemsForListView = new ArrayList<>();
    ListView listView ;
    List<UserInteractions> userInteractionsList = new ArrayList();
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        personalViewModel = ViewModelProviders.of(this).get(PersonalViewModel.class);
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        final TextView textView = view.findViewById(R.id.text_dashboard);
        personalViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) { textView.setText(s); }
        });

        listView = view.findViewById(R.id.listViewHistory);
        context = getContext();
        fileHelper = new FileHelper();

        try {
            userInteractionsList =fileHelper.loadFromFile(fullPathToFileWithInteractions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<>(context,android.R.layout.simple_selectable_list_item,itemsForListView);
        listView.setAdapter(adapter);
        for(int i=userInteractionsList.size()-1;i>=0;i--)
        {
            UserInteractions interactions = userInteractionsList.get(i);
            itemsForListView.add(interactions.getDate()+"  "+interactions.getOperation()+" "+interactions.getAmount()+"  $"+interactions.getNameOfSymbol()+" for "+interactions.getPriceOfSymbol()+"$");

        }
        adapter.notifyDataSetChanged();

        getSymbolsFromList();

        return view;

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
}
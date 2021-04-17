package cz.utb.fai.stock_app.ui.Stocks;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import cz.utb.fai.stock_app.viewAdapter.RecyclerViewAdapter;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.models.Stock;


public class StockFragment extends Fragment implements View.OnClickListener, Serializable {

    private StockViewModel stockViewModel;
    private EditText txt;
    private Button bt;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_stock, container, false);
        context = getContext();
        txt = view.findViewById(R.id.editTextStockList);
        txt.setOnClickListener(this);
        bt= view.findViewById(R.id.buttonAddStockToList);
        bt.setOnClickListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        stockViewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        stockViewModel.init(view.getContext());
        initRecyclerView(view);

        stockViewModel.getStocks().observe(this, new Observer<List<Stock>>() {
            @Override
            public void onChanged(List<Stock> stocks) {
                mAdapter.notifyDataSetChanged();
            }
        });

        // pri stisknuti enter se callne volani stejne jako pri clicku buttonu ADD

        txt.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            stockViewModel.addNewStock(txt.getText().toString(), context);
                            txt.getText().clear();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        return view;
    }


    private void initRecyclerView(View view)
    {
        mAdapter = new RecyclerViewAdapter(view.getContext(),stockViewModel.getStocks().getValue());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        txt=null;
        bt=null;
        context=null;
        stockViewModel=null;
    }

    public void onClick(View v)
    {
        if(R.id.buttonAddStockToList ==v.getId()) {
            stockViewModel.addNewStock(txt.getText().toString(), context);
            txt.getText().clear();
        }
       else if(R.id.editTextStockList ==v.getId())
            txt.getText().clear();
    }

}
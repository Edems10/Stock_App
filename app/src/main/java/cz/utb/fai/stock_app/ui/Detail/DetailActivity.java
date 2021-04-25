package cz.utb.fai.stock_app.ui.Detail;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;

import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.models.Stock;


public class DetailActivity extends AppCompatActivity {

    private BarChart chart;
    private Button btnBuy, btnSell,btnPredict;
    private Stock stock;
    private TextView open, high, low, current, volume, change, changePercentage, predictionPrice,predictionDate,predictionError, predictionAverage;
    private EditText amount;
    private ProgressBar progressBar;
    DetailViewModel detailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
        init();
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSellClick();
            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuyClick();
            }
        });
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPredictClick();
            }
        });
    }

    private void onPredictClick() {
        btnPredict.setVisibility(View.INVISIBLE);
        detailViewModel.getPredictionData();
    }

    private void onBuyClick() {
        detailViewModel.onBuyClicked(amount);
    }

    private void onSellClick() {
        detailViewModel.onSellClicked(amount);
    }

    private void init() {

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        btnPredict=findViewById(R.id.buttonPredict);
        btnBuy = findViewById(R.id.buttonBuy);
        chart = findViewById(R.id.barChart);
        open = findViewById(R.id.open);
        high = findViewById(R.id.high);
        low = findViewById(R.id.low);
        current = findViewById(R.id.current);
        volume = findViewById(R.id.volume);
        change = findViewById(R.id.change);
        btnSell = findViewById(R.id.buttonSell);
        changePercentage = findViewById(R.id.changePercentage);
        amount = findViewById(R.id.amount);
        predictionAverage = findViewById(R.id.predictionAvarage);
        predictionDate = findViewById(R.id.predictionDate);
        predictionError = findViewById(R.id.predictionError);
        predictionPrice = findViewById(R.id.predictionPrice);
        progressBar=findViewById(R.id.progressBar);
        Intent i = getIntent();
        stock = (Stock) i.getSerializableExtra("selected stock");
        detailViewModel.init(this,stock,current,open,high,low,volume,change,changePercentage,predictionAverage,predictionDate,predictionError,predictionPrice,progressBar);
        detailViewModel.getIntradayData(chart);
        detailViewModel.setTextTextViews();

    }
}

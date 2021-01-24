package cz.utb.fai.stock_app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stock {

    public final String[] Values ={"01. symbol","02. open","03. high","04. low","05. price","06. volume","07. latest trading day","08. previous close","09. change","10. change percent"};

    public Stock(String StockTicker)
    {
        Symbol =StockTicker;
    }


    public String Symbol;
    public double Open;
    public double High;
    public double Low;
    public double Price;
    public double Volume;
    public Date LatestTradingDay;
    public double PreviousClose;
    public double Change;
    public String ChangePercent;
}

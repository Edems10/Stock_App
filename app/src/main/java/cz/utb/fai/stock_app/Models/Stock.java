package cz.utb.fai.stock_app.Models;


import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

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

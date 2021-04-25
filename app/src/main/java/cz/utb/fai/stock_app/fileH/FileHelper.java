package cz.utb.fai.stock_app.fileH;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.models.PortfolioMoney;
import cz.utb.fai.stock_app.models.PortfolioStock;
import cz.utb.fai.stock_app.models.Stock;
import cz.utb.fai.stock_app.enums.Trade;
import cz.utb.fai.stock_app.models.History;

public  class FileHelper extends Application {


    final static String fileNameMoney = "money";
    final static String fileNameHistory = "history";
    final static String fileNamePortfolio = "portfolio";
    File dir;

    public FileHelper(Context context) {
        this.dir = new File(context.getFilesDir(),"");;
    }
    

    public List<History> loadFromFileUserInteractions() throws IOException {
        Gson gson = new Gson();
        File file = new File(dir, fileNameHistory);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        String dataFromFile = "[";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        dataFromFile += "]";
        Type dataListType = new TypeToken<ArrayList<History>>() {
        }.getType();
        br.close();
        return gson.fromJson(dataFromFile, dataListType);
    }

    public void storeToFileUserInteractions(History history) throws IOException {
        Gson gson = new Gson();
        String userInteractionsToJson = gson.toJson(history);
        File file = new File(dir, fileNameHistory);
        if(file.length()!=0)
        {
            //adding "," if the file contains other userinteracitons
            userInteractionsToJson = String.format(",%s", userInteractionsToJson);
        }else{
            userInteractionsToJson  = String.format("%s", userInteractionsToJson);
        }

            try {
                FileOutputStream fos = new FileOutputStream(file, true);

                fos.write(userInteractionsToJson.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    //creates File with Fake Money
    private void createFakeMoney(PortfolioMoney portfolioMoney)throws IOException{
        Gson gson = new Gson();
        String settingsToJson = gson.toJson(portfolioMoney);
        File file = new File(dir, fileNameMoney);
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(settingsToJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //loads current amount and currency from file
    public PortfolioMoney loadCurrentMoney() throws IOException {
        Gson gson = new Gson();
        File file = new File(dir, fileNameMoney);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        String dataFromFile ="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<PortfolioMoney>() {
        }.getType();
        br.close();
        return gson.fromJson(dataFromFile, dataListType);
    }

    //value - amount of money sold/bought
    //trade -Side of trade - buy/sell
    //portfolioMoney - current value of accnount
    public void editMoney(double value, Trade trade,PortfolioMoney portfolioMoney) throws IOException {
        Gson gson = new Gson();
        File file = new File(dir, fileNameMoney);
        String newJson=null;
        if(trade.equals(Trade.SELL))
        {
            double currentValue=portfolioMoney.getAmount();
            portfolioMoney.setAmount(currentValue+value);
            newJson = gson.toJson(portfolioMoney);
        }else if(trade.equals(Trade.BUY))
        {
            double currentValue=portfolioMoney.getAmount();
            portfolioMoney.setAmount(currentValue-value);
            newJson = gson.toJson(portfolioMoney);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(newJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean sellStockPortfolio(Stock stock,int amount)throws IOException{

        double priceOfTrade = stock.getPrice()*amount;
        PortfolioMoney portfolioMoney=loadCurrentMoney();
        if(sellStockAddToPortfolio(stock,amount))
        {
            editMoney(priceOfTrade,Trade.SELL,portfolioMoney);
            return true;
        }
        return false;
    }

    public boolean buyStockPortfolio(Stock stock,int amount)throws IOException {
        PortfolioMoney portfolioMoney = loadCurrentMoney();
        double priceOfTrade = stock.getPrice() * amount;
        if ((portfolioMoney.getAmount() - priceOfTrade >= 0))
        {
            editMoney(priceOfTrade,Trade.BUY,portfolioMoney);
            buyStockAddToPortfolio(stock,amount);
            return true;
        }
        return false;
    }

    private void buyStockAddToPortfolio(Stock stock, int amount) throws IOException {
        List<PortfolioStock> portfolioStockList = loadFromPortfolio();
        if(portfolioStockList==null)portfolioStockList=new ArrayList<>();

        int currentAmount=0;
        double currentAveragePrice=0;
        boolean found =false;
        for(int i=0;i<portfolioStockList.size();i++)
        {
            PortfolioStock portfolioStock = portfolioStockList.get(i);
            if(stock.getSymbol().equals(portfolioStock.getTicker()))
            {
                currentAmount = portfolioStock.getAmount();
                currentAveragePrice=portfolioStock.getAveragePrice();
                    int newAmount =currentAmount+amount;
                    double newAveragePrice  =((currentAmount*currentAveragePrice)+(amount*stock.getPrice()))/newAmount;
                    portfolioStock.setAmount(newAmount);
                    portfolioStock.setAveragePrice(newAveragePrice);
                    portfolioStockList.set(i,portfolioStock);

                found=true;
                break;
            }
        }
        if(!found){
            PortfolioStock portfolioStock = new PortfolioStock(stock.getSymbol(),amount,stock.getPrice());
            portfolioStockList.add(portfolioStock);
        }
        storeToPortfolio(portfolioStockList);
    }

    private boolean sellStockAddToPortfolio(Stock stock, int amount) throws IOException {
        List<PortfolioStock> portfolioStockList = loadFromPortfolio();
        if(portfolioStockList==null)return false;

        int currentAmount=0;
        for(int i=0;i<portfolioStockList.size();i++)
        {
            PortfolioStock portfolioStock = portfolioStockList.get(i);
            if(stock.getSymbol().equals(portfolioStock.getTicker()))
            {
                currentAmount = portfolioStock.getAmount();
                    int newAmount =currentAmount-amount;
                    if(newAmount<0)return false;
                    portfolioStock.setAmount(newAmount);
                    portfolioStockList.set(i,portfolioStock);
                   storeToPortfolio(portfolioStockList);
                    return true;

            }
        }
        return false;
    }

    //stores Portfolio Stock to File
    public void storeToPortfolio(List<PortfolioStock> portfolioStockList) throws IOException {
        Gson gson = new Gson();
        File file = new File(dir, fileNamePortfolio);
        String userInteractionsToJson = gson.toJson(portfolioStockList);
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(userInteractionsToJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //loads file to arrayList
    public ArrayList<PortfolioStock> loadFromPortfolio() throws IOException {
        Gson gson = new Gson();
        File file = new File(dir, fileNamePortfolio);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        String dataFromFile="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<ArrayList<PortfolioStock>>() {
        }.getType();
        br.close();
        return gson.fromJson(dataFromFile, dataListType);
    }


    //creates default file with money
    public void checkMoneyExists() {
        File file = new File(dir, fileNameMoney);
        if (!file.exists()) {
            try {
                PortfolioMoney portfolioMoney = new PortfolioMoney(10000.0,"$");
                createFakeMoney(portfolioMoney);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void checkHistoryExists() {
        File file = new File(dir, fileNameHistory);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void checkPortfolioExists() {
        File file = new File(dir, fileNamePortfolio);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkdirExists() {
        if(!dir.exists()){
            dir.mkdir();
        }
    }
}


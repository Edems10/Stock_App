package cz.utb.fai.stock_app;

import android.app.Application;
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

import cz.utb.fai.stock_app.Models.PortfolioMoney;
import cz.utb.fai.stock_app.Models.PortfolioStock;
import cz.utb.fai.stock_app.Models.SettingsModel;
import cz.utb.fai.stock_app.Models.Stock;
import cz.utb.fai.stock_app.Models.Trade;
import cz.utb.fai.stock_app.Models.UserInteractions;

public  class FileHelper extends Application {

    final static String pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String applicationDirectory = "/StockApp/";
    final static String fileNameSetting = "/settings";
    final static String fileNameMoney = "/money";
    final static String fileNameHistory = "/history";
    final static String fileNamePortfolio = "/portfolio";
    final static String pathToDir = pathToStorage + applicationDirectory;
    final static String fullPathToSetting = pathToStorage + applicationDirectory + fileNameSetting;
    final static String fullPathToMoney = pathToStorage + applicationDirectory + fileNameMoney;
    final static String fullPathToHistory = pathToStorage + applicationDirectory + fileNameHistory;
    final static String fullPathToPortfolio = pathToStorage + applicationDirectory + fileNamePortfolio;



    private void createSettings(SettingsModel settingsModel) throws IOException {
        Gson gson = new Gson();
        String settingsToJson = gson.toJson(settingsModel);

        File file = new File(fullPathToSetting);
        if (!file.exists()) {
            file.createNewFile();
        }
            try {
                FileOutputStream fos = new FileOutputStream(fullPathToSetting, false);
                fos.write(settingsToJson.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // upravene netestovane
    public void updateSettings(SettingsModel settingsModel) throws IOException {
        Gson gson = new Gson();
        String settingsToJson = gson.toJson(settingsModel);
        try {

            FileOutputStream fos = new FileOutputStream(fullPathToSetting, false);
            fos.write(settingsToJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public SettingsModel loadFromSettings() throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fullPathToSetting));
        String line = "";
        String dataFromFile ="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<SettingsModel>() {
        }.getType();

        return gson.fromJson(dataFromFile, dataListType);
    }


    public List<UserInteractions> loadFromFileUserInteractions() throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fullPathToHistory));
        String line = "";
        String dataFromFile = "[";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        dataFromFile += "]";
        Type dataListType = new TypeToken<ArrayList<UserInteractions>>() {
        }.getType();

        return gson.fromJson(dataFromFile, dataListType);
    }

    public void storeToFileUserInteractions(UserInteractions userInteractions) throws IOException {
        Gson gson = new Gson();
        String userInteractionsToJson = gson.toJson(userInteractions);
        File file = new File(fullPathToHistory);
        if(file.length()!=0)
        {
            //adding "," if the file contains other userinteracitons
            userInteractionsToJson = String.format(",%s", userInteractionsToJson);
        }else{
            userInteractionsToJson  = String.format("%s", userInteractionsToJson);
        }

            try {
                FileOutputStream fos = new FileOutputStream(fullPathToHistory, true);

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

        try {
            FileOutputStream fos = new FileOutputStream(fullPathToMoney, false);
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
        BufferedReader br = new BufferedReader(new FileReader(fullPathToMoney));
        String line = "";
        String dataFromFile ="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<PortfolioMoney>() {
        }.getType();

        return gson.fromJson(dataFromFile, dataListType);
    }

    //value - amount of money sold/bought
    //trade -Side of trade - buy/sell
    //portfolioMoney - current value of accnount
    public void editMoney(double value, Trade trade,PortfolioMoney portfolioMoney) throws IOException {
        Gson gson = new Gson();
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
            FileOutputStream fos = new FileOutputStream(fullPathToMoney, false);
            fos.write(newJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean sellStockPortfolio(Stock stock,int amount)throws IOException{

        double priceOfTrade = stock.Price*amount;
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
        double priceOfTrade = stock.Price * amount;
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
            if(stock.Symbol.equals(portfolioStock.getTicker()))
            {
                currentAmount = portfolioStock.getAmount();
                currentAveragePrice=portfolioStock.getAveragePrice();
                    int newAmount =currentAmount+amount;
                    double newAveragePrice  =((currentAmount*currentAveragePrice)+(amount*stock.Price))/newAmount;
                    portfolioStock.setAmount(newAmount);
                    portfolioStock.setAveragePrice(newAveragePrice);
                    portfolioStockList.set(i,portfolioStock);

                found=true;
                break;
            }
        }
        if(!found){
            PortfolioStock portfolioStock = new PortfolioStock(stock.Symbol,amount,stock.Price);
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
            if(stock.Symbol.equals(portfolioStock.getTicker()))
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
        String userInteractionsToJson = gson.toJson(portfolioStockList);
        try {
            FileOutputStream fos = new FileOutputStream(fullPathToPortfolio, false);
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
        BufferedReader br = new BufferedReader(new FileReader(fullPathToPortfolio));
        String line = "";
        String dataFromFile="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<ArrayList<PortfolioStock>>() {
        }.getType();

        return gson.fromJson(dataFromFile, dataListType);
    }


    //creates directory if doesn't exist
    public void checkDirectoryExists() {

            File file = new File(pathToDir);
            if (!file.exists()) {
                file.mkdir();
            }

    }


    //creates default file with money
    public void checkMoneyExists() {
        File file = new File(fullPathToMoney);
        if (!file.exists()) {
            try {
                file.createNewFile();
                PortfolioMoney portfolioMoney = new PortfolioMoney(10000.0,"$");
                createFakeMoney(portfolioMoney);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkHistoryExists() {
        File file = new File(fullPathToHistory);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkSettingsExists() {
        File file = new File(fullPathToSetting);
        if (!file.exists()) {
            try {
                file.createNewFile();
                SettingsModel settingsModel = new SettingsModel("Free",1200,"AlphaVantage");
                createSettings(settingsModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkPortfolioExists() {
        File file = new File(fullPathToPortfolio);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


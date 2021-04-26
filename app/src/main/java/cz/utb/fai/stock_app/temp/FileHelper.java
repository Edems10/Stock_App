package cz.utb.fai.stock_app.temp;

import android.app.Application;
import android.content.Context;

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


    /**
     * Loads history from internal file
     * @return List with all data saved in internal file History
     * @throws IOException
     */
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

    /**
     * Saves history to internal file with json formatting
     * @param history
     * @throws IOException
     */
    public void storeToFileHistory(History history) throws IOException {
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

    /**
     * Creates file with fake monez
     * @param portfolioMoney
     * @throws IOException
     */
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

    /**
     * Loads current amount and currency from file
     * @return current money and currency in PortfolioMoney class
     * @throws IOException
     */
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

    /**
     * Edits file with current money depending on if the action is sell or buy
     * @param value amount of money sold/bought
     * @param trade side of trade - Enum Trade
     * @param portfolioMoney current value of account in class PortfolioMoney
     * @throws IOException
     */
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

    /**
     * If there is enough stock in file removes the amount from file and adds money to file with
     * money
     * @param stock stock that is being sold
     * @param amount amount of stock sold
     * @return
     * @throws IOException
     */
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

    /**
     * If there is enough money in ads the amount of stock to file and removes money from file
     * @param stock stock that is being bought
     * @param amount amount of stock bought
     * @return
     * @throws IOException
     */
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

    /**
     * Adds stock to internal file that stores all stocks owned and recalculates average price
     * @param stock
     * @param amount
     * @throws IOException
     */
    private void buyStockAddToPortfolio(Stock stock, int amount) throws IOException {
        List<PortfolioStock> portfolioStockList = loadFromPortfolio();
        if(portfolioStockList==null)portfolioStockList=new ArrayList<>();

        int currentAmount;
        double currentAveragePrice;
        boolean found = false;
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

    /**
     * If there is enough stock to sell - removes stock from portfolio file and returns true
     * else returns false
     * @param stock
     * @param amount
     * @return True if enough stock was in file False if not enough stock was in file
     * @throws IOException
     */
    private boolean sellStockAddToPortfolio(Stock stock, int amount) throws IOException {
        List<PortfolioStock> portfolioStockList = loadFromPortfolio();
        if(portfolioStockList==null)return false;

        int currentAmount;
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

    /**
     * Stores List of PortfolioStock to internal file
     * @param portfolioStockList list of PorfolioStock class
     * @throws IOException
     */
    private void storeToPortfolio(List<PortfolioStock> portfolioStockList) throws IOException {
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


    /**
     * Loads file to arrayList of PortfolioStock class
     * @return Array list of PortfoliStock
     * @throws IOException
     */
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


    /**
     * Creates internal file with 10000 $ if it doesn't already exist
     */
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

    /**
     * Creates internal file history if it doesn't already exist
     */
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

    /**
     * Creates internal file portfolio if it doesn't already exist
     */
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

    /**
     * Creates internal directory if it doesn't already exist
     */
    public void checkdirExists() {
        if(!dir.exists()){
            dir.mkdir();
        }
    }
}


package cz.utb.fai.stock_app;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

import cz.utb.fai.stock_app.Models.Portfolio;
import cz.utb.fai.stock_app.Models.SettingsModel;
import cz.utb.fai.stock_app.Models.UserInteractions;

public  class FileHelper extends Application {

    public void createSettings(SettingsModel settingsModel, String pathToDir, String fullPathToFile) throws IOException {
        Gson gson = new Gson();
        String settingsToJson = gson.toJson(settingsModel);
        dirExist(pathToDir);

        File file = new File(fullPathToFile);
        if (!file.exists()) {
            file.createNewFile();
        }
            try {
                FileOutputStream fos = new FileOutputStream(fullPathToFile, true);
                fos.write(settingsToJson.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    public void updateSettings(SettingsModel settingsModel, String pathToDir, String fullPathToFile) throws IOException {
        Gson gson = new Gson();
        String settingsToJson = gson.toJson(settingsModel);


        dirExist(pathToDir);
        File file = new File(fullPathToFile);
        file.delete();
        if (!file.exists()) {
            file.createNewFile();
        }
        try {

            FileOutputStream fos = new FileOutputStream(fullPathToFile, true);
            fos.write(settingsToJson.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public SettingsModel loadFromSettings(String fullPathToFile) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fullPathToFile));
        String line = "";
        String dataFromFile ="";
        while ((line = br.readLine()) != null) {
            dataFromFile += line;
        }
        Type dataListType = new TypeToken<SettingsModel>() {
        }.getType();

        return gson.fromJson(dataFromFile, dataListType);
    }


    public List<UserInteractions> loadFromFileUserInteractions(String fullPathToFile) throws IOException {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(fullPathToFile));
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

    public void storeToFileUserInteractions(UserInteractions userInteractions, String pathToDir, String fullPathToFile) throws IOException {
        Gson gson = new Gson();
        String userInteractionsToJson = gson.toJson(userInteractions);
        dirExist(pathToDir);

        File file = new File(fullPathToFile);
        if (file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(fullPathToFile, true);
                String temp = String.format(",%s", userInteractionsToJson);
                fos.write(temp.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            try {
                File f = new File(fullPathToFile);
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(fullPathToFile, false);
                fos.write(userInteractionsToJson.getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


//todo dodelat celkove file pro porfolio managment (bonus for me)
// pridat pote upravovani te file napr AMD stocks bougth :1 -> bought:2

    public boolean sellStockPortfolio(Portfolio portfolio, String pathTodir, String fullPathToFile)throws IOException{


        return true;
    }

    public boolean buyStockPortfolio(Portfolio portfolio, String pathTodir, String fullPathToFile)throws IOException{


        return true;
    }
    private void dirExist(String filedir) {
        File file = new File(filedir);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}

package cz.utb.fai.stock_app.Models;

import java.io.File;

import cz.utb.fai.stock_app.FileHelper;
//todo pridelat enum pro selected Api jakmile budu vedet jake tam budou :)
public class SettingsModel {

    public SettingsModel(String version, int delay, String apiProvider) {
        this.version = version;
        this.delay = delay;
        this.apiProvider = apiProvider;
    }

    //constructor for paid versions of api doesn't need delay for api calls
    public SettingsModel(String version, String apiProvider) {
        this.version = version;
        this.apiProvider = apiProvider;
        this.delay=0;
    }


    public String getApiProvider() {
        return apiProvider;
    }

    public void setApiProvider(String apiProvider) {
        this.apiProvider = apiProvider;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private String version;
    private int delay;
    private String apiProvider;
}

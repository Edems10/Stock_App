package cz.utb.fai.stock_app.ui;

import android.os.Bundle;
import android.os.Environment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.IOException;

import cz.utb.fai.stock_app.FileHelper;
import cz.utb.fai.stock_app.Models.SettingsModel;
import cz.utb.fai.stock_app.R;

public class MainActivity extends AppCompatActivity {

    final static String appDir = "/StockAppDir/";
    final static String appDataFileName = "/settings.txt";
    final static String pathToStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String pathToDir = pathToStorage + appDir;
    final static String fullPathToFile = pathToStorage + appDir + appDataFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        File f=new File(fullPathToFile);
        if(!f.exists()) {
            FileHelper fp = new FileHelper();
            try {
                SettingsModel settingsModel = new SettingsModel("Free",1200,"AlphaVantage");
                fp.createSettings(settingsModel,pathToDir, fullPathToFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //creating settings if they don't exist
        //dodelani singletonu pro settings
        //Settings settings = Settings.getInstance();
        SettingsModel sm= new SettingsModel("Free",1200,"AlphaVantage");
        FileHelper fh = new FileHelper();
        try {

            fh.updateSettings(sm,pathToDir,fullPathToFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
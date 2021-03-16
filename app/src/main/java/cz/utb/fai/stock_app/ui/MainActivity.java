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
import cz.utb.fai.stock_app.Models.PortfolioMoney;
import cz.utb.fai.stock_app.Models.SettingsModel;
import cz.utb.fai.stock_app.Models.Trade;
import cz.utb.fai.stock_app.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //todo oznamit userovi ze nemam prava zapisovat do file

        FileHelper fileHelper =new FileHelper();
        fileHelper.checkDirectoryExists();
        fileHelper.checkMoneyExists();
        fileHelper.checkHistoryExists();
        fileHelper.checkSettingsExists();
        fileHelper.checkPortfolioExists();



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
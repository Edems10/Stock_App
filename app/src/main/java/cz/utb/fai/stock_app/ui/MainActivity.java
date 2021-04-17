package cz.utb.fai.stock_app.ui;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import cz.utb.fai.stock_app.fileH.FileHelper;
import cz.utb.fai.stock_app.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        FileHelper fileHelper =new FileHelper(getApplicationContext());
        fileHelper.checkdirExists();
        fileHelper.checkMoneyExists();
        fileHelper.checkHistoryExists();
        fileHelper.checkPortfolioExists();


        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
package com.launcertestapp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.launcertestapp.R;
import com.launcertestapp.view.fragments.HomePageFrag;
import com.launcertestapp.view.pojo.AppData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Kumar on 04/08/2022
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static List<AppData> appDataList = new ArrayList<>();
    private Intent mainIntent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // initially calling homePageFrag
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomePageFrag homePageFrag = new HomePageFrag();
        homePageFrag.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment_container, homePageFrag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0)
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

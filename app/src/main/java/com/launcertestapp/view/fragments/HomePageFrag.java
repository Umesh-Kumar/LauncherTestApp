package com.launcertestapp.view.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.launcertestapp.BuildConfig;
import com.launcertestapp.R;
import com.launcertestapp.utility.Util;
import com.launcertestapp.utils.Api;
import com.launcertestapp.view.pojo.WeatherResp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Umesh Kumar on 03/08/2022
 */
public class HomePageFrag extends Fragment {

    @BindView(R.id.txtCloud1)
    TextView txtCloud1;
    @BindView(R.id.txtCloud2)
    TextView txtCloud2;
    @BindView(R.id.txtCloud3)
    TextView txtCloud3;
    @BindView(R.id.txtCloud4)
    TextView txtCloud4;
    @BindView(R.id.txtCloud5)
    TextView txtCloud5;
    @BindView(R.id.txtCloud6)
    TextView txtCloud6;
    @BindView(R.id.txtBatteryPercent)
    TextView txtBatteryPercent;
    @BindView(R.id.txtChargeStatus)
    TextView txtChargeStatus;
    @BindView(R.id.btnLaunch)
    Button btnLaunch;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.timeView)
    TextView timeView;
    @BindView(R.id.ampmView)
    TextView ampmView;

    private final SimpleDateFormat sdf_ampm = new SimpleDateFormat("a", Locale.ENGLISH);
    private final SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
    private PowerManager.WakeLock appClockWakeLock;
    private Timer timer;
    private int apiCounter = 1;
    private ProgressDialog progressDialog;
    private Unbinder binder;

    public HomePageFrag() {
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            // show battery status
            if (txtBatteryPercent != null)
                txtBatteryPercent.setText(String.valueOf(level) + "%");
            if (pb != null) pb.setProgress(level);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            // hide or view charging status
            if (txtChargeStatus != null)
                txtChargeStatus.setVisibility(isCharging ? View.VISIBLE : View.INVISIBLE);

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home_page, container, false);
        binder = ButterKnife.bind(this, view);

        // register receiver for battery status
        getActivity().registerReceiver(batteryInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // init time
        timeView.setTextScaleX(0.8f);
        updateViews();

        if (Util.isNetworkAvailable(getActivity())) {
            // call api for weather status
            progressDialog = new ProgressDialog(getActivity());
            new CheckWeatherTasks().execute(Api.apiBeijing);
        }

        return view;
    }

    @OnClick({R.id.btnLaunch, R.id.btnAllApps})
    public void onViewClicked(View view) {
        if (Util.btnClickTime(0.4))
            switch (view.getId()) {
                case R.id.btnLaunch:
                    // call launcher on click
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    break;
                case R.id.btnAllApps:
                    // check all apps installed on click
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    AllAppsFrag allAppsFrag = new AllAppsFrag();
                    allAppsFrag.setArguments(getActivity().getIntent().getExtras());
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment_container, allAppsFrag, "allAppsFrag");
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack("allAppsFrag");
                    transaction.commit();
                    break;
            }
    }

    public class CheckWeatherTasks extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog to show the user what is happening
            if (apiCounter == 1) {
                progressDialog.setMessage("checking weather...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            // Fetch data from the API in the background.

            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(BuildConfig.host + params[0]);
                    //open a URL coonnection
                    urlConnection = (HttpURLConnection) url.openConnection();

                    if (urlConnection.getResponseCode() == 200) {
                        InputStream in = urlConnection.getInputStream();

                        InputStreamReader isw = new InputStreamReader(in);

                        int data = isw.read();

                        while (data != -1) {
                            result += (char) data;
                            data = isw.read();
                        }

                        // string response into Gson pojo
                        WeatherResp resp = new Gson().fromJson(result, WeatherResp.class);
                        result = "Temp: " + resp.getTemperature() + "\n" + resp.getCity() + "\n" +
                                resp.getCountry() + "\n" + resp.getDescription();
                    } else result = "Error Code: " + urlConnection.getResponseCode() + "\n" +
                            urlConnection.getResponseMessage();

                    // return the data to onPostExecute method
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            // populate weather widget UI
            switch (apiCounter) {
                case 1:
                    txtCloud1.setText(s);
                    new CheckWeatherTasks().execute(Api.apiBerlin);
                    break;
                case 2:
                    txtCloud2.setText(s);
                    new CheckWeatherTasks().execute(Api.apiCardiff);
                    break;
                case 3:
                    txtCloud3.setText(s);
                    new CheckWeatherTasks().execute(Api.apiEdinburgh);
                    break;
                case 4:
                    txtCloud4.setText(s);
                    new CheckWeatherTasks().execute(Api.apiLondon);
                    break;
                case 5:
                    txtCloud5.setText(s);
                    new CheckWeatherTasks().execute(Api.apiNottingham);
                    break;
                case 6:
                    txtCloud6.setText(s);
                    apiCounter = 0;
                    progressDialog.dismiss();
                    break;
            }
            apiCounter += 1;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // release views
        binder.unbind();
    }

    private void updateViews() {
        // date & time setup
        Date now = new Date();
        ampmView.setText(sdf_ampm.format(now));
        timeView.setText(sdf_time.format(now));
    }

    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateViews();
                    }
                });
            }
        };

        if (timer == null) {
            timer = new Timer(true);
            timer.schedule(timerTask, 0, 15 * 1000);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set wakelock to prevent screen from sleep mode
        if (appClockWakeLock == null) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            appClockWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "myLock");
        }

        startTimer();
        appClockWakeLock.acquire();
    }

    @Override
    public void onPause() {
        stopTimer();

        // release wake lock
        if (appClockWakeLock != null) {
            appClockWakeLock.release();
        }
        super.onPause();
    }

}

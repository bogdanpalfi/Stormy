package com.example.bogdan.stormy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bogdan.stormy.R;
import com.example.bogdan.stormy.weather.Current;
import com.example.bogdan.stormy.weather.Day;
import com.example.bogdan.stormy.weather.Forecast;
import com.example.bogdan.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends Activity {

    public static final String DAILY_FORECAST="DAILY FORECAST";
    public static final String HOURLY_FORECAST="HOURLY FORECAST";
    public static final String TAG= MainActivity.class.getSimpleName();
    private Forecast mForecast;


    @InjectView(R.id.timeLabel)TextView mTimeLabel;
    @InjectView(R.id.temperaturaLabel)TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue)TextView mHumidityValue;
    @InjectView(R.id.precipValue)TextView mPrecipLabel;
    @InjectView(R.id.summaryLabel)TextView mSummaryLabel;
    @InjectView(R.id.refreshButton)ImageView mRefreshButton;
    @InjectView(R.id.progressBar)ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude=46.770439;
        final double longitude=23.591423;
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast(latitude,longitude);
            }
        });
        getForecast(latitude,longitude);
        Log.d(TAG,"Main UI code is running");
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey="2ece418c68d8cf608c2bb7229305a312";

        String forecastUrl="https://api.forecast.io/forecast/"+apiKey+
                "/"+latitude+","+longitude;


        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData =  response.body().string();
                        Log.v(TAG,jsonData);
                        if (response.isSuccessful()) {
                            mForecast =parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDispay();
                                }
                            });
                        } else alertUserAboutError();
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);

                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else{
            Toast.makeText(this, R.string.network_unavailable,Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility()==View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.INVISIBLE);}
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateDispay() {
        Current current =mForecast.getCurrent();
        mTemperatureLabel.setText(current.getmTemperature()+"");
        mTimeLabel.setText("at " + current.getFormattedTime()+" it will be ");
        mHumidityValue.setText(current.getmHumidity()+"");
        mPrecipLabel.setText(current.getmPrecipChange()+"%");
        mSummaryLabel.setText(current.getSummary());
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException{
        Forecast forecast=new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];
        for(int i=0; i<data.length();i++){
            JSONObject jsonDay=data.getJSONObject(i);
            Day day=new Day();
            day.setSummary(jsonDay.getString("summary"));
            day.setTemperature(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);
            days[i]=day;
        }
        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly= forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours=new Hour[data.length()];
        for(int i=0;i<data.length();i++){
            JSONObject jsonHour= data.getJSONObject(i);
            Hour hour=new Hour();
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i]=hour;
        }
        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG,"From JSON: "+ timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        Current current =new Current();
        current.setmHumidity(currently.getDouble("humidity"));
        current.setmTime(currently.getLong("time"));
        current.setmPrecipChange(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setmTemperature(currently.getDouble("temperature"));
        current.setmTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =(ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        boolean isAvailable= false;
        if(networkInfo!=null&&networkInfo.isConnected())
            isAvailable=true;

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog= new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view){
        Intent intent = new Intent (this, DailyForecastActivity.class);

        intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());
        startActivity(intent);
    }

    /*@OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view)
    {
        Intent intent = new Intent (this, HourlyForecastActivity.class);

        intent.putExtra(HOURLY_FORECAST,mForecast.getHourlyForecast());
        startActivity(intent);
    }*/
}

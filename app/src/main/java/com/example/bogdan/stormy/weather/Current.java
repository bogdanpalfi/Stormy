package com.example.bogdan.stormy.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by BOGDAN on 8/31/2016.
 */
public class Current {

    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChange;
    private String summary;

    public String getmTimeZone() {
        return mTimeZone;
    }

    public void setmTimeZone(String mTimeZone) {
        this.mTimeZone = mTimeZone;
    }

    private String mTimeZone;




    public long getmTime() {
        return mTime;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getmTimeZone()));
        Date dateTime=new Date(getmTime()*1000);
        String timeString = formatter.format(dateTime);
        return timeString;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public int getmTemperature() {
        return (int)(Math.round((mTemperature-32)*5/90));
    }

    public void setmTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public double getmHumidity() {
        return mHumidity;
    }

    public void setmHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getmPrecipChange() {
        double precipPerc=mPrecipChange*100;
        return (int)Math.round(precipPerc);
    }

    public void setmPrecipChange(double mPrecipChange) {
        this.mPrecipChange = mPrecipChange;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

package com.example.bogdan.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bogdan.stormy.R;
import com.example.bogdan.stormy.weather.Day;

/**
 * Created by BOGDAN on 9/2/2016.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;
    public DayAdapter(Context context, Day[] days){
        mContext=context;
        mDays=days;
    }


    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);
            holder= new ViewHolder();
            holder.dayLabel=(TextView) view.findViewById(R.id.dayNameLabel);
            holder.temperatureLabel=(TextView) view.findViewById(R.id.tempLabel);

            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }
        Day day= mDays[i];
        holder.temperatureLabel.setText(day.getTemperature()+"");
        holder.dayLabel.setText(day.getDayOfTheWeek());
        if(i==0){
            holder.dayLabel.setText("Today");
        }

        return view;
    }
    private static class ViewHolder{
        TextView temperatureLabel;
        TextView dayLabel;

    }
}

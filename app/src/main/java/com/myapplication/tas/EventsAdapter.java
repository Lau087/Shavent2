package com.myapplication.tas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by i7-3930 on 29/01/2016.
 */
public class EventsAdapter extends ArrayAdapter{
    List list=new ArrayList<>();

    public EventsAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class DataHandler{
        ImageView event_image;
        TextView event_name;
        TextView event_time;
        ImageView event_active;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        DataHandler handler;
        if(convertView == null)
        {
            LayoutInflater inflater= (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.listview_custom_layout,parent, false);
            handler=new DataHandler();
            handler.event_image=(ImageView) row.findViewById(R.id.event_image);
            handler.event_name=(TextView) row.findViewById(R.id.event_name);
            handler.event_time=(TextView) row.findViewById(R.id.from_date);
            handler.event_active=(ImageView) row.findViewById(R.id.event_active);

            row.setTag(handler);
        }else{
            handler=(DataHandler)row.getTag();
        }

        EventDatasProvider datasProvider;
        datasProvider=(EventDatasProvider)this.getItem(position);

        handler.event_image.setImageResource(datasProvider.getEvent_image_resource());
        handler.event_name.setText(datasProvider.getEvent_name());
        handler.event_time.setText(datasProvider.getEvent_time());
        if(datasProvider.isEvent_active()){
            handler.event_active.setImageResource(R.drawable.v_green);
        }else{
            //handler.event_active.setImageResource(R.drawable.v_black);
        }

        return row;
    }
}

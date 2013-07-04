package com.android.adapter;

import java.util.ArrayList;

import com.android.model.WebAddress;
import com.example.facebookslider.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<WebAddress> {

	Context context;
    int layoutResourceId;   
     ArrayList<WebAddress> data=new ArrayList<WebAddress>();
    public MenuAdapter(Context context, int layoutResourceId, ArrayList<WebAddress> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RegardingTypeHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new RegardingTypeHolder();
            holder.textName=(TextView)row.findViewById(R.id.link);
            row.setTag(holder);
        }
        else
        {
            holder = (RegardingTypeHolder)row.getTag();
        }
       
        WebAddress address = data.get(position);
        holder.textName.setText(address.getName());
        return row;
       
    }
   
    static class RegardingTypeHolder
    {
        
        TextView textName;
    }
}


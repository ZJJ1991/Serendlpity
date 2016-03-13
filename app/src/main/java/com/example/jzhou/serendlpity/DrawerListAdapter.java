package com.example.jzhou.serendlpity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bel on 24.02.2016.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context context;
    ArrayList<NavItem> aNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> aNavItems) {
        this.context = context;
        this.aNavItems = aNavItems;
    }

    @Override
    public int getCount() {
        return aNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return aNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.description);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText(aNavItems.get(position).title);
        subtitleView.setText( aNavItems.get(position).description);
        iconView.setImageResource(aNavItems.get(position).icon);

        return view;
    }
}
package com.pinodex.loadcentral;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by pinodex on 3/28/15.
 */
public class NetworkListAdapter extends BaseAdapter {

    private Context context;

    private final String[][] mobileNetworks;

    public NetworkListAdapter(Context context, String[][] mobileNetworks) {
        this.context = context;
        this.mobileNetworks = mobileNetworks;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.default_list_layout, null);
        }


        TextView networkName = (TextView) convertView.findViewById(R.id.label);
        networkName.setText(mobileNetworks[position][1]);

        return convertView;
    }

    @Override
    public int getCount() {
        return mobileNetworks.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

package com.pinodex.loadcentral;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pinodex on 4/9/15.
 */
public class AccessNumberListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> accessNumbers;

    public AccessNumberListAdapter(Context context, ArrayList<String> accessNumbers) {
        this.context = context;
        this.accessNumbers = accessNumbers;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.access_number_list_layout, null);
        }


        TextView accessNumber = (TextView) convertView.findViewById(R.id.label);
        accessNumber.setText(accessNumbers.get(position));

        final int listPosition = position;

        ImageView deleteButton = (ImageView) convertView.findViewById(R.id.delete);

        String[] defAccessNumbers = App.getContext().getResources()
                .getStringArray(R.array.default_access_numbers);

        if (Arrays.asList(defAccessNumbers).contains(accessNumbers.get(position))) {
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accessNumbers.remove(listPosition);
                    notifyDataSetChanged();

                    Preferences.putString("access_numbers", TextUtils.join(",", accessNumbers));
                    SettingsActivity.doRefresh = true;
                }
            });
        }

        return convertView;
    }

    public void update(ArrayList<String> accessNumbers) {
        this.accessNumbers = accessNumbers;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return accessNumbers.size();
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

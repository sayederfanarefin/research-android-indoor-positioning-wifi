package com.bulbinc.wifi_indoor_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erfan on 4/29/2016.
 */
public class wifi_result_adapter extends ArrayAdapter<ScanResult> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public wifi_result_adapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ScanResult currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.wifi_results_layout_unit, parent, false);
        }

        row.setTag(currentItem);
        final TextView the_unit_news_titlw = (TextView) row.findViewById(R.id.textView_news_list_title);
        the_unit_news_titlw.setText(currentItem.SSID);
        return row;
    }


}
package com.bulbinc.wifi_indoor_app;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

public class indoor_map extends AppCompatActivity {

    getMapApi api;
    Boolean attached = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);
String bssid;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            bssid= null;
        } else {
            bssid= extras.getString("bssid");
        }


        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (api.get_json() != null) {
                            boolean error = false;
                            try {
                                if (api.get_json().get("Exception").equals("-666")) {
                                    error = true;
                                }
                            } catch (JSONException e) {

                            }
                            if (error) {
                                    Toast.makeText(getApplicationContext(), "Somethin went wrong. sorry", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    
                                    Log.v("==url" ,api.get_json().getString("map_image_url") );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplicationContext(), "please wait", Toast.LENGTH_LONG).show();
                            }

                        } else {

                        }

                        break;
                    default:
                        break;

                }
            };
        };

        api = new getMapApi(myHandler, bssid);
    }
}

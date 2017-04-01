package com.bulbinc.wifi_indoor_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager wifi;
    ListView lv;

    List<ScanResult> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView)findViewById(R.id.list_wifi);

        wifi = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        wifi.startScan();
        WifiScanReceiver wifiReciever = new WifiScanReceiver();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        List <ScanResult> losr = wifi.getScanResults();

        wifi_result_adapter wra = new wifi_result_adapter( this ,R.layout.wifi_results_layout_unit);

        for (int i =0; i < losr.size(); i++){

            wra.add(losr.get(i));
        }

        lv.setAdapter(wra);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ScanResult n = (ScanResult) parent.getItemAtPosition(position);

                String networkSSID = n.SSID;
                String networkPass = "BuLb_InC_2017";

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\"";

                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                wifi.addNetwork(conf);

                List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifi.disconnect();
                        wifi.enableNetwork(i.networkId, true);
                        wifi.reconnect();
                        break;
                    }
                }


                Intent intent = new Intent(MainActivity.this, indoor_map.class);

                intent.putExtra("bssid", n.BSSID);
//                intent.putExtra("news_title", n.title);
//                intent.putExtra("news_body", n.body);
//                intent.putExtra("news_date", n.date);
//                intent.putExtra("image_url", n.url);
//                intent.putExtra("local_image_url", n.local_url);
//
//
               startActivity(intent);

            }
        });



    }

}

class WifiScanReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
    }
}

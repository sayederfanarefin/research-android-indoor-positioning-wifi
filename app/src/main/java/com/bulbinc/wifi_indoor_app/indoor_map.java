package com.bulbinc.wifi_indoor_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class indoor_map extends AppCompatActivity {
    NetworkChangeReceiver ncreceiver;
    getMapApi api;
    Boolean attached = false;
    Handler myHandler;
    String bssid;
    ImageView map;
    Boolean fuck_you = false;
    String local_url = "na";
    String image_url;
    WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            bssid = null;
        } else {
            bssid = extras.getString("bssid");
        }
        wifi = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        map = (ImageView) findViewById(R.id.map);

        ncreceiver = new NetworkChangeReceiver(); // Create the receiver
        registerReceiver(ncreceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); // Register receiver
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                       // Toast.makeText(getApplicationContext(), "case0", Toast.LENGTH_LONG).show();
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

                                    Log.v("==url", api.get_json().getString("map_image_url"));
                                    image_url = filter_url(api.get_json().getString("map_image_url"));

                                    final Handler myHandler2 = new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case 0:
                                                Toast.makeText(getApplicationContext(), "case0", Toast.LENGTH_LONG).show();
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

                                                            Log.v("==localurl", local_url);
                                                          //  URL url = new URL(filter_url(api.get_json().getString("map_image_url")));
//
//                                                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                                            map.setImageBitmap(bmp);

                                                            File f=new File(local_url);
                                                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                                                            map.setImageBitmap(b);
                                                            getLocation();

                                                        } catch (Exception e) {
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
                                        }

                                        ;
                                    };

                                    download(image_url, myHandler2);
//                                    URL url = new URL(filter_url(api.get_json().getString("map_image_url")));
//                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                    map.setImageBitmap(bmp);
                                } catch (Exception e) {
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
            }

            ;
        };


    }

    private String filter_url(String url) {
        url = url.replace("\\", "");
        url = url.replace("[", "");
        url = url.replace("]", "");
        url = url.replace("\"", "");
        return url;
    }

    public void download(final String file_url, Handler h) {
        this.myHandler = h;
        try {

            new AsyncTask<Void, Void, String>() {
                String path_temp;

                @Override
                protected String doInBackground(Void... params) {
                    String file_name = "666";
                    try {
                        URL url = new URL(filter_url(file_url));
                        HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                        conection.connect();
                        // download the file
                        InputStream iStream = conection.getInputStream();
                        // Creating a bitmap from the downloaded inputstream
                        Bitmap b = BitmapFactory.decodeStream(iStream);
                        file_name = makeNameFromURL(file_url);
                        path_temp = saveToInternalStorage(b, file_name);
                        iStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return path_temp + "/" + file_name;
                }

                @Override
                protected void onPostExecute(String file_location) {
                    local_url = file_location;
                    myHandler.sendEmptyMessage(0);
                }
            }.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String makeNameFromURL(String url) {
        String name = "";
        int x = url.lastIndexOf('/');
        name = url.substring(x + 1, url.length());
        return name;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected() && !fuck_you) {
                Toast.makeText(getApplicationContext(), "Wifi! up!", Toast.LENGTH_LONG).show();

                api = new getMapApi(myHandler, bssid);
                fuck_you = true;
            }
        }
    }

    public void getLocation(){
        List<ScanResult> losr = wifi.getScanResults();
        List<ScanResult> lossr = new ArrayList<ScanResult>();

        for(int i =0 ; i < losr.size(); i ++){
            if(losr.get(i).SSID.contains("indoor_router")){
                lossr.add(losr.get(i));
            }

        }
    }
}

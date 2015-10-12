package com.level.scoliometer;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.level.scoliometer.models.MSKITEM;

public class HttpActivity extends Activity {
    public static String apiURL = "";
    private Panel _panel;
    public static String SD = "";
    public static String Mean= "";
    public MSKITEM mskitem = null;
    public String[] arrmskitem = null;
    public void setApiURL(String url) {
        this.apiURL=url;
        new HttpAPI().execute(apiURL);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
    }



    private class HttpAPI extends AsyncTask<String, String, MSKITEM> {
        Exception mException = null;

        @Override
        protected MSKITEM doInBackground(String... params) {
            String urlString=params[0]; // URL to call
            String resultToDisplay = "";
            InputStream in = null;
            Scanner scanner = null;
            String[] MSKresult= null;
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            StringBuilder jsonSB = new StringBuilder();
            // HTTP Get
            try {
                url = new URL(urlString.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
              //  urlConnection.setRequestMethod("GET");
             //   urlConnection.setDoOutput(true);
             //   urlConnection.setDoInput(true);
             //   urlConnection.setConnectTimeout(9000);
             //   urlConnection.connect();
                in = new BufferedInputStream(urlConnection.getInputStream());
                scanner = new Scanner(in);
                while (scanner.hasNext()) jsonSB.append(scanner.nextLine());

            } catch (Exception e ) {
                this.mException = e;
            }
            finally {
                scanner.close();
                urlConnection.disconnect();
            }

                String json = jsonSB.toString();
                Gson gson = new GsonBuilder().create();
            try {
                mskitem = gson.fromJson(json, MSKITEM.class);
            } catch (Exception e ) {
                this.mException = e;
            }
                url = null;
           Mean = mskitem.getMean();
           // SD =  mskitem.getStandard_Deviation();
            ScoliometerActivity Sactivity = new ScoliometerActivity();
          //  Intent intent = getIntent();
           // _panel = Sactivity._panel;
          //  _panel = (Panel) findViewById(R.id.level);
            Sactivity._panel.setthreec((Sactivity._panel.getonea()-Double.parseDouble(Mean)));
            Sactivity._panel.postInvalidate();
            return (mskitem);
        }

        protected void onPostExecute() {
            super.onPostExecute(mskitem);
            if (this.mException != null) {};
        }
    } // end CallAPI
}

package com.level.scoliometer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.level.scoliometer.models.Version;
import com.level.scoliometer.utils.*;


public class RequestTask extends AsyncTask<String, String, String>{
    private ProgressDialog mDialog;
    private Activity mActivity;
    private String sfilename = "";
    private String iversion = "";
    private SharedPreference sharedPreference;
    public RequestTask(String tfilename, ScoliometerActivity activity) {
        mDialog = new ProgressDialog(activity);
        mActivity = activity;
        sfilename = tfilename;
    }
    private String downloaddata(String uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.toString();
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }
    @Override
    protected void onPreExecute() {
        mDialog.setMessage(mActivity.getString(R.string.loading));
        mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.toString();
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mDialog.dismiss();
        String vfilename = "version.json";

        //Utils.writeToFile(sfilename, result, mActivity);
        String json = result;//Utils.readFromFile(vfilename, mActivity);
        if (json != null) {
            Gson gson = new Gson();
            Version nversion = gson.fromJson(json, Version.class);
            iversion = nversion.getversion();
            sharedPreference = new SharedPreference();
            String cversion = "0";
            cversion = sharedPreference.getValue(mActivity, "version", "version");

            if (!cversion.equals(iversion)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Database Update");
                builder.setMessage("Service Updated. Do you want to update database?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sfilename = "MSKROM.json";
                        dialog.dismiss();
                        mDialog.setMessage(mActivity.getString(R.string.loading));
                        AlertDialog.Builder builderloading = new AlertDialog.Builder(mActivity);
                        builderloading.setTitle(R.string.loading);
                        final AlertDialog alertloading = builderloading.create();
                        alertloading.show();
                        String tMSKROM = downloaddata(Constants.cURL);
                        Utils.writeToFile(sfilename, tMSKROM, mActivity);
                        sharedPreference.save(mActivity, iversion, "version", "version");
                        // Hide after some seconds
                        final Handler handler  = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (alertloading.isShowing()) {
                                    alertloading.dismiss();
                                }
                            }
                        };
                        alertloading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler.removeCallbacks(runnable);
                            }
                        });
                        handler.postDelayed(runnable, 10000);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code that is executed when clicking NO
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        Log.d("RequestTask", result.toString());

    }

}

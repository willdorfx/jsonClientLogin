package com.example.jsonclientlogin;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncNetConn extends AsyncTask<String, Integer, String> {

    // AsyncTask implementation for sending request and receiving response from server

    public SendTaskListener mListener;

    public interface SendTaskListener
    {
        public void onSendFinish(String response);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String jsonReply = null;

        try {
            URL url = new URL("https://server.com:0");                                   // replace with url:port of app server (and please use https)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(strings[0]);

            os.flush();
            os.close();

            Integer resCode = conn.getResponseCode();

            if(resCode >= 200 && resCode <= 299)
            {
                InputStream response = conn.getInputStream();
                jsonReply = convertStreamToString(response);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonReply;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onSendFinish(s);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
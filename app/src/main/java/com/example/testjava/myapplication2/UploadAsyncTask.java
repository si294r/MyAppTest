package com.example.testjava.myapplication2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by chairuddinsyah on 3/10/16.
 */
public class UploadAsyncTask extends AsyncTask<String, Void, String> {
    static final int BUFFER_SIZE = 1024;
    private Context _context;
    private String _filename;
    private String _url;

    public UploadAsyncTask setContext(Context context) {
        _context = context;
        return this;
    }

    public UploadAsyncTask setFilename(String filename) {
        _filename = filename;
        return this;
    }

    public UploadAsyncTask setUrl(String url) {
        _url = url;
        return this;
    }

    @Override
    protected String doInBackground(String... param) {
        HttpURLConnection httpConn = null;

        try {
            FileInputStream inputStream = _context.openFileInput(_filename);

            System.out.println("File to upload: " + _filename);

            // creates a HTTP connection
            URL url = new URL(_url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            // sets file name as a HTTP header
            httpConn.setRequestProperty("fileName", _filename);
            httpConn.setRequestProperty("Connection", "Close");
            httpConn.connect();

            Log.i("fileName", _filename);

            // opens output stream of the HTTP connection for writing data
            OutputStream outputStream = httpConn.getOutputStream();

            // Opens input stream of the file for reading data
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            System.out.println("Start writing data...");

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Data was written.");
            outputStream.close();
            inputStream.close();
        } catch (SocketTimeoutException e) {
            Log.e("Debug", "error: " + e.getMessage(), e);
        } catch (MalformedURLException ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }

        try {

            // always check HTTP response code from server
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // reads server's response
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String response = reader.readLine();
                System.out.println("Server's response: " + response);
            } else {
                System.out.println("Server returned non-OK code: " + responseCode);
            }
        } catch (IOException ioex) {
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
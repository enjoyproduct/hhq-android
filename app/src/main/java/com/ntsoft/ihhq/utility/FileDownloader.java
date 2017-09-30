package com.ntsoft.ihhq.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.model.Global;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader {

    public static Activity mActivity;
    // Progress Dialog
    private static ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    // File url to download
//    private static String file_url = "http://www.qwikisoft.com/demo/ashade/20001.kml";
    private static String file_url = "";
    private static String fileName = "";
    private static FileDownloadCompleteListener completeListener;
    /**
     * Showing Dialog
     * */

    protected static void showDialog(int id) {
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    protected static void dismissDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }
    public static void downloadFile(Activity activity, String url, String filename, FileDownloadCompleteListener listener) {
        mActivity = activity;
        file_url = url;
        fileName = filename;
        completeListener = listener;
        new DownloadFileFromURL().execute(file_url);
    }
    /**
     * Background Async Task to download file
     * */
    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                conection.setRequestProperty("Authorization", "Bearer " + Global.getInstance().me.token);
                conection.setDoOutput(false);
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                int status = conection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                // download the file
                InputStream input = conection.getInputStream();
                // Output stream
                OutputStream output = new FileOutputStream(Constant.MEDIA_PATH + "/" + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();
            completeListener.onComplete(Constant.MEDIA_PATH + "/" + fileName);
        }

    }
}

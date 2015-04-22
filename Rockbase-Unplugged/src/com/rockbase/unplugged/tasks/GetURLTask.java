package com.rockbase.unplugged.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.rockbase.unplugged.fragments.VideoListFragment;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 9/04/15
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class GetURLTask extends AsyncTask<String, Void, String[][]> {

    private Exception exception;

    private ProgressDialog dialog;
    private VideoListFragment activity;

    public GetURLTask(VideoListFragment activity) {
        this(activity, true);
    }

    public GetURLTask(VideoListFragment activity, boolean showDialog) {
        this.activity = activity;
        if (showDialog && (activity != null)) {
            dialog = new ProgressDialog(activity.getActivity());
        }
    }

    protected String[][] doInBackground(String... url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet clientGetMethod = new HttpGet(url[0]);
            HttpResponse clientResponse = null;
            clientResponse = client.execute(clientGetMethod);
            String infoString = _convertStreamToString(clientResponse.getEntity().getContent());
            JSONArray entries = new JSONObject(infoString).getJSONObject("feed").getJSONArray("entry");
            int numVideos = entries.length();
            String[] videoIds = new String[numVideos];
            String[] videoTitles = new String[numVideos];
            String[] videoDescriptions = new String[numVideos];
            for (int i = 0; i < numVideos; i++) {
                JSONObject entry = entries.getJSONObject(i).getJSONObject("media$group");
                videoIds[i] = entry.getJSONObject("yt$videoid").getString("$t");
                videoTitles[i] = entry.getJSONObject("media$title").getString("$t");
                videoDescriptions[i] = entry.getJSONObject("media$description").getString("$t");
            }


            String[][] data = new String[3][];
            data[0] = videoIds;
            data[1] = videoTitles;
            data[2] = videoDescriptions;

            return data;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    private String _convertStreamToString(InputStream iS) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
        StringBuilder sB = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sB.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                iS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sB.toString();
    }


    protected void onPostExecute(String[][] json) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        if (this.activity != null) {
            this.activity.setVideos(json[0], json[1], json[2]);
        }
    }
}
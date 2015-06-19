package com.rockbase.unplugged.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rockbase.unplugged.R;
import com.rockbase.unplugged.activities.MainActivity;
import com.rockbase.unplugged.fragments.VideoListFragment;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
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

    private static final int ERROR_DURATION = 5000;
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

    public static HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }

    protected String[][] doInBackground(String... url) {
        try {
            HttpClient client = createHttpClient();
            HttpGet clientGetMethod = new HttpGet(url[0]);
            HttpResponse clientResponse = null;
            clientResponse = client.execute(clientGetMethod);
            String infoString = _convertStreamToString(clientResponse.getEntity().getContent());
            JSONArray entries = new JSONObject(infoString).getJSONArray("items");
            int numVideos = entries.length();
            String[] videoIds = new String[numVideos];
            String[] videoTitles = new String[numVideos];
            String[] videoDescriptions = new String[numVideos];
            for (int i = 0; i < numVideos; i++) {
                JSONObject entry = entries.getJSONObject(i).getJSONObject("snippet");
                videoIds[i] = entry.getJSONObject("resourceId").getString("videoId");
                videoTitles[i] = entry.getString("title");
                videoDescriptions[i] = entry.getString("description");
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
            if (json != null) {
                this.activity.setVideos(json[0], json[1], json[2]);
            } else {
                Activity activity = this.activity.getActivity();
                Toast.makeText(activity, activity.getText(R.string.error_json), ERROR_DURATION).show();
            }
        }
    }
}
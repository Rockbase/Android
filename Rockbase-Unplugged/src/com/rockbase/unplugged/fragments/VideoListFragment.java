package com.rockbase.unplugged.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.rockbase.unplugged.DeveloperKey;
import com.rockbase.unplugged.R;
import com.rockbase.unplugged.activities.MainActivity;
import com.rockbase.unplugged.engine.PageAdapter;
import com.rockbase.unplugged.engine.VideoEntry;
import com.rockbase.unplugged.tasks.GetURLTask;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VideoListFragment extends ListFragment implements AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {

    private static final String YOUTUBE_INFO_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=_ID_&key=" + DeveloperKey.DEVELOPER_KEY_HTTP; //"http://gdata.youtube.com/feeds/api/playlists/_ID_?v=2&alt=json";

    private static final int ANIMATION_DURATION_MILLIS = 300;
    private static final int TIP_DURATION = 4000;

    private List<VideoEntry> videoList;
    private Activity activity;
    private String[] videoOptions;
    private static final int DIALOG_OPTION_VIEW_DESCRIPTION = 0;
    private static final int DIALOG_OPTION_SHARE_VIDEO = 1;

    private int selectedVideo = 0;

    private PageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getVideoList();
        prepareOptions();
    }

    private void prepareOptions() {
        videoOptions = new String[]{
                (String) getText(R.string.video_option_description),
                (String) getText(R.string.video_option_share)
        };
    }

    private void getVideoList() {
        try {
            getUrl((String) getText(R.string.rbu_playlist));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        ListView listView = this.getListView();
        listView.setDivider(null);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnItemLongClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    private String getVideoDescription(int position) {
        String videoDescription = videoList.get(position).description;

        return videoDescription;
    }

    private String getVideoId(int position) {
        String videoId = videoList.get(position).videoId;

        return videoId;
    }

    private String getVideoTitle(int position) {
        String videoTitle = videoList.get(position).title;

        return videoTitle;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        View container = activity.findViewById(R.id.video_description_container);
        if (container.getVisibility() != View.VISIBLE) {
            String videoId = getVideoId(position);
            VideoFragment videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.getView().setVisibility(View.VISIBLE);
            videoFragment.setVideoId(videoId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        adapter.releaseLoaders();
    }

    private void getUrl(String id) throws IOException, JSONException {
        GetURLTask task = new GetURLTask(this);
        task.execute(YOUTUBE_INFO_URL.replace("_ID_", id));
    }

    public void setVideos(String[] videoIds, String[] videoTitles, String[] videoDescriptions) {
        videoList = new ArrayList<VideoEntry>();
        for (int i = 0; i < videoIds.length; i++) {
            videoList.add(new VideoEntry(videoTitles[i], videoIds[i], videoDescriptions[i]));
        }
        List<VideoEntry> collection = Collections.unmodifiableList(videoList);
        adapter = new PageAdapter(activity, collection);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(adapter);
        Toast.makeText(activity, getText(R.string.long_click_tip), TIP_DURATION).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String videoId = videoList.get(position).videoId;
        if (!videoId.isEmpty()) {
            selectedVideo = position;
            this.viewVideoOptions();
            return true;
        }

        return false;
    }

    private void viewVideoOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(videoOptions, this);
        builder.create().show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int optionId) {
        switch (optionId) {
            case DIALOG_OPTION_VIEW_DESCRIPTION:
                viewDescription();
                break;
            case DIALOG_OPTION_SHARE_VIDEO:
                shareVideo();
                break;
        }
    }

    private void shareVideo() {
        String videoId = getVideoId(selectedVideo);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String url = getVideoTitle(selectedVideo) + " " + getText(R.string.youtube_video_url) + videoId;
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, getText(R.string.video_option_share)));
    }

    private void viewDescription() {
        String videoDescription = getVideoDescription(selectedVideo);
        View container = activity.findViewById(R.id.video_description_container);
        WebView descriptionContainer = (WebView) container.findViewById(R.id.video_description);
        descriptionContainer.setBackgroundColor(Color.TRANSPARENT);
        videoDescription = com.rockbase.utils.Html.parse(videoDescription);
        descriptionContainer.loadDataWithBaseURL("", videoDescription, "text/html", "UTF-8", "");
        container.setVisibility(View.VISIBLE);
    }


}

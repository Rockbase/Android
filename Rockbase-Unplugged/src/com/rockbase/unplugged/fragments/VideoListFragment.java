package com.rockbase.unplugged.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.rockbase.unplugged.R;
import com.rockbase.unplugged.engine.PageAdapter;
import com.rockbase.unplugged.engine.VideoEntry;
import com.rockbase.unplugged.tasks.GetURLTask;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VideoListFragment extends ListFragment {

    private static final String YOUTUBE_INFO_URL = "http://gdata.youtube.com/feeds/api/playlists/_ID_?v=2&alt=json";

    private static final int ANIMATION_DURATION_MILLIS = 300;

    private List<VideoEntry> videoList;
    private Activity activity;

    private PageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getVideoList();
    }

    private void getVideoList() {
        try {
            getUrl("PL4lVfCYRLO4VYNzQySkQ_u-06CvgUfdFf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        ListView listView = this.getListView();
        listView.setDivider(null);
        listView.setSelector(R.drawable.list_selector);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String videoId = videoList.get(position).videoId;
        VideoFragment videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.getView().setVisibility(View.VISIBLE);
        videoFragment.setVideoId(videoId);
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

    public void setVideos(String[] videoIds) {
        videoList = new ArrayList<VideoEntry>();
        for(int i=0; i<videoIds.length;i++){
            videoList.add(new VideoEntry("", videoIds[i]));
        }
        List<VideoEntry> collection = Collections.unmodifiableList(videoList);
        adapter = new PageAdapter(activity, collection);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(adapter);
    }

}

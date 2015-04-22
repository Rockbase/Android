package com.rockbase.unplugged.fragments;

import android.os.Bundle;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.rockbase.unplugged.DeveloperKey;
import com.rockbase.unplugged.activities.MainActivity;

public final class VideoFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (player != null) {
                player.setFullscreen(true);
                ((MainActivity) getActivity()).isPlaying = true;
                player.cueVideo(videoId);
            }
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
            player.setFullscreen(false);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
        this.player = player;
        if (!restored && videoId != null) {
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.setOnFullscreenListener((MainActivity) getActivity());
            player.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.player = null;
    }

}
/**
 * @copyright Rockbase
 * @author Alex Belencoso
 */
package com.rockbase.unplugged.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import com.rockbase.unplugged.R;
import com.rockbase.unplugged.fragments.VideoFragment;
import com.rockbase.unplugged.fragments.VideoListFragment;

@TargetApi(13)
public final class MainActivity extends Activity implements View.OnClickListener, OnFullscreenListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private VideoListFragment listFragment;
    private VideoFragment videoFragment;

    public boolean isPlaying = false;

    private boolean isFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_list);

        /** TODO Android 5
         Window window = this.getWindow();
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.setStatusBarColor(this.getResources().getColor(R.color.rb_red));
         */

        prepareButtons();

        listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);

        refreshLayout();

        checkYouTubeApi();
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoFragment.pause();
            isPlaying = false;
            isFullscreen = false;
            refreshLayout();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                View container = findViewById(R.id.video_description_container);
                if (container.getVisibility() == View.VISIBLE) {
                    hideDescription();
                    return true;
                } else if (isPlaying) {
                    isPlaying = false;
                    isFullscreen = false;
                    refreshLayout();
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkYouTubeApi() {
        Context context = this.getApplicationContext();
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(context);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Recreate the activity if user performed a recovery action
            recreate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //refreshLayout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = !this.isPlaying;
        refreshLayout();
    }

    private void prepareButton(int viewId) {
        findViewById(viewId).setOnClickListener(this);
    }

    private void prepareButtons() {
        prepareButton(R.id.rb_logo);
        prepareButton(R.id.button);
    }

    private void refreshLayout() {
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        listFragment.getView().setVisibility(!isPortrait && isPlaying ? View.GONE : View.VISIBLE);
        videoFragment.getView().setVisibility(!isPortrait && isPlaying ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);//
        } else if (isPortrait) {
            setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
        } else {
            /*
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
            */
        }
    }

    public void onClickClose(@SuppressWarnings("unused") View view) {
        listFragment.getListView().clearChoices();
        listFragment.getListView().requestLayout();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_logo:
                Intent myIntent = new Intent(MainActivity.this, CreditsActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;
            case R.id.button:
                hideDescription();
                break;
        }
    }

    private void hideDescription() {
        View container = findViewById(R.id.video_description_container);
        container.setVisibility(View.GONE);
    }

}

package com.rockbase.unplugged.engine;

import android.graphics.Rect;
import android.util.Log;
import android.widget.FrameLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.rockbase.unplugged.R;

public final class ThumbnailListener implements
        YouTubeThumbnailView.OnInitializedListener,
        YouTubeThumbnailLoader.OnThumbnailLoadedListener {

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        loader.setOnThumbnailLoadedListener(this);
        //thumbnailViewToLoaderMap.put(view, loader);
        //view.setImageResource(R.drawable.loading_thumbnail);
        String videoId = (String) view.getTag();
        loader.setVideo(videoId);
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView view, YouTubeInitializationResult loader) {
        //view.setImageResource(R.drawable.no_thumbnail);
    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        Rect sourceBounds = view.getDrawable().getBounds();
        int sourceWidth = sourceBounds.width();
        int sourceHeight = sourceBounds.height();
        int destinationWidth = view.getWidth();
        int destinationHeight = Math.round(((float) sourceHeight / sourceWidth) * destinationWidth);
        view.setMinimumHeight(destinationHeight);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(destinationWidth, destinationHeight);
        params.bottomMargin = RoundedCornerLayout.MARGIN_BOTTOM;
        view.setLayoutParams(params);
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
        //view.setImageResource(R.drawable.no_thumbnail);
    }
}
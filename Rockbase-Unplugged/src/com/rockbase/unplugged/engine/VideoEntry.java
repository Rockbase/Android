package com.rockbase.unplugged.engine;

public final class VideoEntry {
    public final String title;
    public final String videoId;
    public final String description;

    public VideoEntry(String title, String videoId, String description) {
        this.description = description;
        this.title = title;
        this.videoId = videoId;
    }
}
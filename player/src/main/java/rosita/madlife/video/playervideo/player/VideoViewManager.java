package rosita.madlife.video.playervideo.player;

import android.app.Application;

import java.util.LinkedHashMap;

import rosita.madlife.video.playervideo.util.L;


public class VideoViewManager {

    private final LinkedHashMap<String, VideoView> mVideoViews = new LinkedHashMap<>();

    private boolean mPlayOnMobileNetwork;

    private static VideoViewManager sInstance;

    private static VideoViewConfig sConfig;

    private VideoViewManager() {
        mPlayOnMobileNetwork = getConfig().mPlayOnMobileNetwork;
    }

    public static void setConfig(VideoViewConfig config) {
        if (sConfig == null) {
            synchronized (VideoViewConfig.class) {
                if (sConfig == null) {
                    sConfig = config == null ? VideoViewConfig.newBuilder().build() : config;
                }
            }
        }
    }

    public static VideoViewConfig getConfig() {
        setConfig(null);
        return sConfig;
    }

    public boolean playOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        mPlayOnMobileNetwork = playOnMobileNetwork;
    }

    public static VideoViewManager instance() {
        if (sInstance == null) {
            synchronized (VideoViewManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoViewManager();
                }
            }
        }
        return sInstance;
    }

    public void add(VideoView videoView, String tag) {
        if (!(videoView.getContext() instanceof Application)) {
            L.w("The Context of this VideoView is not an Application Context," +
                    "you must remove it after release,or it will lead to memory leek.");
        }
        VideoView old = get(tag);
        if (old != null) {
            old.release();
            remove(tag);
        }
        mVideoViews.put(tag, videoView);
    }

    public VideoView get(String tag) {
        return mVideoViews.get(tag);
    }

    public void remove(String tag) {
        mVideoViews.remove(tag);
    }

    public void removeAll() {
        mVideoViews.clear();
    }
    public void releaseByTag(String tag) {
        releaseByTag(tag, true);
    }

    public void releaseByTag(String tag, boolean isRemove) {
        VideoView videoView = get(tag);
        if (videoView != null) {
            videoView.release();
            if (isRemove) {
                remove(tag);
            }
        }
    }

    public boolean onBackPress(String tag) {
        VideoView videoView = get(tag);
        if (videoView == null) return false;
        return videoView.onBackPressed();
    }

}

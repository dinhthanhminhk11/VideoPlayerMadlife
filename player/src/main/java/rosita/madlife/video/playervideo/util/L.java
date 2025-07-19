package rosita.madlife.video.playervideo.util;

import android.util.Log;

import rosita.madlife.video.playervideo.player.VideoViewManager;

public final class L {

    private L() {
    }

    private static final String TAG = "mad_life_layer";

    private static boolean isDebug = VideoViewManager.getConfig().mIsEnableLog;


    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(TAG, msg);
        }
    }

    public static void setDebug(boolean isDebug) {
        L.isDebug = isDebug;
    }
}

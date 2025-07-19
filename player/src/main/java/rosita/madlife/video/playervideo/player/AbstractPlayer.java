package rosita.madlife.video.playervideo.player;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

public abstract class AbstractPlayer {
    public static final int MEDIA_INFO_RENDERING_START = 3;
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
    protected PlayerEventListener mPlayerEventListener;

    public abstract void initPlayer();

    public abstract void setDataSource(String path, Map<String, String> headers);

    public abstract void setDataSource(AssetFileDescriptor fd);

    public abstract void start();

    public abstract void pause();

    public abstract void stop();

    public abstract void prepareAsync();

    public abstract void reset();

    public abstract boolean isPlaying();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract int getBufferedPercentage();

    public abstract void setSurface(Surface surface);

    public abstract void setDisplay(SurfaceHolder holder);

    public abstract void setVolume(float v1, float v2);

    public abstract void setLooping(boolean isLooping);

    public abstract void setOptions();

    public abstract void setSpeed(float speed);

    public abstract float getSpeed();

    public abstract long getTcpSpeed();

    public void setPlayerEventListener(PlayerEventListener playerEventListener) {
        this.mPlayerEventListener = playerEventListener;
    }

    public interface PlayerEventListener {

        void onError();

        void onCompletion();

        void onInfo(int what, int extra);

        void onPrepared();

        void onVideoSizeChanged(int width, int height);
    }
}

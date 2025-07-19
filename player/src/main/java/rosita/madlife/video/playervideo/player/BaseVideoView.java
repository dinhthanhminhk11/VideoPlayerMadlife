package rosita.madlife.video.playervideo.player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rosita.madlife.video.playervideo.R;
import rosita.madlife.video.playervideo.controller.BaseVideoController;
import rosita.madlife.video.playervideo.controller.MediaPlayerControl;
import rosita.madlife.video.playervideo.render.IRenderView;
import rosita.madlife.video.playervideo.render.RenderViewFactory;
import rosita.madlife.video.playervideo.util.L;
import rosita.madlife.video.playervideo.util.PlayerUtils;

public class BaseVideoView<P extends AbstractPlayer> extends FrameLayout
        implements MediaPlayerControl, AbstractPlayer.PlayerEventListener {

    protected P mMediaPlayer;
    protected PlayerFactory<P> mPlayerFactory;
    @Nullable
    protected BaseVideoController mVideoController;

    protected FrameLayout mPlayerContainer;

    protected IRenderView mRenderView;
    protected RenderViewFactory mRenderViewFactory;

    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    public static final int SCREEN_SCALE_ORIGINAL = 4;
    public static final int SCREEN_SCALE_CENTER_CROP = 5;
    protected int mCurrentScreenScaleType;

    protected int[] mVideoSize = {0, 0};

    protected boolean mIsMute;

    protected String mUrl;
    protected Map<String, String> mHeaders;
    protected AssetFileDescriptor mAssetFileDescriptor;

    protected long mCurrentPosition;

    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    public static final int STATE_START_ABORT = 8;
    protected int mCurrentPlayState = STATE_IDLE;

    public static final int PLAYER_NORMAL = 10;
    public static final int PLAYER_FULL_SCREEN = 11;
    public static final int PLAYER_TINY_SCREEN = 12;
    protected int mCurrentPlayerState = PLAYER_NORMAL;

    protected boolean mIsFullScreen;

    protected boolean mIsTinyScreen;
    protected int[] mTinyScreenSize = {0, 0};

    protected boolean mEnableAudioFocus;
    @Nullable
    protected AudioFocusHelper mAudioFocusHelper;

    protected List<OnStateChangeListener> mOnStateChangeListeners;

    @Nullable
    protected ProgressManager mProgressManager;

    protected boolean mIsLooping;

    private final int mPlayerBackgroundColor;

    public BaseVideoView(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        VideoViewConfig config = VideoViewManager.getConfig();
        mEnableAudioFocus = config.mEnableAudioFocus;
        mProgressManager = config.mProgressManager;
        mPlayerFactory = config.mPlayerFactory;
        mCurrentScreenScaleType = config.mScreenScaleType;
        mRenderViewFactory = config.mRenderViewFactory;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseVideoView);
        mEnableAudioFocus = a.getBoolean(R.styleable.BaseVideoView_enableAudioFocus, mEnableAudioFocus);
        mIsLooping = a.getBoolean(R.styleable.BaseVideoView_looping, false);
        mCurrentScreenScaleType = a.getInt(R.styleable.BaseVideoView_screenScaleType, mCurrentScreenScaleType);
        mPlayerBackgroundColor = a.getColor(R.styleable.BaseVideoView_playerBackgroundColor, Color.BLACK);
        a.recycle();

        initView();
    }

    protected void initView() {
        mPlayerContainer = new FrameLayout(getContext());
        mPlayerContainer.setBackgroundColor(mPlayerBackgroundColor);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);
    }

    public void setPlayerBackgroundColor(int color) {
        mPlayerContainer.setBackgroundColor(color);
    }

    @Override
    public void start() {
        if (isInIdleState()
                || isInStartAbortState()) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
    }

    protected boolean startPlay() {
        if (showNetWarning()) {
            setPlayState(STATE_START_ABORT);
            return false;
        }
        if (mEnableAudioFocus) {
            mAudioFocusHelper = new AudioFocusHelper(this);
        }
        if (mProgressManager != null) {
            mCurrentPosition = mProgressManager.getSavedProgress(mUrl);
        }
        initPlayer();
        addDisplay();
        startPrepare(false);
        return true;
    }

    protected boolean showNetWarning() {
        if (isLocalDataSource()) return false;
        return mVideoController != null && mVideoController.showNetWarning();
    }

    protected boolean isLocalDataSource() {
        if (mAssetFileDescriptor != null) {
            return true;
        } else if (!TextUtils.isEmpty(mUrl)) {
            Uri uri = Uri.parse(mUrl);
            return ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())
                    || ContentResolver.SCHEME_FILE.equals(uri.getScheme())
                    || "rawresource".equals(uri.getScheme());
        }
        return false;
    }

    protected void initPlayer() {
        mMediaPlayer = mPlayerFactory.createPlayer(getContext());
        mMediaPlayer.setPlayerEventListener(this);
        setInitOptions();
        mMediaPlayer.initPlayer();
        setOptions();
    }

    protected void setInitOptions() {
    }

    protected void setOptions() {
        mMediaPlayer.setLooping(mIsLooping);
        float volume = mIsMute ? 0.0f : 1.0f;
        mMediaPlayer.setVolume(volume, volume);
    }

    protected void addDisplay() {
        if (mRenderView != null) {
            mPlayerContainer.removeView(mRenderView.getView());
            mRenderView.release();
        }
        mRenderView = mRenderViewFactory.createRenderView(getContext());
        mRenderView.attachToPlayer(mMediaPlayer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mPlayerContainer.addView(mRenderView.getView(), 0, params);
    }

    protected void startPrepare(boolean reset) {
        if (reset) {
            mMediaPlayer.reset();
            setOptions();
        }
        if (prepareDataSource()) {
            mMediaPlayer.prepareAsync();
            setPlayState(STATE_PREPARING);
            setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : isTinyScreen() ? PLAYER_TINY_SCREEN : PLAYER_NORMAL);
        }
    }

    protected boolean prepareDataSource() {
        if (mAssetFileDescriptor != null) {
            mMediaPlayer.setDataSource(mAssetFileDescriptor);
            return true;
        } else if (!TextUtils.isEmpty(mUrl)) {
            mMediaPlayer.setDataSource(mUrl, mHeaders);
            return true;
        }
        return false;
    }

    protected void startInPlaybackState() {
        mMediaPlayer.start();
        setPlayState(STATE_PLAYING);
        if (mAudioFocusHelper != null && !isMute()) {
            mAudioFocusHelper.requestFocus();
        }
        mPlayerContainer.setKeepScreenOn(true);
    }

    @Override
    public void pause() {
        if (isInPlaybackState()
                && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setPlayState(STATE_PAUSED);
            if (mAudioFocusHelper != null && !isMute()) {
                mAudioFocusHelper.abandonFocus();
            }
            mPlayerContainer.setKeepScreenOn(false);
        }
    }

    public void resume() {
        if (isInPlaybackState()
                && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setPlayState(STATE_PLAYING);
            if (mAudioFocusHelper != null && !isMute()) {
                mAudioFocusHelper.requestFocus();
            }
            mPlayerContainer.setKeepScreenOn(true);
        }
    }

    public void release() {
        if (!isInIdleState()) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (mRenderView != null) {
                mPlayerContainer.removeView(mRenderView.getView());
                mRenderView.release();
                mRenderView = null;
            }
            if (mAssetFileDescriptor != null) {
                try {
                    mAssetFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mAudioFocusHelper != null) {
                mAudioFocusHelper.abandonFocus();
                mAudioFocusHelper = null;
            }
            mPlayerContainer.setKeepScreenOn(false);
            saveProgress();
            mCurrentPosition = 0;
            setPlayState(STATE_IDLE);
        }
    }

    protected void saveProgress() {
        if (mProgressManager != null && mCurrentPosition > 0) {
            L.d("saveProgress: " + mCurrentPosition);
            mProgressManager.saveProgress(mUrl, mCurrentPosition);
        }
    }

    protected boolean isInPlaybackState() {
        return mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_START_ABORT
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED;
    }

    protected boolean isInIdleState() {
        return mCurrentPlayState == STATE_IDLE;
    }

    private boolean isInStartAbortState() {
        return mCurrentPlayState == STATE_START_ABORT;
    }

    @Override
    public void replay(boolean resetPosition) {
        if (resetPosition) {
            mCurrentPosition = 0;
        }
        addDisplay();
        startPrepare(true);
    }

    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    @Override
    public void seekTo(long pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    @Override
    public void setMute(boolean isMute) {
        this.mIsMute = isMute;
        if (mMediaPlayer != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public boolean isMute() {
        return mIsMute;
    }

    @Override
    public void onPrepared() {
        setPlayState(STATE_PREPARED);
        if (!isMute() && mAudioFocusHelper != null) {
            mAudioFocusHelper.requestFocus();
        }
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_BUFFERING_START:
                setPlayState(STATE_BUFFERING);
                break;
            case AbstractPlayer.MEDIA_INFO_BUFFERING_END:
                setPlayState(STATE_BUFFERED);
                break;
            case AbstractPlayer.MEDIA_INFO_RENDERING_START:
                setPlayState(STATE_PLAYING);
                mPlayerContainer.setKeepScreenOn(true);
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mRenderView != null) mRenderView.setVideoRotation(extra);
                break;
        }
    }

    @Override
    public void onError() {
        mPlayerContainer.setKeepScreenOn(false);
        setPlayState(STATE_ERROR);
    }

    @Override
    public void onCompletion() {
        mPlayerContainer.setKeepScreenOn(false);
        mCurrentPosition = 0;
        if (mProgressManager != null) {
            mProgressManager.saveProgress(mUrl, 0);
        }
        setPlayState(STATE_PLAYBACK_COMPLETED);
    }

    public int getCurrentPlayerState() {
        return mCurrentPlayerState;
    }

    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayer != null ? mMediaPlayer.getTcpSpeed() : 0;
    }

    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public float getSpeed() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getSpeed();
        }
        return 1f;
    }

    public void setUrl(String url) {
        setUrl(url, null);
    }

    public void setUrl(String url, Map<String, String> headers) {
        mAssetFileDescriptor = null;
        mUrl = url;
        mHeaders = headers;
    }

    public void setAssetFileDescriptor(AssetFileDescriptor fd) {
        mUrl = null;
        this.mAssetFileDescriptor = fd;
    }

    public void skipPositionWhenPlay(int position) {
        this.mCurrentPosition = position;
    }

    public void setVolume(float v1, float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    public void setProgressManager(@Nullable ProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    public void setEnableAudioFocus(boolean enableAudioFocus) {
        mEnableAudioFocus = enableAudioFocus;
    }


    public void setPlayerFactory(PlayerFactory playerFactory) {
        if (playerFactory == null) {
            throw new IllegalArgumentException("PlayerFactory can not be null!");
        }
        mPlayerFactory = playerFactory;
    }

    public void setRenderViewFactory(RenderViewFactory renderViewFactory) {
        if (renderViewFactory == null) {
            throw new IllegalArgumentException("RenderViewFactory can not be null!");
        }
        mRenderViewFactory = renderViewFactory;
    }

    @Override
    public void startFullScreen() {
        if (mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = true;

        hideSysBar(decorView);

        this.removeView(mPlayerContainer);
        decorView.addView(mPlayerContainer);

        setPlayerState(PLAYER_FULL_SCREEN);
    }

    private void hideSysBar(ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mIsFullScreen) {
            //重新获得焦点时保持全屏状态
            hideSysBar(getDecorView());
        }
    }

    @Override
    public void stopFullScreen() {
        if (!mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = false;

        showSysBar(decorView);

        decorView.removeView(mPlayerContainer);
        this.addView(mPlayerContainer);

        setPlayerState(PLAYER_NORMAL);
    }

    private void showSysBar(ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected ViewGroup getDecorView() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    protected ViewGroup getContentView() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return activity.findViewById(android.R.id.content);
    }

    protected Activity getActivity() {
        Activity activity;
        if (mVideoController != null) {
            activity = PlayerUtils.scanForActivity(mVideoController.getContext());
            if (activity == null) {
                activity = PlayerUtils.scanForActivity(getContext());
            }
        } else {
            activity = PlayerUtils.scanForActivity(getContext());
        }
        return activity;
    }

    @Override
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    public void startTinyScreen() {
        if (mIsTinyScreen) return;
        ViewGroup contentView = getContentView();
        if (contentView == null) return;
        this.removeView(mPlayerContainer);
        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = PlayerUtils.getScreenWidth(getContext(), false) / 2;
        }

        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = width * 9 / 16;
        }

        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        contentView.addView(mPlayerContainer, params);
        mIsTinyScreen = true;
        setPlayerState(PLAYER_TINY_SCREEN);
    }

    public void stopTinyScreen() {
        if (!mIsTinyScreen) return;

        ViewGroup contentView = getContentView();
        if (contentView == null) return;
        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);

        mIsTinyScreen = false;
        setPlayerState(PLAYER_NORMAL);
    }

    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mVideoSize[0] = videoWidth;
        mVideoSize[1] = videoHeight;

        if (mRenderView != null) {
            mRenderView.setScaleType(mCurrentScreenScaleType);
            mRenderView.setVideoSize(videoWidth, videoHeight);
        }
    }

    public void setVideoController(@Nullable BaseVideoController mediaController) {
        mPlayerContainer.removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.addView(mVideoController, params);
        }
    }

    @Override
    public void setScreenScaleType(int screenScaleType) {
        mCurrentScreenScaleType = screenScaleType;
        if (mRenderView != null) {
            mRenderView.setScaleType(screenScaleType);
        }
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        if (mRenderView != null) {
            mRenderView.getView().setScaleX(enable ? -1 : 1);
        }
    }

    @Override
    public Bitmap doScreenShot() {
        if (mRenderView != null) {
            return mRenderView.doScreenShot();
        }
        return null;
    }

    @Override
    public int[] getVideoSize() {
        return mVideoSize;
    }

    @Override
    public void setRotation(float rotation) {
        if (mRenderView != null) {
            mRenderView.setVideoRotation((int) rotation);
        }
    }

    public void setTinyScreenSize(int[] tinyScreenSize) {
        this.mTinyScreenSize = tinyScreenSize;
    }

    protected void setPlayState(int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null) {
            mVideoController.setPlayState(playState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnStateChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onPlayStateChanged(playState);
                }
            }
        }
    }

    protected void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
        if (mVideoController != null) {
            mVideoController.setPlayerState(playerState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnStateChangeListener l : PlayerUtils.getSnapshot(mOnStateChangeListeners)) {
                if (l != null) {
                    l.onPlayerStateChanged(playerState);
                }
            }
        }
    }

    public interface OnStateChangeListener {
        void onPlayerStateChanged(int playerState);
        void onPlayStateChanged(int playState);
    }

    public static class SimpleOnStateChangeListener implements OnStateChangeListener {
        @Override
        public void onPlayerStateChanged(int playerState) {}
        @Override
        public void onPlayStateChanged(int playState) {}
    }

    public void addOnStateChangeListener(@NonNull OnStateChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }
        mOnStateChangeListeners.add(listener);
    }

    public void removeOnStateChangeListener(@NonNull OnStateChangeListener listener) {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.remove(listener);
        }
    }

    public void setOnStateChangeListener(@NonNull OnStateChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        } else {
            mOnStateChangeListeners.clear();
        }
        mOnStateChangeListeners.add(listener);
    }

    public void clearOnStateChangeListeners() {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.clear();
        }
    }

    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        L.d("onSaveInstanceState: " + mCurrentPosition);
        saveProgress();
        return super.onSaveInstanceState();
    }
}

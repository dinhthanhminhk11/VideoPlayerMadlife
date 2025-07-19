package rosita.madlife.video.playervideo.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import rosita.madlife.video.playervideo.player.VideoView;
import rosita.madlife.video.playervideo.player.VideoViewManager;
import rosita.madlife.video.playervideo.util.CutoutUtil;
import rosita.madlife.video.playervideo.util.L;
import rosita.madlife.video.playervideo.util.PlayerUtils;

public abstract class BaseVideoController extends FrameLayout
        implements IVideoController,
        OrientationHelper.OnOrientationChangeListener {

    protected ControlWrapper mControlWrapper;

    @Nullable
    protected Activity mActivity;

    protected boolean mShowing;

    protected boolean mIsLocked;

    protected int mDefaultTimeout = 4000;

    private boolean mEnableOrientation;
    protected OrientationHelper mOrientationHelper;

    private boolean mAdaptCutout;
    private Boolean mHasCutout;
    private int mCutoutHeight;

    private boolean mIsStartProgress;

    protected LinkedHashMap<IControlComponent, Boolean> mControlComponents = new LinkedHashMap<>();

    private Animation mShowAnim;
    private Animation mHideAnim;

    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        }
        mOrientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = VideoViewManager.getConfig().mEnableOrientation;
        mAdaptCutout = VideoViewManager.getConfig().mAdaptCutout;

        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);

        mActivity = PlayerUtils.scanForActivity(getContext());
    }


    protected abstract int getLayoutId();

    @CallSuper
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        mControlWrapper = new ControlWrapper(mediaPlayer, this);
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.attach(mControlWrapper);
        }
        mOrientationHelper.setOnOrientationChangeListener(this);
    }

    public void addControlComponent(IControlComponent... component) {
        for (IControlComponent item : component) {
            addControlComponent(item, false);
        }
    }

    public void addControlComponent(IControlComponent component, boolean isDissociate) {
        mControlComponents.put(component, isDissociate);
        if (mControlWrapper != null) {
            component.attach(mControlWrapper);
        }
        View view = component.getView();
        if (view != null && !isDissociate) {
            addView(view, 0);
        }
    }

    public void removeControlComponent(IControlComponent component) {
        removeView(component.getView());
        mControlComponents.remove(component);
    }

    public void removeAllControlComponent() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            removeView(next.getKey().getView());
        }
        mControlComponents.clear();
    }

    public void removeAllDissociateComponents() {
        Iterator<Map.Entry<IControlComponent, Boolean>> it = mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<IControlComponent, Boolean> next = it.next();
            if (next.getValue()) {
                it.remove();
            }
        }
    }

    @CallSuper
    public void setPlayState(int playState) {
        handlePlayStateChanged(playState);
    }

    @CallSuper
    public void setPlayerState(final int playerState) {
        handlePlayerStateChanged(playerState);
    }

    public void setDismissTimeout(int timeout) {
        if (timeout > 0) {
            mDefaultTimeout = timeout;
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            stopFadeOut();
            handleVisibilityChanged(false, mHideAnim);
            mShowing = false;
        }
    }

    @Override
    public void show() {
        if (!mShowing) {
            handleVisibilityChanged(true, mShowAnim);
            startFadeOut();
            mShowing = true;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void startFadeOut() {
        //重新开始计时
        stopFadeOut();
        postDelayed(mFadeOut, mDefaultTimeout);
    }

    @Override
    public void stopFadeOut() {
        removeCallbacks(mFadeOut);
    }

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    public void setLocked(boolean locked) {
        mIsLocked = locked;
        handleLockStateChanged(locked);
    }

    @Override
    public boolean isLocked() {
        return mIsLocked;
    }

    @Override
    public void startProgress() {
        if (mIsStartProgress) return;
        post(mShowProgress);
        mIsStartProgress = true;
    }

    @Override
    public void stopProgress() {
        if (!mIsStartProgress) return;
        removeCallbacks(mShowProgress);
        mIsStartProgress = false;
    }

    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mControlWrapper.isPlaying()) {
                postDelayed(this, (long) ((1000 - pos % 1000) / mControlWrapper.getSpeed()));
            } else {
                mIsStartProgress = false;
            }
        }
    };

    private int setProgress() {
        int position = (int) mControlWrapper.getCurrentPosition();
        int duration = (int) mControlWrapper.getDuration();
        handleSetProgress(duration, position);
        return position;
    }

    public void setAdaptCutout(boolean adaptCutout) {
        mAdaptCutout = adaptCutout;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkCutout();
    }

    private void checkCutout() {
        if (!mAdaptCutout) return;
        if (mActivity != null && mHasCutout == null) {
            mHasCutout = CutoutUtil.allowDisplayToCutout(mActivity);
            if (mHasCutout) {
                mCutoutHeight = (int) PlayerUtils.getStatusBarHeightPortrait(mActivity);
            }
        }
        L.d("hasCutout: " + mHasCutout + " cutout height: " + mCutoutHeight);
    }

    @Override
    public boolean hasCutout() {
        return mHasCutout != null && mHasCutout;
    }

    @Override
    public int getCutoutHeight() {
        return mCutoutHeight;
    }

    public boolean showNetWarning() {
        return PlayerUtils.getNetworkType(getContext()) == PlayerUtils.NETWORK_MOBILE
                && !VideoViewManager.instance().playOnMobileNetwork();
    }

    protected void togglePlay() {
        mControlWrapper.togglePlay();
    }

    protected void toggleFullScreen() {
        mControlWrapper.toggleFullScreen(mActivity);
    }

    protected boolean startFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mControlWrapper.startFullScreen();
        return true;
    }

    protected boolean stopFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControlWrapper.stopFullScreen();
        return true;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mControlWrapper.isPlaying()
                && (mEnableOrientation || mControlWrapper.isFullScreen())) {
            if (hasWindowFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOrientationHelper.enable();
                    }
                }, 800);
            } else {
                mOrientationHelper.disable();
            }
        }
    }

    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    private int mOrientation = 0;

    @CallSuper
    @Override
    public void onOrientationChanged(int orientation) {
        if (mActivity == null || mActivity.isFinishing()) return;

        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            int o = mActivity.getRequestedOrientation();
            if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == 0) return;
            if (mOrientation == 0) return;
            mOrientation = 0;
            onOrientationPortrait(mActivity);
        } else if (orientation > 80 && orientation < 100) {

            int o = mActivity.getRequestedOrientation();
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) return;
            if (mOrientation == 90) return;
            mOrientation = 90;
            onOrientationReverseLandscape(mActivity);
        } else if (orientation > 260 && orientation < 280) {
            int o = mActivity.getRequestedOrientation();
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) return;
            if (mOrientation == 270) return;
            mOrientation = 270;
            onOrientationLandscape(mActivity);
        }
    }

    protected void onOrientationPortrait(Activity activity) {
        if (mIsLocked) return;
        if (!mEnableOrientation) return;

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControlWrapper.stopFullScreen();
    }

    protected void onOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoView.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.startFullScreen();
        }
    }

    protected void onOrientationReverseLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoView.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.startFullScreen();
        }
    }

    private void handleVisibilityChanged(boolean isVisible, Animation anim) {
        if (!mIsLocked) {
            for (Map.Entry<IControlComponent, Boolean> next
                    : mControlComponents.entrySet()) {
                IControlComponent component = next.getKey();
                component.onVisibilityChanged(isVisible, anim);
            }
        }
        onVisibilityChanged(isVisible, anim);
    }

    protected void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    private void handlePlayStateChanged(int playState) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onPlayStateChanged(playState);
        }
        onPlayStateChanged(playState);
    }

    @CallSuper
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                mOrientationHelper.disable();
                mOrientation = 0;
                mIsLocked = false;
                mShowing = false;
                removeAllDissociateComponents();
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                mIsLocked = false;
                mShowing = false;
                break;
            case VideoView.STATE_ERROR:
                mShowing = false;
                break;
        }
    }

    private void handlePlayerStateChanged(int playerState) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onPlayerStateChanged(playerState);
        }
        onPlayerStateChanged(playerState);
    }

    @CallSuper
    protected void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                if (mEnableOrientation) {
                    mOrientationHelper.enable();
                } else {
                    mOrientationHelper.disable();
                }
                if (hasCutout()) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), false);
                }
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mOrientationHelper.enable();
                if (hasCutout()) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), true);
                }
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                mOrientationHelper.disable();
                break;
        }
    }

    private void handleSetProgress(int duration, int position) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.setProgress(duration, position);
        }
        setProgress(duration, position);
    }

    protected void setProgress(int duration, int position) {

    }

    private void handleLockStateChanged(boolean isLocked) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onLockStateChanged(isLocked);
        }
        onLockStateChanged(isLocked);
    }

    protected void onLockStateChanged(boolean isLocked) {

    }
}

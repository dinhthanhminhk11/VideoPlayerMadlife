package rosita.madlife.video.playervideo.player;


import androidx.annotation.Nullable;

import rosita.madlife.video.playervideo.render.RenderViewFactory;
import rosita.madlife.video.playervideo.render.TextureRenderViewFactory;

public class VideoViewConfig {

    public static Builder newBuilder() {
        return new Builder();
    }

    public final boolean mPlayOnMobileNetwork;

    public final boolean mEnableOrientation;

    public final boolean mEnableAudioFocus;

    public final boolean mIsEnableLog;

    public final ProgressManager mProgressManager;

    public final PlayerFactory mPlayerFactory;

    public final int mScreenScaleType;

    public final RenderViewFactory mRenderViewFactory;

    public final boolean mAdaptCutout;

    private VideoViewConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableOrientation = builder.mEnableOrientation;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mProgressManager = builder.mProgressManager;
        mScreenScaleType = builder.mScreenScaleType;
        if (builder.mPlayerFactory == null) {
            mPlayerFactory = AndroidMediaPlayerFactory.create();
        } else {
            mPlayerFactory = builder.mPlayerFactory;
        }
        if (builder.mRenderViewFactory == null) {
            mRenderViewFactory = TextureRenderViewFactory.create();
        } else {
            mRenderViewFactory = builder.mRenderViewFactory;
        }
        mAdaptCutout = builder.mAdaptCutout;
    }


    public final static class Builder {

        private boolean mIsEnableLog;
        private boolean mPlayOnMobileNetwork = true;
        private boolean mEnableOrientation;
        private boolean mEnableAudioFocus = true;
        private ProgressManager mProgressManager;
        private PlayerFactory mPlayerFactory;
        private int mScreenScaleType;
        private RenderViewFactory mRenderViewFactory;
        private boolean mAdaptCutout = true;

        public Builder setEnableOrientation(boolean enableOrientation) {
            mEnableOrientation = enableOrientation;
            return this;
        }

        public Builder setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
            mPlayOnMobileNetwork = playOnMobileNetwork;
            return this;
        }

        public Builder setEnableAudioFocus(boolean enableAudioFocus) {
            mEnableAudioFocus = enableAudioFocus;
            return this;
        }

        public Builder setProgressManager(@Nullable ProgressManager progressManager) {
            mProgressManager = progressManager;
            return this;
        }

        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        public Builder setPlayerFactory(PlayerFactory playerFactory) {
            mPlayerFactory = playerFactory;
            return this;
        }

        public Builder setScreenScaleType(int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        public Builder setRenderViewFactory(RenderViewFactory renderViewFactory) {
            mRenderViewFactory = renderViewFactory;
            return this;
        }

        public Builder setAdaptCutout(boolean adaptCutout) {
            mAdaptCutout = adaptCutout;
            return this;
        }

        public VideoViewConfig build() {
            return new VideoViewConfig(this);
        }
    }
}

package rosita.madlife.video.playervideo.controller;

public interface IVideoController {

    void startFadeOut();

    void stopFadeOut();

    boolean isShowing();

    void setLocked(boolean locked);

    boolean isLocked();

    void startProgress();

    void stopProgress();

    void hide();

    void show();

    boolean hasCutout();

    int getCutoutHeight();
}

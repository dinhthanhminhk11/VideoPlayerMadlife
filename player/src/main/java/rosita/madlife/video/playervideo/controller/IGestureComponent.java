package rosita.madlife.video.playervideo.controller;

public interface IGestureComponent extends IControlComponent {

    void onStartSlide();

    void onStopSlide();

    void onPositionChange(int slidePosition, int currentPosition, int duration);

    void onBrightnessChange(int percent);

    void onVolumeChange(int percent);
}

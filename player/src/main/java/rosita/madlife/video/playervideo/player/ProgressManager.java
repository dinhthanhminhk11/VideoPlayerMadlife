package rosita.madlife.video.playervideo.player;

public abstract class ProgressManager {
    public abstract void saveProgress(String url, long progress);

    public abstract long getSavedProgress(String url);

}

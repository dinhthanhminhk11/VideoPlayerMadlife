package rosita.madlife.video.videocache.sourcestorage;


import rosita.madlife.video.videocache.SourceInfo;

public class NoSourceInfoStorage implements SourceInfoStorage {

    @Override
    public SourceInfo get(String url) {
        return null;
    }

    @Override
    public void put(String url, SourceInfo sourceInfo) {
    }

    @Override
    public void release() {
    }
}

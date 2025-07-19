package rosita.madlife.video.videocache.sourcestorage;


import rosita.madlife.video.videocache.SourceInfo;

public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}

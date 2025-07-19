package rosita.madlife.video.videocache;


public interface Source {


    void open(long offset) throws ProxyCacheException;


    long length() throws ProxyCacheException;


    int read(byte[] buffer) throws ProxyCacheException;


    void close() throws ProxyCacheException;
}

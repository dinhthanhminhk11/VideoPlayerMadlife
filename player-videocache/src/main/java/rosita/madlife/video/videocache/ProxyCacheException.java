package rosita.madlife.video.videocache;


public class ProxyCacheException extends Exception {

    public ProxyCacheException(String message) {
        super(message);
    }

    public ProxyCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyCacheException(Throwable cause) {
        super("No explanation error", cause);
    }
}

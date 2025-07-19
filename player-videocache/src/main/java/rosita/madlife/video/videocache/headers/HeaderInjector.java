package rosita.madlife.video.videocache.headers;

import java.util.Map;


public interface HeaderInjector {


    Map<String, String> addHeaders(String url);

}

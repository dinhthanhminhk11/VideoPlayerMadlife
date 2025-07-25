package rosita.madlife.video.videocache;


import static rosita.madlife.video.videocache.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


class IgnoreHostProxySelector extends ProxySelector {

    private static final List<Proxy> NO_PROXY_LIST = Arrays.asList(Proxy.NO_PROXY);

    private final ProxySelector defaultProxySelector;
    private final String hostToIgnore;
    private final int portToIgnore;

    IgnoreHostProxySelector(ProxySelector defaultProxySelector, String hostToIgnore, int portToIgnore) {
        this.defaultProxySelector = checkNotNull(defaultProxySelector);
        this.hostToIgnore = checkNotNull(hostToIgnore);
        this.portToIgnore = portToIgnore;
    }

    static void install(String hostToIgnore, int portToIgnore) {
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        ProxySelector ignoreHostProxySelector = new IgnoreHostProxySelector(defaultProxySelector, hostToIgnore, portToIgnore);
        ProxySelector.setDefault(ignoreHostProxySelector);
    }

    @Override
    public List<Proxy> select(URI uri) {
        boolean ignored = hostToIgnore.equals(uri.getHost()) && portToIgnore == uri.getPort();
        return ignored ? NO_PROXY_LIST : defaultProxySelector.select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
        defaultProxySelector.connectFailed(uri, address, failure);
    }
}

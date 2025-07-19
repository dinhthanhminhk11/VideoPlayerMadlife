package rosita.madlife.video.videocache.file;

import java.io.File;
import java.io.IOException;


public class UnlimitedDiskUsage implements DiskUsage {

    @Override
    public void touch(File file) throws IOException {
        // do nothing
    }
}

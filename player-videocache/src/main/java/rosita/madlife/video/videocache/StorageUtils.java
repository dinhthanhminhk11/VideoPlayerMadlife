package rosita.madlife.video.videocache;

import static android.os.Environment.MEDIA_MOUNTED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;


public final class StorageUtils {

    private static final String INDIVIDUAL_DIR_NAME = "video-cache";

    static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        return new File(cacheDir, INDIVIDUAL_DIR_NAME);
    }


    private static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = context.getExternalCacheDir();
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            @SuppressLint("SdCardPath") String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    public static boolean deleteFiles(File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() && f.exists()) {
                    if (!f.delete()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.isFile()) {
                String[] filePaths = file.list();

                if (filePaths != null) {
                    for (String path : filePaths) {
                        deleteFile(filePath + File.separator + path);
                    }
                }
            }
            return file.delete();
        }
        return true;
    }
}

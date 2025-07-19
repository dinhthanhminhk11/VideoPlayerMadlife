package rosita.madlife.video.playervideo.render;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

import rosita.madlife.video.playervideo.player.AbstractPlayer;


public interface IRenderView {

    void attachToPlayer(@NonNull AbstractPlayer player);

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoRotation(int degree);

    void setScaleType(int scaleType);

    View getView();

    Bitmap doScreenShot();

    void release();

}
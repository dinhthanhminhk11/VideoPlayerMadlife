package rosita.madlife.video.videoplayer.exo;

import android.content.Context;

import androidx.media3.common.util.UnstableApi;

import rosita.madlife.video.playervideo.player.PlayerFactory;


public class ExoMediaPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @UnstableApi
    @Override
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}

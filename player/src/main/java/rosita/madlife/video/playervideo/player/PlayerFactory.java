package rosita.madlife.video.playervideo.player;

import android.content.Context;


public abstract class PlayerFactory<P extends AbstractPlayer> {

    public abstract P createPlayer(Context context);
}

package ma.algobot;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


class Sound extends SoundPool {
    private final int click_t;
    private final int click_r;
    Sound(Context context) {
        super(4, AudioManager.STREAM_MUSIC,0);
        click_r = this.load(context,R.raw.r,1);
        click_t = this.load(context,R.raw.t,1);
    }
    int click_t(){
        return this.play(click_t,0.3f,0.3f,1,0,1f);
    }
    int click_f() { return this.play(click_r,0.7f,0.7f,1,0,1f); }
}


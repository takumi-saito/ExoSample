package com.example.saito.exosample;

import android.app.Application;

import com.example.saito.exosample.WrapExoPlayer;

/**
 * Created by t-saito on 16/08/03.
 */
public class ExoPlayerApplication extends Application {

    private WrapExoPlayer wrapExoPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // プレイヤーを作成しとく
        wrapExoPlayer = new WrapExoPlayer();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public WrapExoPlayer getWrapExoPlayer() {
        return wrapExoPlayer;
    }

    public void setWrapExoPlayer(WrapExoPlayer wrapExoPlayer) {
        this.wrapExoPlayer = wrapExoPlayer;
    }
}

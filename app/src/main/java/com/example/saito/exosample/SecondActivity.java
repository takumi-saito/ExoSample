package com.example.saito.exosample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.saito.exosample.WrapExoPlayer;
import com.google.android.exoplayer.util.Util;

import java.io.File;

public class SecondActivity extends AppCompatActivity {

    ExoPlayerApplication exoPlayerApplication;
    WrapExoPlayer mWrapExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        setContentView(R.layout.activity_main);

        // プレイヤーを引き継ぎ
        exoPlayerApplication = ((ExoPlayerApplication)getApplication());
        mWrapExoPlayer = exoPlayerApplication.getWrapExoPlayer();

        CheckBox backgroudPlayCheckBox = (CheckBox) findViewById(R.id.background_playback_check);
        backgroudPlayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // バックグラウンド再生フラグ設定
                mWrapExoPlayer.setEnableBackgroundAudio(isChecked);
            }
        });

        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(getClass().getSimpleName());

        Button nextActivityButton = (Button) findViewById(R.id.next_activity_btn);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 画面遷移
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ThirdActivity.class);
                startActivity(intent);
            }
        });

        String filePath1 = Environment.getExternalStorageDirectory().toString() + Constants.MOVIE_PATH[0];
        String filePath2 = Environment.getExternalStorageDirectory().toString() + Constants.MOVIE_PATH[1];
        File file = new File(filePath1);
        Log.v("saito", "file:" + file.getAbsolutePath() + (file.exists() ? "ファイルあり" : "ファイルなし"));
        file = new File(filePath2);
        Log.v("saito", "file:" + file.getAbsolutePath() + (file.exists() ? "ファイルあり" : "ファイルなし"));


        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.video_view);
        // 映像を表示させるviewの設定
        mWrapExoPlayer.setSurfaceView(surfaceView);
        mWrapExoPlayer.pushSurface(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        if (Util.SDK_INT > 23) {
            mWrapExoPlayer.onShown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        if (Util.SDK_INT <= 23 || mWrapExoPlayer != null) {
            mWrapExoPlayer.onShown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        mWrapExoPlayer.onHidden();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        if (Util.SDK_INT > 23) {
            // 24 N 以上の場合
            mWrapExoPlayer.onHidden();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }
}

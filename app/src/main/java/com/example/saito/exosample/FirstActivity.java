package com.example.saito.exosample;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.util.Util;

import java.io.File;


public class FirstActivity extends AppCompatActivity {

    WrapExoPlayer mWrapExoPlayer;
    ExoPlayerApplication exoPlayerApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
        setContentView(R.layout.activity_main);

        // プレイヤーを引き継ぎ
        exoPlayerApplication = ((com.example.saito.exosample.ExoPlayerApplication) this.getApplication());
        mWrapExoPlayer =  exoPlayerApplication.getWrapExoPlayer();
        CheckBox backgroudPlayCheckBox = (CheckBox) findViewById(R.id.background_playback_check);
        backgroudPlayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // バックグラウンド再生フラグ設定
                mWrapExoPlayer.setEnableBackgroundAudio(isChecked);
            }
        });
        backgroudPlayCheckBox.setChecked(mWrapExoPlayer.isEnableBackgroundAudio());

        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(getClass().getSimpleName());

        Button nextActivityButton = (Button) findViewById(R.id.next_activity_btn);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exoPlayerApplication.setWrapExoPlayer(mWrapExoPlayer);
                // 画面遷移
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }
        });

        String filePath = Environment.getExternalStorageDirectory().toString() + Constants.MOVIE_PATH[1];
        File file = new File(filePath);
        Toast.makeText(getApplicationContext(), "file:" + file.getAbsolutePath() + (file.exists() ? "を再生します" : "が存在しません"), Toast.LENGTH_SHORT).show();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.video_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(createSurfaceHolderCallback());
        // コンテンツのソースをセット
        mWrapExoPlayer.setContentUri(filePath);
        // 映像を表示させるviewの設定
//        mWrapExoPlayer.setSurfaceView(surfaceView);
        mWrapExoPlayer.initExoPlayer(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
    }

    private SurfaceHolder.Callback createSurfaceHolderCallback() {
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
                mWrapExoPlayer.setSurface(holder.getSurface());
                // ExoPlayerがなかったらnewする
                mWrapExoPlayer.onShown();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.v(getClass().getSimpleName(), new Throwable().getStackTrace()[0].getMethodName());
                // サーフェースが破棄されたら映像レンダリングを止める
                mWrapExoPlayer.onHidden();
            }
        };
        return callback;
    }
}

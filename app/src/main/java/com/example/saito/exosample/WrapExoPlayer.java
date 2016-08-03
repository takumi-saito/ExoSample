package com.example.saito.exosample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.Util;

/**
 * Created by t-saito on 16/08/03.
 */
public class WrapExoPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    // For use within demo app code.
    public static final String CONTENT_ID_EXTRA = "content_id";
    public static final String CONTENT_TYPE_EXTRA = "content_type";
    public static final String PROVIDER_EXTRA = "provider";
    // For use when launching the demo app using adb.
    private static final String CONTENT_EXT_EXTRA = "type";

    private ExoPlayer exoPlayer;

    private TrackRenderer[] trackRenderers;
    private TrackRenderer videoRenderer, audioRenderer;

    private Context mContext;
    // 映像を表示させるビューの設定
    private SurfaceView surfaceView;
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    private Surface surface;

    private Uri contentUri;
    public Uri getContentUri() {
        return contentUri;
    }
    public void setContentUri(String contentUri) {
        setContentUri(Uri.parse(contentUri));
    }
    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

    private boolean backgrounded = false;


    private int videoTrackToRestore;

    // バックグランド再生フラグ
    private boolean enableBackgroundAudio = false;
    public boolean isEnableBackgroundAudio() {
        return enableBackgroundAudio;
    }

    public void setEnableBackgroundAudio(boolean enableBackgroundAudio) {
        this.enableBackgroundAudio = enableBackgroundAudio;
    }

    /**
     * 初期設定
     */
    public void initExoPlayer(Context context) {

        mContext = context;
        DataSource dataSource = new DefaultUriDataSource(context, "userAgent");
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        SampleSource sampleSource = new ExtractorSampleSource(contentUri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        // トラックのレンダラーを作成
        videoRenderer = new MediaCodecVideoTrackRenderer(context, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);

        trackRenderers = new TrackRenderer[]{
                videoRenderer,
                audioRenderer
        };

    }

    public void setSelectedTrack(int type, int index) {
        exoPlayer.setSelectedTrack(type, index);

        /** いらなそう。。 */
//        if (type == Constants.TYPE_TEXT && index < 0 && captionListener != null) {
//            captionListener.onCues(Collections.<Cue>emptyList());
//        }
    }

    /**
     * 音声、映像のレンダラーのインデックスを返す？
     *
     * @param type
     * @return
     */
    public int getSelectedTrack(int type) {
        return exoPlayer.getSelectedTrack(type);
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
//        pushSurface(false);
    }

    public Surface getSurface() {
        return surface;
    }

    /**
     * サーフェース(メモリ)を破棄し
     * 映像レンダリングも止める
     */
    public void blockingClearSurface() {
        surface = null;
        pushSurface(true);
    }

    /**
     * trueなら映像レンダリングを一時停止させる？？
     * falseなら映像レンダリングさせる？？
     *
     * @param blockForSurfacePush
     */
    public void pushSurface(boolean blockForSurfacePush) {
        if (videoRenderer == null) {
            return;
        }

        if (blockForSurfacePush) {
            exoPlayer.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
            Log.v("saito", "映像レンダリング中止");
        } else {
            exoPlayer.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
            Log.v("saito", "映像レンダリング開始");
        }
    }
    public void pushSurfaceView(boolean blockForSurfacePush) {
        if (videoRenderer == null) {
            return;
        }

        if (blockForSurfacePush) {
            exoPlayer.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceView.getHolder().getSurface());
            Log.v("saito", "映像レンダリング中止");
        } else {
            exoPlayer.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surfaceView.getHolder().getSurface());
            Log.v("saito", "映像レンダリング開始");
        }
    }

    public void onShown() {
        if (exoPlayer == null) {
            if (!maybeRequestPermission()) {
                // パーミッションあり、再生させる
                preparePlayer(true);
            }
        } else {
            setBackgrounded(false);
        }
    }

    /**
     * Activityバックグラウンド時の処理
     */
    public void onHidden() {
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            // 映像レンダリング中止
            setBackgrounded(true);
        }
    }

    public void preparePlayer(boolean playWhenReady) {
        if (exoPlayer == null) {
            // プレイヤーがなかったら
            exoPlayer = ExoPlayer.Factory.newInstance(trackRenderers.length);
        }

        // 再生させるための準備
        exoPlayer.prepare(videoRenderer, audioRenderer);
        // レンダリングの設定
        pushSurface(false);
        // 再生開始
        exoPlayer.setPlayWhenReady(playWhenReady);
    }
    /**
     * プレイヤーリリース処理
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
//            debugViewHelper.stop();
//            debugViewHelper = null;
//            playerPosition = exoPlayer.getCurrentPosition();
            exoPlayer.release();
            exoPlayer = null;
        }
    }


    public boolean getBackgrounded() {
        return backgrounded;
    }

    public void setBackgrounded(boolean backgrounded) {
        if (this.backgrounded == backgrounded) {
            // バックグラウンド再生ステータスが変わらない場合
            return;
        }
        // バックグラウンド再生ステータス反映
        this.backgrounded = backgrounded;
        if (backgrounded) {
            // バックグラウンド再生ステータスが有効に変わった場合
            videoTrackToRestore = getSelectedTrack(Constants.TYPE_VIDEO);
            setSelectedTrack(Constants.TYPE_VIDEO, ExoPlayer.TRACK_DISABLED);
            // 映像レンダリング中止
            blockingClearSurface();
        } else {
            // バックグラウンド再生ステータスが無効に変わった場合
            setSelectedTrack(Constants.TYPE_VIDEO, videoTrackToRestore);
            pushSurface(false);
        }
    }

    /**
     * パーミッション関連
     *
     * @return
     */
    @TargetApi(23)
    private boolean maybeRequestPermission() {
        if (requiresPermission(contentUri)) {
            ((Activity)mContext).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23
                && Util.isLocalFileUri(uri)
                && mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }
}

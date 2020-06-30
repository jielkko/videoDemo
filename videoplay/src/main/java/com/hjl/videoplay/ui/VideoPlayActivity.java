package com.hjl.videoplay.ui;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hjl.videoplay.R;
import com.hjl.videoplay.utils.NavigationBarInfo;
import com.hjl.videoplay.view.DragCloseHelper;
import com.hjl.videoplay.view.OnDragCloseListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = VideoPlayActivity.class.getSimpleName();
    Context mContext;

    private MediaPlayer mMediaPlayer;
    private String mPath;
    private boolean isInitFinish = false;
    private SurfaceHolder mSurfaceHolder;


    private DragCloseHelper mDragCloseHelper;


    private LinearLayout mIvPreviewCl;
    private LinearLayout mLlContent;
    private RelativeLayout mRlVideo;
    private SurfaceView mVideoPlaySurfaceview;
    private LinearLayout mLlFiretFrame;
    private ImageView mIvFirstFrame;
    private ImageView mClose;
    private LinearLayout mLlBottom;
    private ImageView mStartAndStop;
    private TextView mCurrentTime;
    private SeekBar mSeekBar;
    private TextView mSumTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_video_play);

        mContext = this;

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mIvPreviewCl = (LinearLayout) findViewById(R.id.iv_preview_cl);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mRlVideo = (RelativeLayout) findViewById(R.id.rl_video);
        mVideoPlaySurfaceview = (SurfaceView) findViewById(R.id.video_play_surfaceview);
        mLlFiretFrame = (LinearLayout) findViewById(R.id.ll_firet_frame);
        mIvFirstFrame = (ImageView) findViewById(R.id.iv_first_frame);
        mClose = (ImageView) findViewById(R.id.close);
        mLlBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mStartAndStop = (ImageView) findViewById(R.id.start_and_stop);
        mCurrentTime = (TextView) findViewById(R.id.currentTime);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSumTime = (TextView) findViewById(R.id.sumTime);


        mStartAndStop.setOnClickListener(this);
        //SeekBar的监听事件
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        mPath = bundle.getString("path");

        if (NavigationBarInfo.hasNavBar(mContext)) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, NavigationBarInfo.getNavigationBarHeight(mContext));// 设置间距
            mLlBottom.setLayoutParams(lp);

        }


        initMediaPalyer();
        initSurfaceviewStateListener();

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.dchlib_anim_empty, R.anim.dchlib_anim_alpha_out_long_time);

            }
        });

        mDragCloseHelper = new DragCloseHelper(this);
        mDragCloseHelper.setShareElementMode(true);
        mDragCloseHelper.setDragCloseViews(mIvPreviewCl, mLlContent);
        mDragCloseHelper.setOnDragCloseListener(new OnDragCloseListener() {
            @Override
            public void onDragBegin() {

            }

            @Override
            public void onDragging(float percent) {

            }

            @Override
            public void onDragEnd(boolean isShareElementMode) {
                if (isShareElementMode) {
                    onBackPressed();
                }
            }

            @Override
            public void onDragCancel() {

            }

            @Override
            public boolean intercept() {
                // 默认false
                return false;
            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDragCloseHelper.handleEvent(ev)) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_and_stop) {
            if (mMediaPlayer.isPlaying()) {
                pausePlay();

            } else {
                startPlay();

            }
        }


    }

    private void initSurfaceviewStateListener() {
        mSurfaceHolder = mVideoPlaySurfaceview.getHolder();

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);//给mMediaPlayer添加预览的SurfaceHolder
                setPlayVideo(mPath);//添加播放视频的路径
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e(TAG, "surfaceChanged触发: width=" + width + "height" + height);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });


    }

    private void initMediaPalyer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                changeVideoSize();

            }
        });
        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {


                return false;
            }
        });

        //获取视频第一帧
        mIvFirstFrame.setImageBitmap(ThumbnailUtils.createVideoThumbnail(mPath, MediaStore.Video.Thumbnails.MINI_KIND));
        mLlFiretFrame.setVisibility(View.VISIBLE);
    }

    //改变视频的尺寸自适应。
    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        int surfaceWidth = mVideoPlaySurfaceview.getWidth();
        int surfaceHeight = mVideoPlaySurfaceview.getHeight();

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
        } else {
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth / (float) surfaceHeight), (float) videoHeight / (float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
        params.addRule(RelativeLayout.CENTER_VERTICAL, mRlVideo.getId());
        mVideoPlaySurfaceview.setLayoutParams(params);

    }


    private void setPlayVideo(String path) {
        try {
            mMediaPlayer.setDataSource(path);//
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);//缩放模式
            mMediaPlayer.setLooping(false);//设置循环播放
            mMediaPlayer.prepareAsync();//异步准备
//            mMediaPlayer.prepare();//同步准备,因为是同步在一些性能较差的设备上会导致UI卡顿
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { //准备完成回调
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isInitFinish = true;
                    //mp.start();
                    mLlFiretFrame.setVisibility(View.GONE);
                    startPlay();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlay() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mSumTime.setText(time(mMediaPlayer.getDuration()));
            VideoThreed mVideoThreed = new VideoThreed();
            mVideoThreed.start();
            mStartAndStop.setImageResource(R.drawable.ic_stop);
        }
    }

    private void stopPlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mStartAndStop.setImageResource(R.drawable.ic_start);
        }
    }

    private void pausePlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mStartAndStop.setImageResource(R.drawable.ic_start);

        }
    }

    private void seekTo(int time) {
        mMediaPlayer.seekTo(time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }


    //更新UI
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                    mCurrentTime.setText(time(mMediaPlayer.getCurrentPosition()));

                    break;
            }
        }
    };

    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    //视频进度条更新
    class VideoThreed extends Thread {
        @Override
        public void run() {
            while (mMediaPlayer != null && mMediaPlayer.isPlaying()) {

                if (mMediaPlayer.getCurrentPosition() == mMediaPlayer.getDuration()) {
                    mStartAndStop.setImageResource(R.drawable.ic_start);
                    return;
                }
                mStartAndStop.setImageResource(R.drawable.ic_stop);
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }


            }
        }
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        // 当进度条停止修改的时候触发
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (mMediaPlayer != null) {
                // 设置当前播放的位置
                mMediaPlayer.seekTo(progress);
                startPlay();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };


    private Bitmap firstFrame;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIvFirstFrame.setVisibility(View.VISIBLE);
            mIvFirstFrame.setImageBitmap(firstFrame);
        }
    };


}

package com.hjl.videodemo.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.hjl.videodemo.R;
import com.hjl.videoplay.ui.VideoPlayActivity;
import com.hjl.videoplay.view.DragCloseHelper;
import com.hjl.videoplay.view.OnDragCloseListener;

public class VideoDetailActivity extends AppCompatActivity {
    private static final String TAG = VideoPlayActivity.class.getSimpleName();

    private DragCloseHelper mDragCloseHelper;

    private LinearLayout mIvPreviewCl;
    private LinearLayout mLlContent;




    private String VIDEOPATH = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        mIvPreviewCl = (LinearLayout) findViewById(R.id.iv_preview_cl);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);


        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        VIDEOPATH = bundle.getString("VIDEOPATH");
        Log.d(TAG, "onCreate: ");

        mDragCloseHelper = new DragCloseHelper(this);
        mDragCloseHelper.setShareElementMode(true);
        mDragCloseHelper.setDragCloseViews(mIvPreviewCl,mLlContent);
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
}

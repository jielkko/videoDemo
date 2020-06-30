package com.hjl.videoplay;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.hjl.videoplay.ui.VideoPlayActivity;

/**
 * Description :
 *
 * @author huangjieliang
 * @date 2020-06-30
 */

public class VideoUtil {



    public static void show(Activity mActivity, View v, String videoPath){
        Intent intent = new Intent(mActivity, VideoPlayActivity.class);
        intent.putExtra("path", videoPath);//设置参数,""
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(mActivity, v, "");
        mActivity.startActivity(intent, options.toBundle());
    }
}

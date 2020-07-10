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

    private static int VIDEO = 66;


    public static void show(Activity mActivity, View v, String videoPath){
        Intent intent = new Intent(mActivity, VideoPlayActivity.class);
        intent.putExtra("path", videoPath);//设置参数,""
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(mActivity, v, "");
        mActivity.startActivity(intent, options.toBundle());
    }



    /**
     * 从相册中选择视频
     */
    public static void choiceVideo(Activity mActivity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        //intent = new Intent(Intent.ACTION_GET_CONTENT);
        //comma-separated MIME types
        //intent.setType("video/*, image/*");
        intent.setType("video/*");
        mActivity.startActivityForResult(intent, VIDEO);
    }


    
}

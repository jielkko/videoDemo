package com.hjl.videoplay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.hjl.videoplay.ui.VideoPlayActivity;

import java.io.File;

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

    public static void taskVideo(Activity mActivity){
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    mActivity,
                    mActivity.getPackageName() + ".fileprovider",
                    file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mActivity.startActivityForResult(intent, VIDEO);
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

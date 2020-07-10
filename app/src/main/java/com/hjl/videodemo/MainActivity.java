package com.hjl.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjl.videoplay.VideoUtil;
import com.hjl.videoplay.ui.VideoPlayActivity;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnSelectVideo;
    private Button mBtnVideo;


    private String videoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnSelectVideo = (Button) findViewById(R.id.btn_select_video);
        mBtnVideo = (Button) findViewById(R.id.btn_video);

        mBtnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceVideo();
            }
        });
        mBtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(videoPath)){
                    Toast toast = Toast.makeText(MainActivity.this, "请选择视频", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                //videoPath = "http://res.cloudinary.com/liuyuesha/video/upload/v1475978853/广告_bl4dbp.mp4";
                //videoPath = "/storage/emulated/0/Pictures/Screenshots/SVID_20200629_153659_1.mp4";
                /*Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
                intent.putExtra("path", videoPath);//设置参数,""
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, v, getString(R.string.transition_test));
                startActivity(intent, options.toBundle());*/

                VideoUtil.show(MainActivity.this, v,videoPath);
            }
        });




        initPermission();
    }


    private void initData(){

    }

    /**
     * 从相册中选择视频
     */
    private void choiceVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        //intent = new Intent(Intent.ACTION_GET_CONTENT);
        //comma-separated MIME types
        //intent.setType("video/*, image/*");
        intent.setType("video/*");
        startActivityForResult(intent, 66);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 66 && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            videoPath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("首页", "videoPath: " + videoPath);
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    private static final int RC_CAMERA_AND_LOCATION = 40001;
    String[] perms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    //获取权限
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (EasyPermissions.hasPermissions(this, perms)) {
                initData();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "部分功能需要获取存储空间；否则，您将无法正常使用",
                        RC_CAMERA_AND_LOCATION, perms);
            }


        } else {
            initData();
        }


    }

    //同意授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        Log.i(TAG, "onPermissionsGranted:" + requestCode + ":" + list.size());
        if (EasyPermissions.hasPermissions(this, perms)) {
            initData();
        } else {
            // Do not have permissions, request them now
            finish();
        }


    }


    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // ToastUtils.showToast(getApplicationContext(), "用户授权失败");
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static android.graphics.BitmapFactory.decodeResource;
import static com.tencent.rtmp.TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO;
import static com.tencent.rtmp.TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_INVALID_ADDRESS;
import static com.tencent.rtmp.TXLiveConstants.PUSH_ERR_NET_DISCONNECT;
import static com.tencent.rtmp.TXLiveConstants.PUSH_WARNING_NET_BUSY;

public class MainActivity extends AppCompatActivity {

    private String rtmpURL;
    Button yinsi , Noyinsi , look , shexiang;
    private TXLivePushConfig mLivePushConfig;
    private TXLivePusher mLivePusher;
    private int screenchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        yinsi = findViewById(R.id.yinsi);
        Noyinsi = findViewById(R.id.Noyinsi);
        look = findViewById(R.id.look);
        shexiang = findViewById(R.id.shexiang);

        //重力感应
        try {
            screenchange = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


        mLivePushConfig = new TXLivePushConfig();
        mLivePusher = new TXLivePusher(this);
        // 一般情况下不需要修改 config 的默认配置
        mLivePusher.setConfig(mLivePushConfig);

        //启动本地摄像头预览
        TXCloudVideoView mPusherView = (TXCloudVideoView) findViewById(R.id.pusher_tx_cloud_view);
        mLivePusher.startCameraPreview(mPusherView);


        //此处填写您的 rtmp 推流地址
        rtmpURL = "rtmp://58866.livepush.myqcloud.com/live/suming?txSecret=f27aa31b0382becfab466fff819bbdd8&txTime=5D5AC77F";
        int ret = mLivePusher.startPusher(rtmpURL.trim());
        if (ret == -5) {
            Log.i("直播", "startRTMPPush: license 校验失败");
        }
        //设置隐私的图片
        Bitmap bitmap = decodeResource(getResources(), R.drawable.ic_lar);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setPauseImg(300, 5);

        mLivePushConfig.setPauseFlag(PAUSE_FLAG_PAUSE_VIDEO|PAUSE_FLAG_PAUSE_AUDIO);

        mLivePusher.setConfig(mLivePushConfig);

        //四个参数依次是水印图片的 Bitmap、水印位置的 X 轴坐标，水印位置的 Y 轴坐标，水印宽度。后面三个参数取值范围是[0, 1]
        Bitmap waterBmp = decodeResource(getResources(), R.drawable.ic_lar);
        mLivePushConfig.setWatermark(waterBmp, 0.1f, 0.1f, 0.1f);
        mLivePusher.setConfig(mLivePushConfig);

        yinsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLivePusher.pausePusher();
            }
        });

        Noyinsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLivePusher.resumePusher();
            }
        });

        look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LookActivity.class);
                startActivity(intent);
            }
        });

        shexiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLivePusher.switchCamera();
            }
        });


        //设置视屏镜像效果与现实一致
        mLivePusher.setMirror(false);
        //视屏渲染的方向
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
            onOrientationChange(false);
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
            onOrientationChange(true);
        }
        ITXLivePushListener itxLivePushListener = new ITXLivePushListener() {
            @Override
            public void onPushEvent(int i, Bundle bundle) {
                if (i == PUSH_ERR_NET_DISCONNECT || i == PUSH_ERR_INVALID_ADDRESS) {
                    //...

                } else if (i == PUSH_WARNING_NET_BUSY) {
                    Toast.makeText(MainActivity.this,"网络不佳", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        };

    }


    public void onOrientationChange(boolean isPortrait) {
        if (isPortrait) {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.setRenderRotation(0);
        } else {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
            mLivePusher.setConfig(mLivePushConfig);
            // 因为采集旋转了，为了保证本地渲染是正的，则设置渲染角度为90度。
            //activity采用横竖屏自动切换，故此设置0
            mLivePusher.setRenderRotation(0);
        }
    }


}

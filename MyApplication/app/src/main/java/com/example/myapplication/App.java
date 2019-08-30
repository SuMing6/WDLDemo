package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import com.tencent.rtmp.TXLiveBase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String licenceURL = "http://license.vod2.myqcloud.com/license/v1/d463386512ffa20a96c2efb93455f512/TXLiveSDK.licence"; // 获取到的 licence url
        String licenceKey = "96f93df352bcc29ade657298745d70c4"; // 获取到的 licence key
        TXLiveBase.getInstance().setLicence(this, licenceURL, licenceKey);
        TXLiveBase.getInstance().getLicenceInfo(this);

    }
}

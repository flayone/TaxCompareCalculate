package com.flayone.taxcc.taxcomparecalculate.ad;

import android.content.Context;
import android.util.Log;

import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomInitConfig;
import com.bytedance.msdk.api.v2.ad.custom.init.GMCustomAdapterConfiguration;
import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.managers.status.SDKStatus;

import java.util.Map;

public class GdtCusConfig extends GMCustomAdapterConfiguration {
    String TAG = "[GroMoreAD]" + GdtCusConfig.class.getSimpleName();

    @Override
    public void initializeADN(Context context, GMCustomInitConfig gmCustomInitConfig, Map<String, Object> map) {
        Log.d(TAG, "initializeADN：");
        ThreadUtils.runOnThreadPool(() -> {
            String mid = gmCustomInitConfig.getAppId();
            GDTAdSdk.init(context, mid);
            callInitSuccess();
        });
    }

    @Override
    public String getNetworkSdkVersion() {
        return SDKStatus.getSDKVersion();
    }

    @Override
    public String getAdapterSdkVersion() {
        return "1.0.0";
    }

}

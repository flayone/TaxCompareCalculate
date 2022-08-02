package com.flayone.taxcc.taxcomparecalculate.ad;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.bytedance.msdk.adapter.TToast;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;

import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.GM_SPLASH_ADID;
import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.GM_TAG;


public class GroMoreAD {
    String TAG = GM_TAG;

    public void initSDK(Context context) {
        GMAdManagerHolder.init(context);
    }

    GMSplashAdListener mSplashAdListener;
    AdSplashManager mAdSplashManager;

    public void loadSplashAD(Activity activity, final ViewGroup adContainer, AdvanceAD.SplashCallBack callBack) {

        mSplashAdListener = new GMSplashAdListener() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mAdSplashManager.printInfo();
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                String emsg = "";
                if (adError != null) {
                    emsg = adError.toString();
                }
                Log.d(TAG, "onAdShowFail , adError = " + emsg);
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
//                if (mAdSplashManager != null) {
//                    mAdSplashManager.loadSplashAd(mAdUnitId);
//                }
            }

            @Override
            public void onAdSkip() {
                Log.d(TAG, "onAdSkip");
                callBack.jumpMain();
            }

            @Override
            public void onAdDismiss() {
                Log.d(TAG, "onAdDismiss");
                callBack.jumpMain();
            }
        };

        mAdSplashManager = new AdSplashManager(activity, true, new GMSplashAdLoadCallback() {
            @Override
            public void onSplashAdLoadFail(AdError adError) {
                TToast.show(activity, "广告加载失败");
                Log.d(TAG, adError.message);
                Log.e(TAG, "load splash ad error : " + adError.code + ", " + adError.message);
                // 获取本次waterfall加载中，加载失败的adn错误信息。
                if (mAdSplashManager.getSplashAd() != null)
                    Log.d(TAG, "ad load infos: " + mAdSplashManager.getSplashAd().getAdLoadInfoList());
                callBack.jumpMain();
                callBack.adEnd();
            }

            @Override
            public void onSplashAdLoadSuccess() {
//                TToast.show(activity, "广告加载成功");
                Log.e(TAG, "load splash ad success ");
                mAdSplashManager.printInfo();
                // 根据需要选择调用isReady()
//                if (mAdSplashManager.getSplashAd().isReady()) {
//                    mAdSplashManager.getSplashAd().showAd(mSplashContainer);
//                }
                mAdSplashManager.getSplashAd().showAd(adContainer);
                callBack.adEnd();
            }

            // 注意：***** 开屏广告加载超时回调已废弃，统一走onSplashAdLoadFail，GroMore作为聚合不存在SplashTimeout情况。*****
            @Override
            public void onAdLoadTimeout() {

            }
        }, mSplashAdListener);
        //加载开屏广告
        if(mAdSplashManager != null){
            Log.d(TAG, "start splash");

            mAdSplashManager.loadSplashAd(GM_SPLASH_ADID);
        }

    }
}

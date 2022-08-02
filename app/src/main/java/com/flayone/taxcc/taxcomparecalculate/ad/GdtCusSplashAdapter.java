package com.flayone.taxcc.taxcomparecalculate.ad;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup;

import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.splash.GMCustomSplashAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Map;

public class GdtCusSplashAdapter extends GMCustomSplashAdapter {
    private volatile SplashAD mSplashAD;
    private boolean isLoadSuccess;
    String TAG = "[GroMoreAD]" + GdtCusSplashAdapter.class.getSimpleName();

    @Override
    public void load(Context context, GMAdSlotSplash gmAdSlotSplash, GMCustomServiceConfig serviceConfig) {
        Log.d(TAG, "load ad ：");

        ThreadUtils.runOnThreadPool(() -> {
            mSplashAD = new SplashAD(context, serviceConfig.getADNNetworkSlotId(), new SplashADListener() {
                @Override
                public void onADDismissed() {
                    Log.i(TAG, "onADDismissed");
                    callSplashAdDismiss();
                }

                @Override
                public void onNoAD(AdError adError) {
                    isLoadSuccess = false;
                    if (adError != null) {
                        Log.i(TAG, "onNoAD errorCode = " + adError.getErrorCode() + " errorMessage = " + adError.getErrorMsg());
                        callLoadFail(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                    } else {
                        callLoadFail(new GMCustomAdError(Const.LOAD_ERROR, "no ad"));
                    }
                }

                @Override
                public void onADPresent() {
                    Log.i(TAG, "onADPresent");
                }

                @Override
                public void onADClicked() {
                    Log.i(TAG, "onADClicked");
                    callSplashAdClicked();
                }

                @Override
                public void onADTick(long l) {
                    Log.i(TAG, "onADTick");
                }

                @Override
                public void onADExposure() {
                    Log.i(TAG, "onADExposure");
                    callSplashAdShow();
                }

                @Override
                public void onADLoaded(long expireTimestamp) {
                    Log.i(TAG, "onADLoaded");
                    long timeIntervalSec = expireTimestamp - SystemClock.elapsedRealtime();
                    if (timeIntervalSec > 1000) {
                        isLoadSuccess = true;
                        if (isBidding()) {//bidding类型广告
                            double ecpm = mSplashAD.getECPM(); //当无权限调用该接口时，SDK会返回错误码-1
                            if (ecpm < 0) {
                                ecpm = 0;
                            }
                            Log.e(TAG, "ecpm:" + ecpm);
                            callLoadSuccess(ecpm);  //bidding广告成功回调，回传竞价广告价格
                        } else { //普通类型广告
                            callLoadSuccess();
                        }
                    } else {
                        isLoadSuccess = false;
                        callLoadFail(new GMCustomAdError(Const.LOAD_ERROR, "ad has expired"));
                    }
                }
            }, 3000);
            mSplashAD.fetchAdOnly();
        });
    }

    @Override
    public void showAd(ViewGroup container) {
/**
 * 先切子线程，再在子线程中切主线程进行广告展示
 */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mSplashAD != null && container != null) {
                    container.removeAllViews();
                    mSplashAD.showAd(container);
                }
            }
        });
    }

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
        Log.d(TAG, "receiveBidResult： win = "+win +"， winnerPrice = "+ winnerPrice + "， loseReason = "+ loseReason + "， extra = "+ extra  );

    }

    /**
     * 是否是Bidding广告
     *
     * @return
     */
    public boolean isBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }
}

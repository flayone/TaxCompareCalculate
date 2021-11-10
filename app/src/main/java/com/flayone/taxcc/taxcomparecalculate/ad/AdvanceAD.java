package com.flayone.taxcc.taxcomparecalculate.ad;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.advance.AdvanceBanner;
import com.advance.AdvanceBannerListener;
import com.advance.AdvanceBaseAdspot;
import com.advance.AdvanceConfig;
import com.advance.AdvanceFullScreenItem;
import com.advance.AdvanceFullScreenVideo;
import com.advance.AdvanceFullScreenVideoListener;
import com.advance.AdvanceInterstitial;
import com.advance.AdvanceInterstitialListener;
import com.advance.AdvanceNativeExpress;
import com.advance.AdvanceNativeExpressAdItem;
import com.advance.AdvanceNativeExpressListener;
import com.advance.AdvanceRewardVideo;
import com.advance.AdvanceRewardVideoItem;
import com.advance.AdvanceRewardVideoListener;
import com.advance.AdvanceSDK;
import com.advance.AdvanceSplash;
import com.advance.AdvanceSplashListener;
import com.advance.RewardServerCallBackInf;
import com.advance.custom.AdvanceBaseCustomAdapter;
import com.advance.model.AdvanceError;
import com.advance.model.AdvanceLogLevel;
import com.advance.model.CacheMode;
import com.advance.supplier.baidu.AdvanceBDManager;
import com.advance.utils.LogUtil;
import com.advance.utils.ScreenUtil;
import com.flayone.taxcc.taxcomparecalculate.BuildConfig;
import com.flayone.taxcc.taxcomparecalculate.R;
import com.mercury.sdk.core.config.LargeADCutType;
import com.mercury.sdk.core.config.MercuryAD;

import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.ADVANCE_APP_ID;
import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.ADVANCE_SPLASH_ID;

/**
 * Advance SDK广告加载逻辑统一处理类
 */
public class AdvanceAD {
    AdvanceBaseAdspot baseAD;
    Activity mActivity;
    String sdkId;

    //小米渠道是否需要添加为自定义渠道
    public boolean cusXiaoMi = false;
    //华为渠道是否需要添加为自定义渠道
    public boolean cusHuaWei = false;

    /**
     * 初始化广告处理类
     *
     * @param activity 页面上下文
     */
    public AdvanceAD(Activity activity) {
        mActivity = activity;
    }


    /**
     * 添加自定义渠道，注意一定要在广告初始化以后再调用！
     *
     * @param sdkID   SDK渠道id。具体值需联系运营获取对应接入渠道的id。
     * @param adapter 继承与基类adapter的自定义adapter
     */
    public void addCustomAdapter(String sdkID, AdvanceBaseCustomAdapter adapter) {
        if (baseAD != null) {
            baseAD.addCustomSupplier(sdkID, adapter);
        }
    }

    /**
     * 初始化advance sdk
     *
     * @param context 上下文内容，一般是传入application的context
     */
    public static void initAD(Context context) {
        //必要配置：初始化聚合SDK，三个参数依次为context上下文，appId媒体id
        AdvanceSDK.initSDK(context, ADVANCE_APP_ID);
        //设置debug打印等级
        AdvanceSDK.setDebug(BuildConfig.DEBUG, AdvanceLogLevel.MAX);
        //推荐配置：允许Mercury预缓存素材
        MercuryAD.needPreLoadMaterial(true);
        //策略缓存时长配置
        AdvanceConfig.getInstance().setDefaultStrategyCacheTime(CacheMode.WEEK);
    }

    /**
     * 加载开屏广告
     *
     * @param adContainer   广告承载布局，不可为空
     * @param logoContainer 底部logo布局，可以为空
     * @param skipView      跳过按钮，可以为空
     * @param callBack      跳转回调，在回调中进行跳转主页或其他操作
     */
    public void loadSplash(final ViewGroup adContainer, final ViewGroup logoContainer, final android.widget.TextView skipView, final SplashCallBack callBack) {
        //开屏初始化；adspotId代表广告位id，adContainer为广告容器，skipView不需要自定义可以为null
        final AdvanceSplash advanceSplash = new AdvanceSplash(mActivity, ADVANCE_SPLASH_ID, adContainer, skipView);
        baseAD = advanceSplash;
        MercuryAD.setLargeADCutType(LargeADCutType.FILL_PARENT);
        //注意：如果开屏页是fragment或者dialog实现，这里需要置为true。不设置时默认值为false，代表开屏和首页为两个不同的activity
//        advanceSplash.setShowInSingleActivity(true);
        //设置穿山甲素材尺寸跟随父布局大小
        adContainer.post(new Runnable() {
            @Override
            public void run() {
                advanceSplash.setCsjExpressViewAcceptedSize(adContainer.getWidth(), adContainer.getHeight());
            }
        });
//        if (cusXiaoMi) {
//            //此处自定义的渠道id值，需要联系我们获取。
//            advanceSplash.addCustomSupplier("小米SDK渠道id", new XiaoMiSplashAdapter(mActivity, advanceSplash));
//        }
//        if (cusHuaWei) {
//            advanceSplash.addCustomSupplier("华为SDK渠道id", new HuaWeiSplashAdapter(mActivity, advanceSplash));
//        }

        //必须：设置开屏核心回调事件的监听器。
        advanceSplash.setAdListener(new AdvanceSplashListener() {
            /**
             * @param id 代表当前被选中的策略id，值为"1" 代表mercury策略 ，值为"2" 代表广点通策略， 值为"3" 代表穿山甲策略
             */
            @Override
            public void onSdkSelected(String id) {
                //给sdkId赋值用来判断被策略选中的是哪个SDK
                sdkId = id;

                logAndToast(mActivity, "策略选中SDK id = " + id);
            }

            @Override
            public void onAdLoaded() {
                if (logoContainer != null) {
                    //穿山甲广告加载成功到展现时间很快，所以最好在这里进行logo布局的展示
                    if ("3".equals(sdkId)) {
                        logoContainer.setVisibility(android.view.View.VISIBLE);
                    } else {
                        logoContainer.setVisibility(android.view.View.GONE);
                    }
                }

                logAndToast(mActivity, "广告加载成功");
            }

            @Override
            public void jumpToMain() {
                if (callBack != null) {
                    callBack.jumpMain();
                }
            }

            @Override
            public void onAdShow() {
                //设置开屏父布局背景色为白色
                if (adContainer != null)
                    adContainer.setBackgroundColor(android.graphics.Color.WHITE);
                //logo展示建议：广告展示的时候再展示logo，其他时刻都是展示的全屏的background图片
                if (logoContainer != null)
                    logoContainer.setVisibility(android.view.View.VISIBLE);

                //如果选择了自定义skipView，强烈建议：开屏页布局中按钮初始背景设置成透明背景，skipView只有在广告展示出来以后才将背景色进行填充，这样展现效果较佳
                if (skipView != null)
                    skipView.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.background_circle));

                logAndToast(mActivity, "广告展示成功");
                if (callBack != null) {
                    callBack.adEnd();
                }
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
                if (callBack != null) {
                    callBack.adEnd();
                }
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onAdSkip() {
                logAndToast(mActivity, "跳过广告");
            }

            @Override
            public void onAdTimeOver() {
                logAndToast(mActivity, "倒计时结束，关闭广告");
            }
        });
        advanceSplash.enableStrategyCache(true);
        //必须：请求广告
        advanceSplash.loadStrategy();
    }


    /**
     * 开屏跳转回调
     */
    public interface SplashCallBack {
        void jumpMain();

        //广告周期结束,展示成功或加载失败时回调
        void adEnd();
    }

    /**
     * 加载并展示banner广告
     *
     * @param adContainer banner广告的承载布局
     */
    public void loadBanner(final ViewGroup adContainer) {
        AdvanceBanner advanceBanner = new AdvanceBanner(mActivity, adContainer, "");
        baseAD = advanceBanner;
        //设置穿山甲布局尺寸，宽度全屏，高度自适应
        advanceBanner.setCsjExpressViewAcceptedSize(ScreenUtil.px2dip(mActivity, ScreenUtil.getScreenWidth(mActivity)), 0);
        //推荐：核心事件监听回调
        advanceBanner.setAdListener(new AdvanceBannerListener() {
            @Override
            public void onDislike() {
                logAndToast(mActivity, "广告关闭");

                adContainer.removeAllViews();
            }

            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展现");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "策略选中SDK id = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onAdLoaded() {
                logAndToast(mActivity, "广告加载成功");
            }

        });
        advanceBanner.loadStrategy();
    }


    /**
     * 加载并展示插屏广告。
     * 也可以选择性先提前加载，然后在合适的时机再调用展示方法
     */
    public void loadInterstitial() {
        //初始化
        final AdvanceInterstitial advanceInterstitial = new AdvanceInterstitial(mActivity, "");
        baseAD = advanceInterstitial;
        //注意：穿山甲是否为"新插屏广告"，默认为true
//        advanceInterstitial.setCsjNew(false);
        //推荐：核心事件监听回调
        advanceInterstitial.setAdListener(new AdvanceInterstitialListener() {

            @Override
            public void onAdReady() {
                logAndToast(mActivity, "广告就绪");

                // 大多数情况下可以直接展示
                // 如果有业务需求，可以提前加载广告，保存广告对象，需要时再调用show
                if (advanceInterstitial != null) {
                    advanceInterstitial.show();
                }
            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }


            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        });
        advanceInterstitial.loadStrategy();

    }

    /**
     * 加载并展示激励视频广告。
     * 也可以选择性先提前加载，然后在合适的时机再调用展示方法
     */
    public void loadReward() {
        //初始化，注意需要时再初始化，不要复用。
        AdvanceRewardVideo advanceRewardVideo = new AdvanceRewardVideo(mActivity, "");
        baseAD = advanceRewardVideo;
        //按需必填，注意：如果穿山甲版本号大于3.2.5.1，模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
        advanceRewardVideo.setCsjExpressSize(500, 500);
        //设置通用事件监听器
        advanceRewardVideo.setAdListener(new AdvanceRewardVideoListener() {
            @Override
            public void onAdLoaded(AdvanceRewardVideoItem advanceRewardVideoItem) {
                logAndToast(mActivity, "广告加载成功");

                // 如果有业务需求，可以提前加载广告，保存这里的advanceRewardVideoItem 对象，在需要的时候调用show进行展示
                // 为了方便理解，这里在收到广告后直接调用广告展示，有可能会出现一段时间的缓冲状态。
                if (advanceRewardVideoItem != null) {
                    //展示广告
                    advanceRewardVideoItem.showAd();
                }
            }


            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onVideoCached() {
                logAndToast(mActivity, "广告缓存成功");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放完毕");
            }

            @Override
            public void onVideoSkip() {

            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdReward() {
                logAndToast(mActivity, "激励发放");
            }

            @Override
            public void onRewardServerInf(RewardServerCallBackInf inf) {
                //广点通和穿山甲支持回调服务端激励验证信息，详见RewardServerCallBackInf中字段信息
                logAndToast(mActivity, "onRewardServerInf" + inf);
            }
        });
        advanceRewardVideo.loadStrategy();
    }

    /**
     * 加载并展示全屏视频广告。
     * 也可以选择先提前加载，然后在合适的时机再调用展示方法
     */
    public void loadFullVideo() {
        //初始化
        AdvanceFullScreenVideo advanceFullScreenVideo = new AdvanceFullScreenVideo(mActivity, "");
        baseAD = advanceFullScreenVideo;
        //注意：如果穿山甲版本号大于3.2.5.1，模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
        advanceFullScreenVideo.setCsjExpressSize(500, 500);
        //推荐：核心事件监听回调
        advanceFullScreenVideo.setAdListener(new AdvanceFullScreenVideoListener() {
            @Override
            public void onAdLoaded(AdvanceFullScreenItem advanceFullScreenItem) {
                logAndToast(mActivity, "广告加载成功");

                // 如果有业务需求，可以提前加载广告，保存这里的advanceFullScreenItem 对象，在需要的时候调用show进行展示
                // 为了方便理解，这里在收到广告后直接调用广告展示，有可能会出现一段时间的缓冲状态。
                if (advanceFullScreenItem != null)
                    advanceFullScreenItem.showAd();
            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放结束");
            }

            @Override
            public void onVideoSkipped() {
                logAndToast(mActivity, "跳过视频");
            }

            @Override
            public void onVideoCached() {
                //广告缓存成功，可以在此记录状态，但要注意：不一定所有的广告会返回该回调
                logAndToast(mActivity, "广告缓存成功");
            }

            @Override
            public void onAdShow() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        });
        advanceFullScreenVideo.loadStrategy();
    }

    boolean isGdtExpress2 = false;
    AdvanceNativeExpressAdItem advanceNativeExpressAdItem;
    boolean hasNativeShow = false;
    boolean isNativeLoading = false;

    /**
     * 加载并展示原生模板信息流广告
     *
     * @param adContainer 广告的承载布局
     */
    public void loadNativeExpress(final ViewGroup adContainer) {
        if (hasNativeShow) {
            LogUtil.d("loadNativeExpress hasNativeShow");
            return;
        }
        if (advanceNativeExpressAdItem != null) {
            if (adContainer.getChildCount() > 0 && adContainer.getChildAt(0) == advanceNativeExpressAdItem.getExpressAdView()) {
                return;
            }
        }
        if (isNativeLoading) {
            LogUtil.d("loadNativeExpress isNativeLoading");
            return;
        }
        isNativeLoading = true;

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }

        AdvanceBDManager.getInstance().nativeExpressContainer = adContainer;

        //初始化
        final AdvanceNativeExpress advanceNativeExpress = new AdvanceNativeExpress(mActivity, "");
        baseAD = advanceNativeExpress;
        //必须：设置广告父布局
        advanceNativeExpress.setAdContainer(adContainer);
        //推荐：核心事件监听回调
        advanceNativeExpress.setAdListener(new AdvanceNativeExpressListener() {
            @Override
            public void onAdLoaded(java.util.List<AdvanceNativeExpressAdItem> list) {
                advanceNativeExpress.show();
            }

            @Override
            public void onAdRenderSuccess(android.view.View view) {
                logAndToast(mActivity, "广告渲染成功");
            }


            @Override
            public void onAdClose(android.view.View view) {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdShow(android.view.View view) {
                hasNativeShow = true;
                isNativeLoading = false;
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(AdvanceError advanceError) {
                isNativeLoading = false;
                logAndToast(mActivity, "广告加载失败 code=" + advanceError.code + " msg=" + advanceError.msg);
            }

            @Override
            public void onSdkSelected(String id) {
                logAndToast(mActivity, "onSdkSelected = " + id);
            }

            @Override
            public void onAdRenderFailed(android.view.View view) {
                logAndToast(mActivity, "广告渲染失败");
            }

            @Override
            public void onAdClicked(android.view.View view) {
                logAndToast(mActivity, "广告点击");
            }

        });
        //必须
        advanceNativeExpress.loadStrategy();
    }

    /**
     * 销毁广告
     */
    public void destroy() {
        if (baseAD != null) {
            baseAD.destroy();
            baseAD = null;
        }
    }

    /**
     * 统一处理打印日志，并且toast提示。
     *
     * @param context 上下文
     * @param msg     需要显示的内容
     */
    public void logAndToast(Context context, String msg) {
        android.util.Log.d("[AdvanceAD-logAndToast]", msg);
        //如果不想弹出toast可以在此注释掉下面代码
        if (BuildConfig.DEBUG) {
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}

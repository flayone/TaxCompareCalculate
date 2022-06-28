package com.flayone.taxcc.taxcomparecalculate.ad;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMPangleOption;
import com.bytedance.msdk.api.v2.GMPrivacyConfig;

import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.GM_APP_ID;
import static com.flayone.taxcc.taxcomparecalculate.ad.AdConstantKt.GM_APP_NAME;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class GMAdManagerHolder {

    private static boolean sInit;

    public static void init(Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(@NonNull Context context) {
        if (!sInit) {
            GMMediationAdSdk.initialize(context,buildV2Config(context));
            sInit = true;
        }
    }

    public static GMAdConfig buildV2Config(Context context) {
        /**
         * GMConfigUserInfoForSegment设置流量分组的信息
         * 注意：
         * 1、请每次都传入新的info对象
         * 2、字符串类型的值只能是大小写字母，数字，下划线，连字符，字符个数100以内 ( [A-Za-z0-9-_]{1,100} ) ，不符合规则的信息将被过滤掉，不起作用。
         */
//        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
//        userInfo.setUserId("msdk-demo");
//        userInfo.setGender(UserInfoForSegment.GENDER_MALE);
//        userInfo.setChannel("msdk-channel");
//        userInfo.setSubChannel("msdk-sub-channel");
//        userInfo.setAge(999);
//        userInfo.setUserValueGroup("msdk-demo-user-value-group");

//        Map<String, String> customInfos = new HashMap<>();
//        customInfos.put("aaaa", "test111");
//        customInfos.put("bbbb", "test222");
//        userInfo.setCustomInfos(customInfos);

//        Map<String,Object> initConfig = new HashMap<>();
//        initConfig.put("1111","22222");
//        initConfig.put("22222","33333");
//        initConfig.put("44444","5555");
        return new GMAdConfig.Builder()
                .setAppId(GM_APP_ID)
                .setAppName(GM_APP_NAME)
                .setDebug(true)
//                .setPublisherDid(getAndroidId(context))
                .setOpenAdnTest(false)
//                .setConfigUserInfoForSegment(userInfo)
                .setPangleOption(new GMPangleOption.Builder()
                        .setIsPaid(false)
//                        .setTitleBarTheme(GMAdConstant.TITLE_BAR_THEME_DARK)
                        .setAllowShowNotify(true)
                        .setAllowShowPageWhenScreenLock(true)
                        .setDirectDownloadNetworkType(GMAdConstant.NETWORK_STATE_WIFI, GMAdConstant.NETWORK_STATE_4G)
                        .setIsUseTextureView(true)
                        .setNeedClearTaskReset()
                        .setKeywords("")
                        .build())
                /**
                 * 隐私协议设置，详见GMPrivacyConfig
                 */
//                .setPrivacyConfig(new GMPrivacyConfig() {
//                    // 重写相应的函数，设置需要设置的权限开关，不重写的将采用默认值
//                    // 例如，重写isCanUsePhoneState函数返回true，表示允许使用ReadPhoneState权限。
//                    @Override
//                    public boolean isCanUsePhoneState() {
//                        return false;
//                    }
//
//                    //当isCanUseWifiState=false时，可传入Mac地址信息，穿山甲sdk使用您传入的Mac地址信息
//                    @Override
//                    public String getMacAddress() {
//                        return "";
//                    }
//
//                    @Override
//                    public boolean isCanUseWifiState() {
//                        return false;
//                    }
//
//                    @Override
//                    public String getAndroidId() {
//                        return "";
//                    }
//
//
//                })
//                .setLocalExtra(initConfig)
                .build();
    }

    public static String getAndroidId(Context context) {
        String androidId = null;
        try {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

}

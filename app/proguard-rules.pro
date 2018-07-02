# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\Administrator\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-optimizationpasses 5                                                           # 指定代码的压缩级别
-dontusemixedcaseclassnames                                                     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                                                # 是否混淆第三方jar
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-ignorewarnings                                                                 #这1句是屏蔽警告，脚本中把这行注释去掉
-dontpreverify                                                                  # 混淆时是否做预校验
-verbose                                                                        # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法

-keepattributes *Annotation*, EnclosingMethod, Exceptions, Signature, InnerClasses

-keep public class * extends android.app.Fragment                               # 保持哪些类不被混淆
-keep public class * extends android.app.Activity                               # 保持哪些类不被混淆
-keep public class * extends android.app.Application                            # 保持哪些类不被混淆
-keep public class * extends android.app.Service                                # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver                  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider                    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper               # 保持哪些类不被混淆
-keep public class * extends android.os.Handler                                 # 保持哪些类不被混淆
-keep public class * extends android.app.ProgressDialog                         # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference                      # 保持哪些类不被混淆
-keep public class * extends android.support.v4.**                              # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService              # 保持哪些类不被混淆

-keepclasseswithmembernames class * {                                           # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {                                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);     # 保持自定义控件类不被混淆
}

-keepclassmembers class * extends android.app.Activity {                        # 保持自定义控件类不被混淆
   public void *(android.view.View);
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keepclassmembers enum * {                                                      # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {                                # 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}

-dontwarn **.model.**
-keep class **.model.** { *;}                                     # 保持自己定义的类不被混淆
-dontwarn **.requst.**
-keep class **.requst.** { *;}                                     # 保持自己定义的类不被混淆
-dontwarn **.respone.**
-keep class **.respone.** { *;}                                     # 保持自己定义的类不被混淆

-dontwarn org.codehaus.mojo.**
-keep class org.codehaus.mojo.** { *; }

-dontwarn java.nio.file.**
-keep class java.nio.file.** { *; }

-dontwarn com.squareup.**
-dontwarn okhttp3.**
-keep class com.squareup.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *; }

-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }

-dontwarn com.chanven.**
-keep class com.chanven.** { *; }

-dontwarn com.github.**
-keep class com.github.** { *; }

-dontwarn javax.**
-keep class javax.** { *; }

-dontwarn org.**
-keep class org.** { *; }

-dontwarn kale.**
-keep class kale.** { *; }

-dontwarn cn.hugo.android.scanner.**
-keep class cn.hugo.android.scanner.** { *; }

-dontwarn rx.**
-keep class rx.** { *; }

-dontwarn com.boyaa.**
-keep class com.boyaa.** { *; }

-dontwarn com.saike.android.messagecollector.**
-keep class com.saike.android.messagecollector.** { *; }

-dontwarn in.**
-keep class in.** { *; }

-dontwarn jp.**
-keep class jp.** { *; }

-dontwarn top.**
-keep class top.** { *; }

-dontwarn com.zhy.**
-keep class com.zhy.** { *; }

-dontwarn com.jakewharton.**
-keep class com.jakewharton.** { *; }

-dontwarn com.getbase.**
-keep class com.getbase.** { *; }

-dontwarn com.prolificinteractive.**
-keep class com.prolificinteractive.** { *; }

-dontwarn com.cjj.**
-keep class com.cjj.** { *; }

-dontwarn com.sobot.**
-keep class com.sobot.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }
# 极光推送混淆相关
-keep public class * extends android.app.IntentService
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
# 友盟混淆相关
-keep public class com.chexiang.myhome.R$*{
    public static final int *;
}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
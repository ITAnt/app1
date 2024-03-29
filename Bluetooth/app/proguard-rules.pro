-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-optimizationpasses 5            # 指定代码的压缩级别
-dontusemixedcaseclassnames      # 是否使用大小写混合
-dontpreverify                   # 混淆时是否做预校验
-verbose                         # 混淆时是否记录日志
-keepattributes *Annotation*     # 保持注解
-ignorewarning                   # 忽略警告
-dontoptimize                    # 优化不优化输入的类文件
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/* # 混淆时所采用的算法

#保持哪些类不被混淆
-keep public class * extends android.app.Activity                               # 保持哪些类不被混淆
-keep public class * extends android.app.Application                            # 保持哪些类不被混淆
-keep public class * extends android.app.Service                                # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver                  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider                    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper               # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference                      # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService              # 保持哪些类不被混淆
-keep public class android.net.http.SslError
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.Fragment


                                                                #生成日志数据，gradle build时在本项目根目录输出
-dump class_files.txt                                           #apk包内所有class的内部结构
-printseeds seeds.txt                                           #未混淆的类和成员
-printusage unused.txt                                          #打印未被使用的代码
-printmapping mapping.txt                                       #混淆前后的映射

-keepclasseswithmembernames class * {                           # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {        # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {                                      # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {                # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {     #不混淆Serializable
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable {*;}
-keepattributes Signature                                       #不混淆泛型
-keep public class * extends android.support.**                 #如果有引用v4或者v7包，需添加

-keep class android.support.v4.**
-keep interface android.support.v4.**
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontwarn android.support.*

-keepattributes *Annotation*,Signature,Exceptions,InnerClasses

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}


#-keep class packagename.** {*;}                                 # 另外，有些情况下一些引入的外部lib，如果被混淆也会出现各种各样的问题，如果不想混淆这些包，就要加上

#========
-dontwarn com.jancar.JancarManager
-keep class com.jancar.JancarManager
-dontwarn com.jancar.state.**
-keep class com.jancar.state.**{*;}
-dontwarn com.jancar.JancarService
-keep class com.jancar.JancarService
-dontwarn com.jancar.bluetooth.**
-keep class com.jancar.bluetooth.**{*;}
-dontwarn com.ui.mvp.**
-keep class com.ui.mvp.**{*;}
-dontwarn com.jancar.bluetooth.phone.widget.**
-keep class com.jancar.bluetooth.phone.widget.**{*;}
-dontwarn com.jancar.bluetooth.phone.util.**
-keep class com.jancar.bluetooth.phone.util.**{*;}


# 三方控件#
# leakcanary ==== start############################
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification
# leakcanary ==== end #########################

# eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/abaranov/Android/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interfaces
# class:
#-keepclassmembers class fqcn.of.javascript.interfaces.for.webview {
#   public *;
#}

-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod

-dontwarn android.net.http.**
-dontwarn com.android.internal.http.**

#to implement HyperSnapSDK
#-keep class com.google.android.gms.internal.** { *; }
#-keep class com.google.gson.** { *; }
#-keep public class com.google.gson.** {public private protected *;}
#-keep class retrofit.** { *; }
#-dontwarn com.google.android.gms.internal.zzhu
#
#-keep public class com.google.android.gms.* { public *; }
#-dontwarn com.google.android.gms.**
#
#-dontwarn com.squareup.okhttp.*
#-dontwarn retrofit2.**
#-dontwarn okhttp3.**
#-dontwarn okio.**
#
#-keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
#         private javax.net.ssl.SSLSocketFactory delegate;
#    }
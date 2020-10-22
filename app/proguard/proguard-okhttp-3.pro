# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keep class org.conscrypt.**
-dontwarn org.conscrypt.**

-keep class org.openjsse.javax.net.**
-dontwarn org.openjsse.javax.net.**

-keep class org.openjsse.net.ssl.OpenJSSE
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep class okhttp3.Authenticator$* { *; }
-dontwarn okhttp3.Authenticator$*

-keep class okhttp3.Dns$* { *; }
-dontwarn okhttp3.Dns$*

-keep class okhttp3.CookieJar$* { *; }
-dontwarn okhttp3.CookieJar$*

-keep class okhttp3.logging.HttpLoggingInterceptor$* { *; }
-dontwarn okhttp3.logging.HttpLoggingInterceptor$*

-keep class okhttp3.internal.http2.PushObserver$* { *; }
-dontwarn okhttp3.internal.http2.PushObserver$*

-keep class okhttp3.internal.io.FileSystem$* { *; }
-dontwarn okhttp3.internal.io.FileSystem$*
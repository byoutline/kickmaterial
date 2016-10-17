# GENERAL
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes Signature,*Annotation*,EnclosingMethod,SourceFile,LineNumberTable
-dontnote

-keepclassmembers enum * { *; }

# Picasso
 -dontwarn com.squareup.okhttp.**
# Retrofit 2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn okio.**
-dontwarn org.apache.**
-dontwarn java.lang.invoke.*

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Findbugs
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings

-keep class com.byoutline.kickmaterial.model.** { *; }
#OTTO
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# EVENTS
-keep class com.byoutline.secretsauce.events.** {*;}

# REMOVE LOGS
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
    public static *** i(...);
}


# SAVE MAPPINGS
-printmapping ../release/mapping.txt



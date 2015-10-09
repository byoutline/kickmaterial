# BUTTERKNIFE
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

# DAGGER
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection
# Gradle includes dagger-compiler and javawriter in the final package
-dontwarn dagger.internal.codegen.**
-dontwarn com.squareup.javawriter.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# RETROFIT
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.appengine.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.stream.** { *; }
-dontwarn okio.**
-dontwarn org.apache.**
-dontwarn retrofit.client.ApacheClient$GenericEntityHttpRequest
-dontwarn retrofit.client.ApacheClient$GenericHttpRequest
-dontwarn retrofit.client.ApacheClient$TypedOutputEntity

#SMACK
-keep class de.** { *; }
-keep class org.jivesoftware.** { *;}
-keep class org.apache.** { *;}
-keep class com.novell.** { *;}
-keep class org.xbill.** { *;}

#LITHIENT
-keep class com.lithient.** { *; }

#GSON
-keep class com.high5it.api.model.** { *; }
-keepclassmembers enum * { *; }

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keep class com.high5it.model.** { *; }
#OTTO
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#google play services
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

# EVENTS
-keep class com.high5it.events.** {*;}
-keep class com.byoutline.secretsauce.events.** {*;}

# FACEBOOK
-keep class com.facebook.** { *; }
#ASMACK
-dontwarn sun.net.spi.nameservice.**
# REMOVE LOGS
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
    public static *** i(...);
}


# GENERAL
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# SecretSauce
-dontwarn com.byoutline.secretsauce.**

# Retrolambda
-dontwarn java.lang.invoke.*

# Splunk MINT
-keep class com.splunk.** { *; }
#-libraryjars libs/mint-4.0.2.jar

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes Signature,*Annotation*,EnclosingMethod,SourceFile,LineNumberTable

# SAVE MAPPINGS
-printmapping ../release/mapping.txt


# GCM
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Picasso
-dontwarn com.squareup.okhttp.**

-keepattributes Signature,LineNumberTable

-keep,includedescriptorclasses,allowoptimization class dev.zt64.hyperion.** { public protected *; }
-keep class dev.zt64.hyperion.MainKt {
    public static void main(java.lang.String[]);
}
-keep class 'module-info'

# Kotlin
-dontwarn kotlinx.datetime.**

# Log4J
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.commons.logging.**
-keep,includedescriptorclasses class org.apache.logging.log4j.** { *; }

# SLF4J
-dontwarn org.slf4j.**
-dontwarn org.apache.logging.slf4j.**
-keep,includedescriptorclasses class org.apache.logging.slf4j.** { *; }

# XML
-dontwarn org.apache.batik.**
-dontwarn javax.xml.**
-dontwarn jdk.xml.**
-dontwarn org.w3c.dom.**
-dontwarn org.xml.**

# Other
-dontwarn androidx.compose.desktop.**
-dontwarn org.apache.tika.**
-dontwarn org.koin.compose.stable.**
-dontwarn com.godaddy.android.colorpicker.harmony.**
-dontwarn io.ktor.client.plugins.**
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-dontwarn java.lang.invoke.MethodHandle

#------------------------# OkHttp #------------------------#

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

#------------------------# Kotlinx serialization #------------------------#

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

# disable optimisation for descriptor field because in some versions of ProGuard, optimization generates incorrect bytecode that causes a verification error
# see https://github.com/Kotlin/kotlinx.serialization/issues/2719
-keepclassmembers public class **$$serializer {
    private ** descriptor;
}

-keepclasseswithmembers, allowshrinking, allowobfuscation class
dev.zt64.hyperion.api.network.dto.browse.ApiChannel$Header # <-- List serializable classes with named companions.
{
    *;
}

#------------------------# Ktor #------------------------#

# Most of volatile fields are updated with AtomicFU and should not be mangled/removed
-keepclassmembers class io.ktor.** {
    volatile <fields>;
}

-keepclassmembernames class io.ktor.** {
    volatile <fields>;
}

# client engines are loaded using ServiceLoader so we need to keep them
-keep class io.ktor.client.engine.** implements io.ktor.client.HttpClientEngineContainer
-keep class io.ktor.serialization.kotlinx.** implements io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider

-keep class kotlinx.coroutines.swing.SwingDispatcherFactory { *; }
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
-dontwarn org.apache.tika.**
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

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

# Kotlin serialization looks up the generated serializer classes through a function on companion
# objects. The companions are looked up reflectively so we need to explicitly keep these functions.
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
# If a companion has the serializer function, keep the companion field on the original type so that
# the reflective lookup succeeds.
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
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
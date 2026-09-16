# EvoFit release shrinker rules.
# R8 handles most AndroidX, Compose, CameraX, and Kotlin rules through dependency
# consumer rules. These rules keep app pieces that can be reached by generated
# code, reflection, or framework callbacks.

# Keep Kotlin metadata for libraries/frameworks that inspect Kotlin declarations.
-keep class kotlin.Metadata { *; }

# Room database, DAO, migrations, and entities.
-keep class com.evolixtechnologies.evofit.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# CameraX analyzer/service callbacks.
-keep class com.evolixtechnologies.evofit.core.camera.** { *; }
-keep class com.evolixtechnologies.evofit.core.sensors.StepTrackingService { *; }

# Keep app entry point and manifest-referenced classes.
-keep class com.evolixtechnologies.evofit.MainActivity { *; }

# Keep enum values usable after obfuscation.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve useful line numbers/source file names for Play Console stack traces.
-keepattributes SourceFile,LineNumberTable

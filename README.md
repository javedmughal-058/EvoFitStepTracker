# EvoFit - Step Tracker

Native Android Kotlin / Jetpack Compose project for Evolix Technologies.

## Architecture
- Kotlin + Jetpack Compose + Material 3
- Feature-first package structure with core/data/domain separation
- Room for local health measurements
- CameraX for experimental camera PPG heart-rate estimation
- Android TYPE_STEP_COUNTER for pedometer functionality
- SharedPreferences persistence for daily step baseline and onboarding state
- Navigation Compose

## Included flows
- Splash
- Onboarding
- Personal setup
- Daily goal setup
- Permission information
- Home dashboard
- Day / Week / Month activity screen
- Measure hub
- Camera heart-rate intro, measurement, and result
- Manual cuff blood-pressure logging
- Sleep logging UI
- Profile
- About / Evolix Technologies branding

## Important health note
The phone-camera heart-rate feature is an experimental wellness estimate based on photoplethysmography-style luminance changes. It is not a medical device, should not be used for diagnosis, and should be validated extensively before production release.

A standard phone camera cannot directly produce clinically reliable systolic/diastolic blood-pressure measurements. This project therefore records BP from a validated cuff rather than falsely presenting camera BP as a medical measurement.

## Android Studio setup
Recommended:
- Android Studio Ladybug or newer
- JDK 17
- Android SDK Platform 35
- Physical Android phone for step sensor and camera/flash testing

### Open the project
1. Extract the ZIP.
2. Android Studio > File > Open.
3. Select the `EvoFitStepTracker` root folder.
4. If Android Studio asks for a Gradle JDK, select JDK 17 / Embedded JDK 17.
5. Let Gradle sync and download dependencies.

This source bundle intentionally does not include a Gradle wrapper binary JAR. If Android Studio requires a wrapper, the easiest path is:
1. Create a temporary Empty Activity native Kotlin project in Android Studio using the same JDK.
2. Copy that project's `gradle/`, `gradlew`, and `gradlew.bat` into this project root.
3. Re-open/sync this project.

Alternatively, if you already have Gradle 8.10.x installed globally, run `gradle wrapper` once in the project root.

### Run
1. Enable Developer Options and USB debugging on an Android phone.
2. Connect the phone by USB.
3. Accept the RSA debugging prompt.
4. Select the phone in Android Studio's device selector.
5. Choose the `app` run configuration.
6. Press Run.

The app will request Activity Recognition after onboarding and Camera permission only when the heart-rate flow starts.

## Package
`com.evolixtechnologies.evofit`

Before Play Store release, change the application ID to the final package you control and add production signing.

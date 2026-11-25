# Us, Forever.

A private, mobile-first romantic memory journal for Android with a forest-green and champagne-gold interface, a procedural WebGL beating heart, sealed memory cards and image reveals.

## Install
Open the repository's Actions tab, select the successful Build Android APK run, download Us-Forever-APK, unzip it and open the APK on Android (8.0 or newer). Allow installation from the app used to open the file if prompted.

## Build
Java 17, Android SDK 35, Gradle 8.9. Run `gradle assembleDebug`.

## Privacy
This version works offline, stores data in IndexedDB inside the app and supports JSON backups. It does not provide cloud synchronization, account authentication or encryption for time capsules. Export backups regularly; uninstalling can erase local memories. Do not commit personal backups or photos to this repository. The APK is a debug-signed test build, not a Play Store release. Physical-device testing is still required.

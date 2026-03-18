# JdaSense: Mobile Build & APK Generation Guide

This guide explains how to build the JdaSense Android application, generate the Gradle wrapper, and produce APKs for testing and production.

---

## 🛠 Prerequisites
1.  **Java Development Kit (JDK):** Version 17 is recommended for Android 14 (API 34).
2.  **Android SDK:** Ensure `build-tools` and `platform-tools` for API 34 are installed.
3.  **Android Studio:** (Optional but recommended) Iguana or later.

---

## 🏗 1. Generate Gradle Wrapper
Since the project was initialized without a wrapper, you need to generate it once using an installed version of Gradle:

```bash
cd mobile
gradle wrapper
```
*Note: If you don't have Gradle installed, simply opening the `mobile/` folder in **Android Studio** will generate these files for you automatically.*

---

## 🚀 2. Build APK via Command Line

### Build Debug APK (For Testing)
The Debug APK is unsigned and suitable for quick installation on emulators or physical devices.
```bash
cd mobile
./gradlew assembleDebug
```
*   **Output Path:** `mobile/app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK (For Production)
The Release APK is optimized and requires signing before it can be installed on a device or uploaded to the Play Store.
```bash
cd mobile
./gradlew assembleRelease
```
*   **Output Path:** `mobile/app/build/outputs/apk/release/app-release-unsigned.apk`

---

## ✍️ 3. Signing the Release APK
To install a Release build, you must sign it using `apksigner`.

1.  **Generate a Keystore (if you don't have one):**
    ```bash
    keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
    ```
2.  **Sign the APK:**
    ```bash
    apksigner sign --ks my-release-key.jks --out app-release-signed.apk app-release-unsigned.apk
    ```

---

## 🎨 4. Build via Android Studio (Visual Way)
1.  Open **Android Studio**.
2.  Select **Open** and choose the `mobile/` directory.
3.  Wait for the Gradle sync to finish.
4.  **To run on device:** Click the green **Run** arrow in the top toolbar.
5.  **To generate APK:**
    *   Go to `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`.
    *   Studio will show a notification with a "Locate" link once the APK is ready.

---

## 🧹 5. Useful Maintenance Commands
*   **Clean Build:** ` ./gradlew clean` (Fixes most "mysterious" build errors).
*   **Stop Gradle Daemon:** `./gradlew --stop` (Saves RAM on your Mac).

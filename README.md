# Scott AI for Huawei P20 (Android 10)

A starter Android app with a futuristic dark interface, tap-to-speak speech recognition, Swahili text-to-speech where a compatible voice is installed, and basic app-opening / dial / SMS intents.

## Build an APK with GitHub Actions

1. Upload the contents of this project folder into the root of your GitHub repository (the `.github` folder must also be at the repository root).
2. Commit the files to the `main` branch.
3. Open the repository's **Actions** tab.
4. Select **Build Scott AI APK** and press **Run workflow** (or wait for the automatic build after pushing to `main`).
5. Open the successful run and download the `ScottAI-debug-apk` artifact.
6. Extract the downloaded artifact ZIP on your phone and install `app-debug.apk`.

## Current starter capabilities

- Futuristic dark Compose interface
- Tap-to-speak using Android's installed speech recognition service
- Swahili text-to-speech where the device has a Swahili voice installed
- Basic commands to open WhatsApp / YouTube / Settings, open the dialer, and open SMS composer

## Important limitations

- This starter does **not** implement an always-listening "Hey Scott" wake word.
- It does **not** yet provide an online conversational AI backend.
- WhatsApp sending, contact-name dialing, notification reading, and background listening need additional implementation and permissions.
- Speech recognition and the Swahili female voice depend on services and voice data installed on the Huawei device.
- This project builds a debug APK for testing; it is not a Play Store release build.

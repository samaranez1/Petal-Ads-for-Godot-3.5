# Godot Petal Ads Plugin

This folder contains a Godot 3.5 Android plugin source bridge for Huawei Petal Ads.

## Build

1. Open `android/petalads-plugin` in Android Studio, or run Gradle from this folder.
2. Build the release AAR:

```powershell
./gradlew.bat :petalads:assembleRelease
```

3. Copy the generated AAR:

```text
android/petalads-plugin/petalads/build/outputs/aar/petalads-release.aar
```

to:

```text
android/plugins/GodotPetalAds.release.aar
```

4. Add or enable `android/plugins/GodotPetalAds.gdap` in the Android export preset.

## Godot Singleton

The plugin exposes:

- `init()`
- `loadBanner(ad_id)`
- `showBanner()`
- `hideBanner()`
- `loadRewardedVideo(ad_id)`
- `isRewardedVideoLoaded()`
- `showRewardedVideo()`

Signals:

- `banner_loaded`
- `banner_failed_to_load(error_code)`
- `rewarded_video_loaded`
- `rewarded_video_closed`
- `rewarded_video_failed_to_load(error_code)`
- `rewarded(currency, amount)`

## Notes

The plugin uses Huawei's `com.huawei.hms:ads-lite` dependency and Huawei's Maven repository.
Petal Ads works best on Huawei devices with HMS Core installed.

# Godot Petal Ads Plugin (Godot 3.5)

A Godot 3.5 Android plugin for Huawei Petal Ads (HMS Ads Kit).
## Setup
1. Create your app in AppGallery Connect
2. Download your own `agconnect-services.json`
3. Place it in `android/build/assets/`
## AndroidManifest.xml
Replace `YOUR_APP_ID_HERE` with your App ID from AppGallery Connect → Project Settings.

## Features
- Banner ads
- Rewarded video ads

## Requirements
- Godot 3.5.x with Android custom build
- `agconnect-services.json` from AppGallery Connect
- Huawei Developer account

## Installation
1. Copy `GodotPetalAds.aar` and `GodotPetalAds.gdap` to `android/plugins/`
2. Place `agconnect-services.json` in `android/build/assets/`
3. Enable plugin in Project → Export → Android → Plugins

## Dependencies (build.gradle)
```groovy
implementation 'com.huawei.hms:ads-prime:3.4.80.301'
```

## Usage
Add `AdManager.gd` as an AutoLoad singleton.

```gdscript
# Show banner
AdManager.show_banner()

# Show rewarded video
AdManager.show_rewarded_video()

# Connect reward signal
AdManager.connect("player_rewarded", self, "_on_rewarded")
func _on_rewarded(type, amount):
    lives += 1
```

## Test IDs
- Banner: `testw6vs28auh3`
- Rewarded: `testx9dtjwj8hp`

## Notes
- Banner ads work on non-Huawei devices, If you want show the test Ads download the huawei app gallery then sign-in and install HMS core
- Rewarded ads require HMS Core (Huawei/Honor devices)
- Tested on Godot 3.5.3# Godot Petal Ads Plugin

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
# Petal Ads for Godot 3.5
Petal Ads

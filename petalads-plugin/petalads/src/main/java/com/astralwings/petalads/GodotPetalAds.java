package com.astralwings.petalads;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GodotPetalAds extends GodotPlugin {

    private final Activity activity;
    private BannerView bannerView;
    private RewardAd rewardAd;

    private boolean initialized = false;
    private boolean bannerLoading = false;
    private boolean rewardLoading = false;
    private boolean rewardedLoaded = false;

    public GodotPetalAds(Godot godot) {
        super(godot);
        this.activity = getActivity();
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotPetalAds";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "init",
                "loadBanner",
                "showBanner",
                "hideBanner",
                "loadRewardedVideo",
                "isRewardedVideoLoaded",
                "showRewardedVideo"
        );
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("banner_loaded"));
        signals.add(new SignalInfo("banner_failed_to_load", Integer.class));
        signals.add(new SignalInfo("rewarded_video_loaded"));
        signals.add(new SignalInfo("rewarded_video_closed"));
        signals.add(new SignalInfo("rewarded_video_failed_to_load", Integer.class));
        signals.add(new SignalInfo("rewarded", String.class, Integer.class));
        return signals;
    }

    // ── INIT ─────────────────────────────────────────────────────────────────

    @UsedByGodot
    public void init() {
        if (initialized) return;
        initialized = true;
        activity.runOnUiThread(() -> {
            HwAds.init(activity);
        });
    }

    // ── BANNER ───────────────────────────────────────────────────────────────

    @UsedByGodot
    public void loadBanner(final String adId) {
        if (bannerLoading) return;
        bannerLoading = true;

        activity.runOnUiThread(() -> {
            // Remove old banner if exists
            if (bannerView != null) {
                ViewGroup parent = (ViewGroup) bannerView.getParent();
                if (parent != null) parent.removeView(bannerView);
                bannerView.destroy();
                bannerView = null;
            }

            bannerView = new BannerView(activity);
            bannerView.setAdId(adId);
            bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);
            bannerView.setVisibility(BannerView.GONE); // hide until loaded

            bannerView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    bannerLoading = false;
                    activity.runOnUiThread(() -> {
                        bannerView.setVisibility(BannerView.VISIBLE);
                        bannerView.bringToFront();
                    });
                    emitSignal("banner_loaded");
                }

                @Override
                public void onAdFailed(int errorCode) {
                    bannerLoading = false;
                    emitSignal("banner_failed_to_load", errorCode);
                }
            });

            // Add to Godot's layout so it renders on top of the game
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            ((ViewGroup) activity.findViewById(android.R.id.content)).addView(bannerView, params);

            bannerView.loadAd(new AdParam.Builder().build());
        });
    }

    @UsedByGodot
    public void showBanner() {
        activity.runOnUiThread(() -> {
            if (bannerView != null) {
                bannerView.setVisibility(BannerView.VISIBLE);
                bannerView.bringToFront();
            }
        });
    }

    @UsedByGodot
    public void hideBanner() {
        activity.runOnUiThread(() -> {
            if (bannerView != null) {
                bannerView.setVisibility(BannerView.GONE);
            }
        });
    }

    // ── REWARDED ─────────────────────────────────────────────────────────────

    @UsedByGodot
    public void loadRewardedVideo(final String adId) {
        if (rewardLoading || rewardedLoaded) return;
        rewardLoading = true;

        activity.runOnUiThread(() -> {
            rewardAd = new RewardAd(activity, adId);
            rewardAd.loadAd(new AdParam.Builder().build(), new RewardAdLoadListener() {
                @Override
                public void onRewardedLoaded() {
                    rewardLoading = false;
                    rewardedLoaded = true;
                    emitSignal("rewarded_video_loaded");
                }

                @Override
                public void onRewardAdFailedToLoad(int errorCode) {
                    rewardLoading = false;
                    rewardedLoaded = false;
                    emitSignal("rewarded_video_failed_to_load", errorCode);
                }
            });
        });
    }

    @UsedByGodot
    public boolean isRewardedVideoLoaded() {
        return rewardedLoaded && rewardAd != null;
    }

    @UsedByGodot
    public void showRewardedVideo() {
        activity.runOnUiThread(() -> {
            if (rewardAd == null || !rewardedLoaded) {
                emitSignal("rewarded_video_failed_to_load", -1);
                return;
            }
            rewardAd.show(activity, new RewardAdStatusListener() {
                @Override
                public void onRewardAdOpened() {}

                @Override
                public void onRewardAdClosed() {
                    rewardedLoaded = false;
                    emitSignal("rewarded_video_closed");
                }

                @Override
                public void onRewarded(Reward reward) {
                    if (reward != null) {
                        emitSignal("rewarded", reward.getName(), reward.getAmount());
                    } else {
                        emitSignal("rewarded", "Reward", 1);
                    }
                }

                @Override
                public void onRewardAdFailedToShow(int errorCode) {
                    rewardedLoaded = false;
                    emitSignal("rewarded_video_failed_to_load", errorCode);
                }
            });
        });
    }

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────

    @Override
    public void onMainPause() {
        if (bannerView != null) bannerView.pause();
    }

    @Override
    public void onMainResume() {
        if (bannerView != null) bannerView.resume();
    }

    @Override
    public void onMainDestroy() {
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
    }
}
package org.xiyu.yee.createplus.features;

import org.xiyu.yee.createplus.utils.ConfigManager;
import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    private final List<CreativePlusFeature> features = new ArrayList<>();

    public FeatureManager() {
        // 先注册功能
        registerFeatures();
        // 在所有功能注册后再加载配置
        // ConfigManager.loadConfig(); // 移除这行
    }

    private void registerFeatures() {
        // 飞行增强
        features.add(new ImprovedFlight());
        // 无限放置
        features.add(new InfinitePlacement());
        // 快速建造
        features.add(new SpeedBuild());
        // 区域复制
        features.add(new AreaCopy());
        // 镜像建造
        features.add(new MirrorBuild());
        features.add(new SpinBot());
        features.add(new Nucker());
        features.add(new AreaPlace());
        features.add(new BlockColorSwap());
        features.add(new BuildingExport());
        features.add(new Scaffold());
        features.add(new GammaOverride());
    }

    public void onTick() {
        for (CreativePlusFeature feature : features) {
            if (feature.isEnabled()) {
                feature.onTick();
            }
        }
    }

    // 当功能状态改变时保存配置
    public void onFeatureToggle() {
        ConfigManager.saveConfig();
    }

    public List<CreativePlusFeature> getFeatures() {
        return features;
    }

    public void registerFeature(CreativePlusFeature feature) {
        features.add(feature);
    }
} 
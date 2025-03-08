package org.xiyu.yee.createplus.features;

import org.xiyu.yee.createplus.utils.ConfigManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureManager {
    private final Map<String, CreativePlusFeature> features = new HashMap<>();
    private final List<CreativePlusFeature> featureList = new ArrayList<>();

    public FeatureManager() {
        registerFeatures();
        ConfigManager.init(); // ��ʼ������������
    }

    private void registerFeatures() {
        // ������ǿ
        featureList.add(new ImprovedFlight());
        // ���޷���
        featureList.add(new InfinitePlacement());
        // ���ٽ���
        featureList.add(new SpeedBuild());
        // ������
        featureList.add(new AreaCopy());
        // ������
        featureList.add(new MirrorBuild());
        featureList.add(new SpinBot());
        featureList.add(new Nucker());
        featureList.add(new AreaPlace());
        featureList.add(new BlockColorSwap());
        featureList.add(new BuildingExport());
        featureList.add(new Scaffold());
        featureList.add(new GammaOverride());
        registerFeature(new PingDisplay());
    }

    public void onTick() {
        for (CreativePlusFeature feature : featureList) {
            if (feature.isEnabled()) {
                feature.onTick();
            }
        }
    }

    // ������״̬�ı�ʱ��������
    public void onFeatureToggle() {
        ConfigManager.saveConfig();
    }

    public List<CreativePlusFeature> getFeatures() {
        return featureList;
    }

    public void registerFeature(CreativePlusFeature feature) {
        features.put(feature.getName(), feature);
        featureList.add(feature);
    }

    public CreativePlusFeature getFeature(String name) {
        return features.get(name);
    }

    public void onEnable(CreativePlusFeature feature) {
        feature.setEnabled(true);
        feature.onEnable();
    }

    public void onDisable(CreativePlusFeature feature) {
        feature.setEnabled(false);
        feature.onDisable();
    }
} 
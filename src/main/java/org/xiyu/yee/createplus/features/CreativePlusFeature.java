package org.xiyu.yee.createplus.features;

import net.minecraft.network.chat.Component;
import org.xiyu.yee.createplus.Createplus;

public abstract class CreativePlusFeature {
    private boolean enabled = false;
    private final Component name;
    private final Component description;

    public CreativePlusFeature(String translationKey, String descriptionKey) {
        this.name = Component.translatable(translationKey);
        this.description = Component.translatable(descriptionKey);
    }

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onTick();

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        // 通知FeatureManager保存配置
        Createplus.FEATURE_MANAGER.onFeatureToggle();
    }

    public void enable() {
        enabled = true;
        onEnable();
    }

    public void disable() {
        enabled = false;
        onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name.getString();
    }

    public String getDescription() {
        return description.getString();
    }

    public void handleClick(boolean isRightClick) {
        // 默认空实现，子类可以重写
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }
} 
package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;

public class SpeedBuild extends CreativePlusFeature {
    public SpeedBuild() {
        super("快速建造", "移除放置冷却");
    }

    @Override
    public void onTick() {
        Minecraft.getInstance().rightClickDelay = 0;
    }

    @Override
    public void onEnable() {
        // 不需要特殊初始化
    }

    @Override
    public void onDisable() {
        // 不需要特殊清理
    }
} 
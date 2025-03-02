package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class GammaOverride extends CreativePlusFeature {

    public GammaOverride() {
        super("夜视", "获得永久夜视效果");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 每tick给玩家添加20秒的夜视效果
        // 使用较长的持续时间避免闪烁
        mc.player.addEffect(new MobEffectInstance(
            MobEffects.NIGHT_VISION,
            400, // 20秒
            0,   // 等级0
            false, // 不显示粒子
            false  // 不显示图标
        ));
    }

    @Override
    public void onEnable() {
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("§b夜视已启用")
        );
    }

    @Override
    public void onDisable() {
        // 移除夜视效果
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
        
        mc.player.sendSystemMessage(
            Component.literal("§7夜视已禁用")
        );
    }
} 
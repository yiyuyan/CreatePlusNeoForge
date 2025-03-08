package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ImprovedFlight extends CreativePlusFeature {
    private static final float DEFAULT_FLY_SPEED = 0.05f;
    private float originalFlySpeed;

    public ImprovedFlight() {
        super("�Ľ�����", "�ṩ����ķ����ٶȺ͸���ȷ�Ŀ���");
    }

    @Override
    public void onEnable() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            originalFlySpeed = player.getAbilities().getFlyingSpeed();
            player.getAbilities().setFlyingSpeed(DEFAULT_FLY_SPEED * 5.0f);
        }
    }

    @Override
    public void onDisable() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getAbilities().setFlyingSpeed(originalFlySpeed);
        }
    }

    @Override
    public void onTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.input.jumping) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1.1, 1.1, 1.1));
        }
    }
} 
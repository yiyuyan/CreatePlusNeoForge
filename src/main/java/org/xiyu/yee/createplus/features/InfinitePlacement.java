package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class InfinitePlacement extends CreativePlusFeature {
    public InfinitePlacement() {
        super("���޷���", "����ģʽ����Ʒ���޷���");
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack mainHand = mc.player.getMainHandItem();
            if (!mainHand.isEmpty()) {
                mainHand.setCount(64);
            }
        }
    }
} 
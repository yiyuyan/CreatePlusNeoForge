package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;

public class SpeedBuild extends CreativePlusFeature {
    public SpeedBuild() {
        super("���ٽ���", "�Ƴ�������ȴ");
    }

    @Override
    public void onTick() {
        Minecraft.getInstance().rightClickDelay = 0;
    }

    @Override
    public void onEnable() {
        // ����Ҫ�����ʼ��
    }

    @Override
    public void onDisable() {
        // ����Ҫ��������
    }
} 
package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import org.xiyu.yee.createplus.utils.KeyBindings;

public class Zoom extends CreativePlusFeature {
    private boolean isZooming = false;
    private float zoomLevel = 4.0f;
    private int originalFov;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 10.0f;
    private static final float ZOOM_STEP = 0.5f;
    private static final float SMOOTH_FACTOR = 0.15f;
    private float currentZoom = 1.0f;

    public Zoom() {
        super("�ӽ�����", "������������ӽ�");
    }

    @Override
    public void onEnable() {
        // ����Ҫ�����ʼ��
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();

        // �������ſ���
        if (KeyBindings.TOGGLE_ZOOM.isDown()) {
            if (!isZooming) {
                // ��ʼ����
                isZooming = true;
                originalFov = mc.options.fov().get();
            }
            
            // ����������
            double scroll = mc.mouseHandler.getXVelocity();
            if (scroll != 0) {
                float delta = (float) (Math.signum(scroll) * ZOOM_STEP);
                zoomLevel = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomLevel + delta));
            }
            
            // ƽ������
            currentZoom += (zoomLevel - currentZoom) * SMOOTH_FACTOR;
            // ȷ��FOVֵ����Ч��Χ�� (30-110)
            int newFov = (int) Math.max(30, Math.min(110, originalFov / currentZoom));
            mc.options.fov().set(newFov);
            
        } else if (isZooming) {
            // ��������
            isZooming = false;
            currentZoom = 1.0f;
            mc.options.fov().set(originalFov);
        }
    }

    @Override
    public void onDisable() {
        if (isZooming) {
            Minecraft mc = Minecraft.getInstance();
            isZooming = false;
            currentZoom = 1.0f;
            mc.options.fov().set(originalFov);
        }
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n��7��ס C ��������");
            if (isZooming) {
                desc.append("\n��7��ǰ����: ��e").append(String.format("%.1fx", zoomLevel));
                desc.append("\n��7ʹ�������ֵ�������");
            }
        }
        return desc.toString();
    }
} 
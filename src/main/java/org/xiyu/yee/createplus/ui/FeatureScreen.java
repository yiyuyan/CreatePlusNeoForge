package org.xiyu.yee.createplus.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;
import org.xiyu.yee.createplus.features.SubHUDFeature;

import java.awt.*;

public class FeatureScreen extends Screen {
    private static boolean visible = false;
    private int selectedIndex = 0;
    private long lastKeyPressTime = 0;
    private static final long KEY_COOLDOWN = 150; // 150�������ȴʱ��
    
    // UI��ɫ����
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 180).getRGB();
    private static final int HEADER_COLOR = new Color(0, 111, 255).getRGB();
    private static final int SELECTED_COLOR = new Color(0, 111, 255, 100).getRGB();
    private static final int ENABLED_COLOR = new Color(0, 255, 255).getRGB();
    private static final int DISABLED_COLOR = new Color(170, 170, 170).getRGB();

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 5;
    private static final int COLUMNS = 2;
    
    public FeatureScreen() {
        super(Component.literal("CreatePlus���ܲ˵�"));
        NeoForge.EVENT_BUS.register(this);
    }

    public static void toggleVisibility() {
        visible = !visible;
    }

    private void renderFeatures(GuiGraphics graphics) {
        if (!visible || Minecraft.getInstance().options.hideGui) return;

        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        // �����Ͻǻ���UI
        int startX = 5;
        int startY = 5;
        int width = 120;
        
        // ���ư�͸������
        graphics.fill(startX, startY, startX + width, startY + 17 + features.size() * 12, BACKGROUND_COLOR);
        
        // ���Ʊ���
        graphics.drawString(Minecraft.getInstance().font, 
            "CreatePlus", startX + 4, startY + 5, HEADER_COLOR, true);

        // ���ƹ����б�
        for (int i = 0; i < features.size(); i++) {
            CreativePlusFeature feature = features.get(i);
            int y = startY + 19 + i * 12;
            
            // ����ѡ�б���
            if (i == selectedIndex) {
                graphics.fill(startX, y - 1, startX + width, y + 11, SELECTED_COLOR);
            }

            // ���ƹ�������
            int textColor = feature.isEnabled() ? ENABLED_COLOR : DISABLED_COLOR;
            graphics.drawString(Minecraft.getInstance().font,
                feature.getName(), startX + 4, y, textColor, true);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible || Minecraft.getInstance().options.hideGui) return;

        // ��Ⱦ������
        renderFeatures(graphics);
        
        // ��ȡ��ǰѡ�еĹ���
        CreativePlusFeature selectedFeature = null;
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        if (selectedIndex >= 0 && selectedIndex < features.size()) {
            selectedFeature = features.get(selectedIndex);
        }

        // ����Ǵ���HUD�Ĺ���������������HUD�ɼ�����Ⱦ����HUD
        if (selectedFeature instanceof SubHUDFeature subHUDFeature && 
            selectedFeature.isEnabled() && 
            subHUDFeature.isSubHUDVisible()) {
            // ���������Ҳ���Ⱦ��HUD
            int subHudX = 130; // ����λ�ã���������HUD�ص�
            int subHudY = 5;   // ����HUD����
            subHUDFeature.renderSubHUD(graphics, subHudX, subHudY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyInput(InputEvent.Key event) {
        // �����ȴʱ��
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyPressTime < KEY_COOLDOWN) {
            return;
        }

        boolean keyHandled = false;
        int keyCode = event.getKey();
        
        // ֻ��UI�ɼ�ʱ������
        if (visible) {
            // ��ȡ��ǰѡ�еĹ���
            CreativePlusFeature selectedFeature = null;
            var features = Createplus.FEATURE_MANAGER.getFeatures();
            if (selectedIndex >= 0 && selectedIndex < features.size()) {
                selectedFeature = features.get(selectedIndex);
            }

            // ����Ǵ���HUD�Ĺ���������������HUD�ɼ������ȴ������������
            if (selectedFeature instanceof SubHUDFeature subHUDFeature && 
                selectedFeature.isEnabled() && 
                subHUDFeature.isSubHUDVisible()) {
                if (subHUDFeature.handleKeyPress(keyCode)) {
                    keyHandled = true;
                }
            } else {
                // ��������HUD�ĵ���
                switch (keyCode) {
                    case 265: // �ϼ�ͷ
                        selectedIndex = Math.max(0, selectedIndex - 1);
                        keyHandled = true;
                        break;
                    case 264: // �¼�ͷ
                        selectedIndex = Math.min(features.size() - 1, selectedIndex + 1);
                        keyHandled = true;
                        break;
                    case 257: // �س�
                        if (selectedFeature != null) {
                            selectedFeature.toggle();
                            keyHandled = true;
                        }
                        break;
                    case 262: // �Ҽ�ͷ
                        if (selectedFeature instanceof SubHUDFeature subHUDFeature) {
                            subHUDFeature.toggleSubHUD();
                            keyHandled = true;
                        }
                        break;
                }
            }
        }

        // F9�л�UI��ʾ
        if (keyCode == 36) {  // F9
            toggleVisibility();
            keyHandled = true;
        }

        // ��������������ˣ�������󰴼�ʱ��
        if (keyHandled) {
            lastKeyPressTime = currentTime;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGuiLayerEvent.Post event) {
        // �Ƴ������������Ϊ�����Ѿ���render�����д�������Ⱦ
    }
} 
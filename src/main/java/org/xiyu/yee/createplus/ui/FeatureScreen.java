package org.xiyu.yee.createplus.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;
import org.xiyu.yee.createplus.features.SpeedAdjust;
import org.xiyu.yee.createplus.features.SubHUDFeature;

import java.awt.*;
import org.lwjgl.glfw.GLFW;

public class FeatureScreen extends Screen {
    private static boolean visible = false;
    private int selectedIndex = 0;
    private long lastKeyPressTime = 0;
    private static final long KEY_COOLDOWN = 150; // 150毫秒的冷却时间
    
    // UI颜色常量
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
        super(Component.literal("CreatePlus功能菜单"));
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void toggleVisibility() {
        visible = !visible;
    }

    private void renderFeatures(GuiGraphics graphics) {
        if (!visible || Minecraft.getInstance().options.hideGui) return;

        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        // 在左上角绘制UI
        int startX = 5;
        int startY = 5;
        int width = 120;
        
        // 绘制半透明背景
        graphics.fill(startX, startY, startX + width, startY + 17 + features.size() * 12, BACKGROUND_COLOR);
        
        // 绘制标题
        graphics.drawString(Minecraft.getInstance().font, 
            "CreatePlus", startX + 4, startY + 5, HEADER_COLOR, true);

        // 绘制功能列表
        for (int i = 0; i < features.size(); i++) {
            CreativePlusFeature feature = features.get(i);
            int y = startY + 19 + i * 12;
            
            // 绘制选中背景
            if (i == selectedIndex) {
                graphics.fill(startX, y - 1, startX + width, y + 11, SELECTED_COLOR);
            }

            // 绘制功能名称
            int textColor = feature.isEnabled() ? ENABLED_COLOR : DISABLED_COLOR;
            graphics.drawString(Minecraft.getInstance().font,
                feature.getName(), startX + 4, y, textColor, true);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible || Minecraft.getInstance().options.hideGui) return;

        // 渲染主界面
        renderFeatures(graphics);
        
        // 获取当前选中的功能
        CreativePlusFeature selectedFeature = null;
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        if (selectedIndex >= 0 && selectedIndex < features.size()) {
            selectedFeature = features.get(selectedIndex);
        }

        // 如果是带子HUD的功能且已启用且子HUD可见，渲染其子HUD
        if (selectedFeature instanceof SubHUDFeature subHUDFeature && 
            selectedFeature.isEnabled() && 
            subHUDFeature.isSubHUDVisible()) {
            // 在主界面右侧渲染子HUD
            int subHudX = 130; // 调整位置，避免与主HUD重叠
            int subHudY = 5;   // 与主HUD对齐
            subHUDFeature.renderSubHUD(graphics, subHudX, subHudY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyInput(InputEvent.Key event) {
        // 检查冷却时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyPressTime < KEY_COOLDOWN) {
            return;
        }

        boolean keyHandled = false;
        int keyCode = event.getKey();
        
        // 只在UI可见时处理按键
        if (visible) {
            // 获取当前选中的功能
            CreativePlusFeature selectedFeature = null;
            var features = Createplus.FEATURE_MANAGER.getFeatures();
            if (selectedIndex >= 0 && selectedIndex < features.size()) {
                selectedFeature = features.get(selectedIndex);
            }

            // 如果是带子HUD的功能且已启用且子HUD可见，优先处理其键盘输入
            if (selectedFeature instanceof SubHUDFeature subHUDFeature && 
                selectedFeature.isEnabled() && 
                subHUDFeature.isSubHUDVisible()) {
                if (subHUDFeature.handleKeyPress(keyCode)) {
                    keyHandled = true;
                }
            } else {
                // 否则处理主HUD的导航
                switch (keyCode) {
                    case 265: // 上箭头
                        selectedIndex = Math.max(0, selectedIndex - 1);
                        keyHandled = true;
                        break;
                    case 264: // 下箭头
                        selectedIndex = Math.min(features.size() - 1, selectedIndex + 1);
                        keyHandled = true;
                        break;
                    case 257: // 回车
                        if (selectedFeature != null) {
                            selectedFeature.toggle();
                            keyHandled = true;
                        }
                        break;
                    case 262: // 右箭头
                        if (selectedFeature instanceof SubHUDFeature subHUDFeature) {
                            subHUDFeature.toggleSubHUD();
                            keyHandled = true;
                        }
                        break;
                }
            }
        }

        // F9切换UI显示
        if (keyCode == 36) {  // F9
            toggleVisibility();
            keyHandled = true;
        }

        // 如果按键被处理了，更新最后按键时间
        if (keyHandled) {
            lastKeyPressTime = currentTime;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        // 移除这个方法，因为我们已经在render方法中处理了渲染
    }
} 
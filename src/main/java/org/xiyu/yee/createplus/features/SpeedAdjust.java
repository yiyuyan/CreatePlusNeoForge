package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.Map;

public class SpeedAdjust extends CreativePlusFeature implements SubHUDFeature {
    private final Map<SpeedType, Float> speedMultipliers = new HashMap<>();
    private SpeedType selectedType = SpeedType.WALK;
    private boolean showSubHUD = false;
    
    public enum SpeedType {
        WALK("行走速度", 0.1f, 10.0f, 1.0f),
        FLY("飞行速度", 0.1f, 10.0f, 1.0f),
        RIDE("骑乘速度", 0.1f, 10.0f, 1.0f),
        SWIM("游泳速度", 0.1f, 10.0f, 1.0f),
        ELYTRA("鞘翅速度", 0.1f, 10.0f, 1.0f);

        final String name;
        final float min;
        final float max;
        final float defaultValue;

        SpeedType(String name, float min, float max, float defaultValue) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;
        }
    }

    public SpeedAdjust() {
        super("速度调整", "调整各种移动速度");
        for (SpeedType type : SpeedType.values()) {
            speedMultipliers.put(type, type.defaultValue);
        }
    }

    @Override
    public void handleClick(boolean isRightClick) {
        // 不需要处理点击事件
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n§7当前选择: §e").append(selectedType.name);
            if (showSubHUD) {
                desc.append("\n§7↑↓ 选择速度类型");
                desc.append("\n§7+/- 调整速度值");
                desc.append("\n§7← 返回");
            }
        } else {
            desc.append("\n§7回车键启用");
        }
        return desc.toString();
    }

    @Override
    public boolean handleKeyPress(int keyCode) {
        if (!isEnabled()) return false;

        if (showSubHUD) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT:
                    // 左方向键退出子HUD
                    showSubHUD = false;
                    saveSettings();
                    return true;
                    
                case GLFW.GLFW_KEY_UP:
                    // 上方向键选择上一个选项
                    selectPreviousType();
                    return true;
                    
                case GLFW.GLFW_KEY_DOWN:
                    // 下方向键选择下一个选项
                    selectNextType();
                    return true;
                    
                case GLFW.GLFW_KEY_EQUAL:        // = 键
                case GLFW.GLFW_KEY_KP_ADD:       // 小键盘 +
                    adjustCurrentSpeed(0.1f);
                    return true;
                    
                case GLFW.GLFW_KEY_MINUS:        // - 键
                case GLFW.GLFW_KEY_KP_SUBTRACT:  // 小键盘 -
                    adjustCurrentSpeed(-0.1f);
                    return true;
            }
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            // 右方向键打开子HUD
            showSubHUD = true;
            return true;
        }
        
        return false;
    }

    @Override
    public boolean isSubHUDVisible() {
        return showSubHUD;
    }

    @Override
    public void toggleSubHUD() {
        showSubHUD = !showSubHUD;
    }

    @Override
    public void renderSubHUD(GuiGraphics graphics, int x, int y) {
        if (!isEnabled() || !showSubHUD) return;

        // 绘制背景
        int width = 150;
        int itemHeight = 20;
        int totalHeight = SpeedType.values().length * itemHeight;
        
        // 主面板背景
        graphics.fill(x, y, x + width, y + totalHeight + 25, 0x80000000);
        
        // 绘制每个速度类型
        for (int i = 0; i < SpeedType.values().length; i++) {
            SpeedType type = SpeedType.values()[i];
            int itemY = y + i * itemHeight;
            
            // 当前选中项高亮
            if (type == selectedType) {
                graphics.fill(x, itemY, x + width, itemY + itemHeight, 0x80FFFFFF);
            }
            
            // 绘制名称和值
            String text = String.format("%s: %.1fx", type.name, speedMultipliers.get(type));
            graphics.drawString(
                Minecraft.getInstance().font,
                text,
                x + 5,
                itemY + 6,
                type == selectedType ? 0xFFFFFF : 0xAAAAAA
            );
        }
        
        // 绘制操作提示
        String hint = "↑↓ 选择 | +/- 调整 | ← 返回";
        graphics.drawString(
            Minecraft.getInstance().font,
            hint,
            x + 5,
            y + totalHeight + 5,
            0xAAAAAA
        );
    }

    private void selectPreviousType() {
        int prevIndex = (selectedType.ordinal() - 1 + SpeedType.values().length) % SpeedType.values().length;
        selectedType = SpeedType.values()[prevIndex];
    }

    private void selectNextType() {
        int nextIndex = (selectedType.ordinal() + 1) % SpeedType.values().length;
        selectedType = SpeedType.values()[nextIndex];
    }

    private void adjustCurrentSpeed(float delta) {
        float current = speedMultipliers.get(selectedType);
        setSpeedMultiplier(selectedType, 
            Math.max(selectedType.min, 
            Math.min(selectedType.max, current + delta)));
    }

    private void saveSettings() {
        // 这里可以添加保存到配置文件的逻辑
        // 目前只是保存在内存中
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            showSubHUD = false;  // 启用时不显示面板，等待用户按右方向键打开
            for (SpeedType type : SpeedType.values()) {
                setSpeedMultiplier(type, 2.0f);
            }
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // 恢复默认速度
            for (SpeedType type : SpeedType.values()) {
                setSpeedMultiplier(type, type.defaultValue);
            }
            // 关闭子HUD
            showSubHUD = false;
        }
    }

    @Override
    public void onTick() {
        // 移除tick中的键盘检查，完全依赖handleKeyPress
    }

    /**
     * 设置指定类型的速度倍率
     * @param type 速度类型
     * @param multiplier 速度倍率
     */
    private void setSpeedMultiplier(SpeedType type, float multiplier) {
        // 确保倍率在有效范围内
        multiplier = Math.max(type.min, Math.min(type.max, multiplier));
        speedMultipliers.put(type, multiplier);
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Abilities abilities = mc.player.getAbilities();
            switch (type) {
                case WALK:
                    abilities.setWalkingSpeed(0.1f * multiplier);
                    mc.player.getAttribute(Attributes.MOVEMENT_SPEED)
                        .setBaseValue(0.1 * multiplier);
                    break;
                case FLY:
                    abilities.setFlyingSpeed(0.05f * multiplier);
                    break;
                case RIDE:
                    // 骑乘速度调整暂未实现
                    break;
                case SWIM:
                    // 游泳速度调整暂未实现
                    break;
                case ELYTRA:
                    // 鞘翅速度调整暂未实现
                    break;
            }
            
            // 同步能力更新到客户端
            mc.player.onUpdateAbilities();
        }
    }
} 
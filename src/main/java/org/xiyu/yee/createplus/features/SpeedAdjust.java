package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import java.util.UUID;

public class SpeedAdjust extends CreativePlusFeature implements SubHUDFeature {
    private final Map<SpeedType, Float> speedMultipliers = new HashMap<>();
    private SpeedType selectedType = SpeedType.WALK;
    private boolean showSubHUD = false;
    private float spinBotSpeed = 1.0f;
    private static final float MIN_SPINBOT_SPEED = 0.1f;
    private static final float MAX_SPINBOT_SPEED = 10.0f;
    private static final float SPINBOT_SPEED_STEP = 0.1f;
    
    // Ϊÿ���ٶ����ʹ���Ψһ��UUID
    private static final UUID SWIM_SPEED_UUID = UUID.fromString("9c33c313-0d71-4d44-b033-6c7fbaa5f034");
    private static final UUID ELYTRA_SPEED_UUID = UUID.fromString("3b8c1e6e-32dd-4b51-a807-7c5c0a4485c5");
    private static final UUID RIDE_SPEED_UUID = UUID.fromString("2c2d6933-3e3a-4b40-a923-b4906d7d2b5a");
    private static final UUID SPINBOT_SPEED_UUID = UUID.fromString("1d2e3f4a-5b6c-7d8e-9f0a-1b2c3d4e5f6a");

    public enum SpeedType {
        WALK("�����ٶ�", 0.1f, 10.0f, 1.0f),
        FLY("�����ٶ�", 0.1f, 10.0f, 1.0f),
        RIDE("����ٶ�", 0.1f, 10.0f, 1.0f),
        SWIM("��Ӿ�ٶ�", 0.1f, 10.0f, 1.0f),
        ELYTRA("�ʳ��ٶ�", 0.1f, 10.0f, 1.0f),
        SPINBOT("�����ٶ�", MIN_SPINBOT_SPEED, MAX_SPINBOT_SPEED, 1.0f);

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
        super("�ٶȵ���", "���������ƶ��ٶ�");
        for (SpeedType type : SpeedType.values()) {
            speedMultipliers.put(type, type.defaultValue);
        }
    }

    @Override
    public void handleClick(boolean isRightClick) {
        // ����Ҫ�������¼�
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n��7��ǰѡ��: ��e").append(selectedType.name);
            if (showSubHUD) {
                desc.append("\n��7���� ѡ���ٶ�����");
                desc.append("\n��7+/- �����ٶ�ֵ");
                desc.append("\n��7�� ����");
            }
        } else {
            desc.append("\n��7�س�������");
        }
        return desc.toString();
    }

    @Override
    public boolean handleKeyPress(int keyCode) {
        if (!isEnabled()) return false;

        if (showSubHUD) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT:
                    // ������˳���HUD
                    showSubHUD = false;
                    saveSettings();
                    return true;
                    
                case GLFW.GLFW_KEY_UP:
                    // �Ϸ����ѡ����һ��ѡ��
                    selectPreviousType();
                    return true;
                    
                case GLFW.GLFW_KEY_DOWN:
                    // �·����ѡ����һ��ѡ��
                    selectNextType();
                    return true;
                    
                case GLFW.GLFW_KEY_EQUAL:        // = ��
                case GLFW.GLFW_KEY_KP_ADD:       // С���� +
                    adjustCurrentSpeed(0.1f);
                    return true;
                    
                case GLFW.GLFW_KEY_MINUS:        // - ��
                case GLFW.GLFW_KEY_KP_SUBTRACT:  // С���� -
                    adjustCurrentSpeed(-0.1f);
                    return true;
            }
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            // �ҷ��������HUD
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

        // ���Ʊ���
        int width = 150;
        int itemHeight = 20;
        int totalHeight = SpeedType.values().length * itemHeight;
        
        // ����屳��
        graphics.fill(x, y, x + width, y + totalHeight + 25, 0x80000000);
        
        // ����ÿ���ٶ�����
        for (int i = 0; i < SpeedType.values().length; i++) {
            SpeedType type = SpeedType.values()[i];
            int itemY = y + i * itemHeight;
            
            // ��ǰѡ�������
            if (type == selectedType) {
                graphics.fill(x, itemY, x + width, itemY + itemHeight, 0x80FFFFFF);
            }
            
            // �������ƺ�ֵ
            String text = String.format("%s: %.1fx", type.name, speedMultipliers.get(type));
            graphics.drawString(
                Minecraft.getInstance().font,
                text,
                x + 5,
                itemY + 6,
                type == selectedType ? 0xFFFFFF : 0xAAAAAA
            );
        }
        
        // ���Ʋ�����ʾ
        String hint = "���� ѡ�� | +/- ���� | �� ����";
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
        // ���������ӱ��浽�����ļ����߼�
        // Ŀǰֻ�Ǳ������ڴ���
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            showSubHUD = false;  // ����ʱ����ʾ��壬�ȴ��û����ҷ������
            for (SpeedType type : SpeedType.values()) {
                setSpeedMultiplier(type, 2.0f);
            }
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // �ָ�Ĭ���ٶ�
            for (SpeedType type : SpeedType.values()) {
                setSpeedMultiplier(type, type.defaultValue);
            }
            // �ر���HUD
            showSubHUD = false;
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // ��鲢Ӧ����Ӿ�ٶ�
        if (player.isSwimming() || player.isInWater()) {
            float swimSpeed = speedMultipliers.getOrDefault(SpeedType.SWIM, 1.0f);
            // ֱ���޸�����ٶ�����
            if (swimSpeed != 1.0f) {
                player.setDeltaMovement(player.getDeltaMovement().multiply(swimSpeed, swimSpeed, swimSpeed));
            }
        }

        // ��鲢Ӧ���ʳ��ٶ�
        if (player.isFallFlying()) {
            float elytraSpeed = speedMultipliers.getOrDefault(SpeedType.ELYTRA, 1.0f);
            player.setDeltaMovement(player.getDeltaMovement().multiply(elytraSpeed, 1.0, elytraSpeed));
        }

        // ��鲢Ӧ������ٶ�
        if (player.getVehicle() instanceof LivingEntity mount) {
            float rideSpeed = speedMultipliers.getOrDefault(SpeedType.RIDE, 1.0f);
            if (rideSpeed != 1.0f) {
                mount.setDeltaMovement(mount.getDeltaMovement().multiply(rideSpeed, 1.0, rideSpeed));
            }
        }
    }

    /**
     * ����ָ�����͵��ٶȱ���
     * @param type �ٶ�����
     * @param multiplier �ٶȱ���
     */
    private void setSpeedMultiplier(SpeedType type, float multiplier) {
        // ȷ����������Ч��Χ��
        multiplier = Math.max(type.min, Math.min(type.max, multiplier));
        // �����ٶ�ֵ
        speedMultipliers.put(type, multiplier);
        // Ӧ���ٶ�
        applySpeed(type, multiplier);
    }

    private void applySpeed(SpeedType type, float value) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        switch (type) {
            case WALK:
                player.getAbilities().setWalkingSpeed(0.1f * value);
                player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1 * value);
                break;

            case FLY:
                player.getAbilities().setFlyingSpeed(0.05f * value);
                break;

            case SWIM:
                speedMultipliers.put(SpeedType.SWIM, value);
                break;

            case ELYTRA:
                speedMultipliers.put(SpeedType.ELYTRA, value);
                break;

            case RIDE:
                speedMultipliers.put(SpeedType.RIDE, value);
                break;

            case SPINBOT:
                speedMultipliers.put(SpeedType.SPINBOT, value);
                break;
        }

        player.onUpdateAbilities();
    }

    public float getSpinBotSpeed() {
        return speedMultipliers.getOrDefault(SpeedType.SPINBOT, 1.0f);
    }

    public float getSpeedValue(SpeedType type) {
        return speedMultipliers.getOrDefault(type, type.defaultValue);
    }

    public void setSpeedValue(SpeedType type, float value) {
        setSpeedMultiplier(type, value);
    }
} 
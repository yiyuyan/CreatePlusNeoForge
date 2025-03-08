package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;

public class SpinBot extends CreativePlusFeature {
    private float currentRotation = 0f;
    private static final float SPIN_SPEED = 20f; // 每tick旋转的度数
    private float originalYaw = 0f;
    private boolean isSpinning = false;

    public SpinBot() {
        super("feature.createplus.spinbot.name", "feature.createplus.spinbot.description");
    }

    @Override
    public void onEnable() {
        NeoForge.EVENT_BUS.register(this);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            originalYaw = player.getYRot();
            isSpinning = true;
            player.sendSystemMessage(Component.translatable("message.createplus.spinbot.enabled"));
        }
    }

    @Override
    public void onDisable() {
        NeoForge.EVENT_BUS.unregister(this);
        isSpinning = false;
        currentRotation = 0f;
        // 恢复原始视角
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.setYRot(originalYaw);
            // 同步到服务器
            sendRotationPacket(player, originalYaw);
        }
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !player.isCreative() || !isSpinning) return;

        // 更新旋转角度
        currentRotation = (currentRotation + SPIN_SPEED) % 360;

        // 在第三人称或其他玩家视角时发送旋转数据包
        if (!mc.options.getCameraType().isFirstPerson()) {
            sendRotationPacket(player, currentRotation);
        }
    }

    private void sendRotationPacket(Player player, float yaw) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.Rot(
                yaw,
                player.getXRot(),
                player.onGround()
            ));
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (event.getEntity() == Minecraft.getInstance().player && isSpinning) {
            // 在渲染时设置玩家模型的旋转
            event.getPoseStack().mulPose(
                com.mojang.math.Axis.YP.rotationDegrees(currentRotation)
            );
        }
    }
} 
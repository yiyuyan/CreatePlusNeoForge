package org.xiyu.yee.createplus.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.xiyu.yee.createplus.utils.KeyBindings;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;

import org.xiyu.yee.createplus.api.FreecamAPI;

public class Freecam extends CreativePlusFeature {
    private Vec3 originalPosition;
    private float originalYRot;
    private float originalXRot;
    private float originalFlySpeed;
    private GameType originalGameMode; // ��¼ԭʼ��Ϸģʽ
    private float flySpeed = 1.0f;
    private static final float MIN_SPEED = 0.1f;
    private static final float MAX_SPEED = 5.0f;
    private static final float SPEED_STEP = 0.1f;

    public Freecam() {
        super("������", "����������������ɹ۲�");
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // ��鰴��״̬
        if (KeyBindings.TOGGLE_FREECAM.consumeClick()) {
            toggleFreecam();
        }

        // �������������״̬���������λ��
        if (FreecamAPI.isFreecam()) {
            updateCamera();
        }

        // ���������ֵ����ٶ�
        //vanilla spectator game mode will instead these
        /*while (mc.mouseHandler.getXVelocity() != 0) {
            float delta = (float) (Math.signum(mc.mouseHandler.getXVelocity()) * SPEED_STEP);
            flySpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, flySpeed + delta));
        }*/
    }

    private void toggleFreecam() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) return;

        if (!FreecamAPI.isFreecam()) {
            // ����ԭʼ״̬
            originalPosition = mc.player.position();
            originalYRot = mc.player.getYRot();
            originalXRot = mc.player.getXRot();
            originalFlySpeed = mc.player.getAbilities().getFlyingSpeed();
            originalGameMode = mc.gameMode.getPlayerMode(); // ����ԭʼ��Ϸģʽ

            // ����������
            FreecamAPI.setFreecamEnabled(true);
            FreecamAPI.setFreecamPosition(originalPosition);
            FreecamAPI.setFreecamRotation(originalYRot, originalXRot);
            
            // �л����Թ���ģʽ
            mc.gameMode.setLocalMode(GameType.SPECTATOR);
            
        } else {
            // �ָ�ԭʼ״̬
            mc.player.setPos(originalPosition.x, originalPosition.y, originalPosition.z);
            mc.player.setYRot(originalYRot);
            mc.player.setXRot(originalXRot);
            mc.player.getAbilities().setFlyingSpeed(originalFlySpeed);
            
            // ����������
            FreecamAPI.setFreecamEnabled(false);
            
            // �ָ�ԭʼ��Ϸģʽ
            mc.gameMode.setLocalMode(originalGameMode);
        }
    }

    private void updateCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // �����ƶ�����
        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();
        float forward = 0;
        float up = 0;
        float strafe = 0;

        if (mc.options.keyUp.isDown()) forward += 1;
        if (mc.options.keyDown.isDown()) forward -= 1;
        if (mc.options.keyLeft.isDown()) strafe -= 1;
        if (mc.options.keyRight.isDown()) strafe += 1;
        if (mc.options.keyJump.isDown()) up += 1;
        if (mc.options.keyShift.isDown()) up -= 1;

        // �����ƶ�����
        double rad = Math.toRadians(yaw);
        double dx = (strafe * Math.cos(rad) - forward * Math.sin(rad)) * flySpeed;
        double dz = (forward * Math.cos(rad) + strafe * Math.sin(rad)) * flySpeed;
        double dy = up * flySpeed;

        // �������λ��
        Vec3 pos = FreecamAPI.getFreecamPos();
        Vec3 newPos = new Vec3(pos.x + dx, pos.y + dy, pos.z + dz);
        FreecamAPI.setFreecamPosition(newPos);
        FreecamAPI.setFreecamRotation(yaw, pitch);
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!FreecamAPI.isFreecam() || !isEnabled()) return;
        
        // �����Ⱦ���Ǳ�����ң��Ҵ���������״̬
        if (event.getEntity() == Minecraft.getInstance().player) {
            // ��Ⱦ�������
            renderPlayerOutline(event.getPoseStack(), event.getEntity());
        }
    }

    private void renderPlayerOutline(PoseStack poseStack, Entity entity) {
        // ���浱ǰ��Ⱦ״̬
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        
        // ��ȡ��ұ߽��
        AABB box = entity.getBoundingBox();
        float r = 0.0f;
        float g = 1.0f;
        float b = 1.0f;
        float alpha = 0.4f;
        
        // ��Ⱦ��͸������
        renderBox(poseStack, box, r, g, b, alpha);
        
        // �ָ���Ⱦ״̬
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void renderBox(PoseStack poseStack, AABB box, float r, float g, float b, float alpha) {
        // ��ȡTesselator��BufferBuilder
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        // ��ʼ����
        
        // ���Ʊ߽�������
        // �ײ�����
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.minZ, r, g, b, alpha);
        
        // ��������
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.minZ, r, g, b, alpha);
        
        // ������
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.minZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.minY, box.maxZ, r, g, b, alpha);
        vertex(bufferBuilder, poseStack, box.minX, box.maxY, box.maxZ, r, g, b, alpha);
        
        // ��������
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private void vertex(BufferBuilder bufferBuilder, PoseStack poseStack, double x, double y, double z, float r, float g, float b, float alpha) {
        bufferBuilder.addVertex(poseStack.last().pose(), (float)x, (float)y, (float)z)
            .setColor(r, g, b, alpha)
            ;
    }

    @Override
    public void onDisable() {
        if (FreecamAPI.isFreecam()) {
            toggleFreecam();
        }
    }

    @Override
    public void onEnable() {
        // ����Ҫ�����ʼ��
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n��7�� F6 ����������");
            if (FreecamAPI.isFreecam()) {
                desc.append("\n��7��ǰ�����ٶ�: ��e").append(String.format("%.1f", flySpeed));
                desc.append("\n��7ʹ�������ֵ����ٶ�");
                desc.append("\n��7ԭʼģʽ: ��e").append(originalGameMode.getName());
            }
        }
        return desc.toString();
    }
} 
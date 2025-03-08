package org.xiyu.yee.createplus.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMixin {
    private static final ResourceLocation[] LOADING_FRAMES = new ResourceLocation[6];
    private static final int FRAME_TIME = 50; // ����֡��
    private long startTime = -1;
    private int currentFrame = 0;
    private boolean isLoading = true;
    
    static {
        // ��ʼ������֡����Դλ��
        for (int i = 0; i < LOADING_FRAMES.length; i++) {
            LOADING_FRAMES[i] = ResourceLocation.tryBuild("createplus",
                String.format("textures/gui/loading/frame_%d.png", i));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(PoseStack poseStack, MultiBufferSource p_168773_, MapId p_324127_, MapItemSavedData p_168775_, boolean p_168776_, int p_168774_, CallbackInfo ci) {
        if (!isLoading) {
            return;
        }

        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        
        // ���㵱ǰ֡
        currentFrame = (int)((elapsedTime / FRAME_TIME) % LOADING_FRAMES.length);
        
        // ��Ⱦ���ض���
        renderLoadingFrame(poseStack);
        
        // ����Ƿ�Ӧ�ý�������
        if (elapsedTime > FRAME_TIME * LOADING_FRAMES.length * 2) { // ��������ѭ��
            isLoading = false;
            startTime = -1;
            return;
        }
        
        // ȡ��ԭʼ��Ⱦ
        ci.cancel();
    }

    private void renderLoadingFrame(PoseStack poseStack) {
        // ���浱ǰ����״̬
        poseStack.pushPose();
        
        // ������Ⱦ״̬
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, LOADING_FRAMES[currentFrame]);

        // ��ȡMinecraftʵ���ʹ��ڳߴ�
        Minecraft mc = Minecraft.getInstance();
        float scale = (float)mc.getWindow().getGuiScale();
        
        // ������Ⱦλ��(����)
        float mapSize = 128.0F;
        float x = (mc.getWindow().getGuiScaledWidth() - mapSize) / 2;
        float y = (mc.getWindow().getGuiScaledHeight() - mapSize) / 2;
        
        // ��Ⱦ��ǰ֡
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        // ���������ı���
        bufferbuilder.addVertex(poseStack.last().pose(), x, y + mapSize, 0).setUv(0, 1);
        bufferbuilder.addVertex(poseStack.last().pose(), x + mapSize, y + mapSize, 0).setUv(1, 1);
        bufferbuilder.addVertex(poseStack.last().pose(), x + mapSize, y, 0).setUv(1, 0);
        bufferbuilder.addVertex(poseStack.last().pose(), x, y, 0).setUv(0, 0);
        
        // �����Ⱦ
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        
        // �ָ���Ⱦ״̬
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
} 
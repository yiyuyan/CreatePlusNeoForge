package org.xiyu.yee.createplus.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;


public class RenderUtils {
    public static void drawBox(PoseStack poseStack, AABB box, float red, float green, float blue, float alpha) {
        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource()
            .getBuffer(RenderType.lines());
            
        LevelRenderer.renderLineBox(
            poseStack, builder,
            box.minX, box.minY, box.minZ,
            box.maxX, box.maxY, box.maxZ,
            red, green, blue, alpha
        );
    }

    public static void renderText(PoseStack poseStack, String text, double x, double y, double z, int color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.font == null) return;

        poseStack.pushPose();
        
        // 获取玩家相机位置
        net.minecraft.world.phys.Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        
        // 计算渲染位置
        double dx = x - camera.x;
        double dy = y - camera.y;
        double dz = z - camera.z;
        
        // 设置渲染位置
        poseStack.translate(dx, dy, dz);
        
        // 使文本始终面向玩家
        poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        // 创建Component
        Component textComponent = Component.literal(text);
        
        // 获取渲染缓冲
        var bufferSource = mc.renderBuffers().bufferSource();
        
        // 渲染文本
        mc.font.drawInBatch(
            textComponent,
            -mc.font.width(text) / 2.0F,
            0,
            color,
            false,
            poseStack.last().pose(),
            bufferSource,
            net.minecraft.client.gui.Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 确保文本被渲染
        bufferSource.endBatch();
        
        poseStack.popPose();
    }

    public static void drawEdgeLines(PoseStack poseStack, AABB box, float red, float green, float blue, float alpha) {
        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource()
            .getBuffer(RenderType.lines());
        
        // 绘制12条边
        // 底部四条边
        drawLine(poseStack, builder, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, red, green, blue, alpha);
        
        // 顶部四条边
        drawLine(poseStack, builder, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, red, green, blue, alpha);
        
        // 竖直四条边
        drawLine(poseStack, builder, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
        drawLine(poseStack, builder, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawCornerMarkers(PoseStack poseStack, AABB box, float red, float green, float blue, float alpha) {
        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource()
            .getBuffer(RenderType.lines());
        
        float markerSize = 0.3f;
        
        // 绘制8个角点标记
        drawCornerMarker(poseStack, builder, box.minX, box.minY, box.minZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.maxX, box.minY, box.minZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.maxX, box.minY, box.maxZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.minX, box.minY, box.maxZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.minX, box.maxY, box.minZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.maxX, box.maxY, box.minZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.maxX, box.maxY, box.maxZ, markerSize, red, green, blue, alpha);
        drawCornerMarker(poseStack, builder, box.minX, box.maxY, box.maxZ, markerSize, red, green, blue, alpha);
    }

    private static void drawLine(PoseStack poseStack, VertexConsumer builder, 
            double x1, double y1, double z1, 
            double x2, double y2, double z2, 
            float red, float green, float blue, float alpha) {
        Matrix4f matrix = poseStack.last().pose();
        builder.vertex(matrix, (float)x1, (float)y1, (float)z1)
            .color(red, green, blue, alpha)
            .normal(1, 0, 0)
            .endVertex();
        builder.vertex(matrix, (float)x2, (float)y2, (float)z2)
            .color(red, green, blue, alpha)
            .normal(1, 0, 0)
            .endVertex();
    }

    private static void drawCornerMarker(PoseStack poseStack, VertexConsumer builder, 
            double x, double y, double z, float size,
            float red, float green, float blue, float alpha) {
        // X轴标记
        drawLine(poseStack, builder, x-size, y, z, x+size, y, z, red, green, blue, alpha);
        // Y轴标记
        drawLine(poseStack, builder, x, y-size, z, x, y+size, z, red, green, blue, alpha);
        // Z轴标记
        drawLine(poseStack, builder, x, y, z-size, x, y, z+size, red, green, blue, alpha);
    }
} 
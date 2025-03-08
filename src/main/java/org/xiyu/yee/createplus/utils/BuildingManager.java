package org.xiyu.yee.createplus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class BuildingManager {
    private static final BuildingManager INSTANCE = new BuildingManager();
    private BlockPos firstPos;
    private BlockPos secondPos;
    private Map<BlockPos, BlockState> selectedBlocks = new HashMap<>();
    private List<String> pendingCommands = new ArrayList<>();
    private boolean isPreviewActive = false;
    private AABB previewBox;
    private CommandSourceStack currentSource;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private int blockCount = 0;
    private double volume = 0;

    public static BuildingManager getInstance() {
        return INSTANCE;
    }

    private BuildingManager() {
        NeoForge.EVENT_BUS.register(this);
    }

    public void setFirstPos(BlockPos pos) {
        firstPos = pos;
    }

    public void setSecondPos(BlockPos pos) {
        secondPos = pos;
        if (firstPos != null) {
            updateSelection();
        }
    }

    public BlockPos getFirstPos() {
        return firstPos;
    }

    public boolean hasSelection() {
        return firstPos != null && secondPos != null;
    }

    public Map<BlockPos, BlockState> getSelectedBlocks() {
        return selectedBlocks;
    }

    private void updateSelection() {
        selectedBlocks.clear();
        if (firstPos == null || secondPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int minX = Math.min(firstPos.getX(), secondPos.getX());
        int minY = Math.min(firstPos.getY(), secondPos.getY());
        int minZ = Math.min(firstPos.getZ(), secondPos.getZ());
        int maxX = Math.max(firstPos.getX(), secondPos.getX());
        int maxY = Math.max(firstPos.getY(), secondPos.getY());
        int maxZ = Math.max(firstPos.getZ(), secondPos.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.level.getBlockState(pos);
                    if (!state.isAir()) {
                        selectedBlocks.put(pos, state);
                    }
                }
            }
        }
    }

    public void startPreview(Path filePath, CommandSourceStack source) {
        try {
            pendingCommands.clear();
            pendingCommands.addAll(Files.readAllLines(filePath));
            currentSource = source;
            isPreviewActive = true;
            
            // ����Ԥ������
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
            blockCount = 0;
            
            for (String command : pendingCommands) {
                if (command.startsWith("setblock")) {
                    blockCount++;
                    String[] parts = command.split(" ");
                    int x = Integer.parseInt(parts[1].substring(1));
                    int y = Integer.parseInt(parts[2].substring(1));
                    int z = Integer.parseInt(parts[3].substring(1));
                    
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    minZ = Math.min(minZ, z);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    maxZ = Math.max(maxZ, z);
                }
            }
            
            // �������
            int width = maxX - minX + 1;
            int height = maxY - minY + 1;
            int depth = maxZ - minZ + 1;
            volume = width * height * depth;
            
            net.minecraft.world.phys.Vec3 pos = source.getPosition();
            BlockPos playerPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
            previewBox = new AABB(
                playerPos.getX() + minX, playerPos.getY() + minY, playerPos.getZ() + minZ,
                playerPos.getX() + maxX + 1, playerPos.getY() + maxY + 1, playerPos.getZ() + maxZ + 1
            );

            // ��ʾ������Ϣ
            source.sendSuccess(() -> 
                Component.literal(String.format(
                    "��a������Ϣ:\n" +
                    "��7- ��С: ��f%dx%dx%d\n" +
                    "��7- ������: ��f%d\n" +
                    "��7- ���: ��f%.1f m?",
                    width, height, depth,
                    blockCount,
                    volume
                )), false);
            
            source.sendSuccess(() -> 
                Component.literal("��a����Ԥ������������ /confirmimport ȷ�ϵ��루�ǵ��л��Թ�ģʽ�������� /cancelimport ȡ��"), false);
            
        } catch (IOException e) {
            source.sendFailure(Component.literal("��c�޷���ȡ�����ļ�: " + e.getMessage()));
        }
    }

    public void confirmImport() {
        if (!isPreviewActive || currentSource == null) return;
        
        // �ر�Ԥ��״̬
        isPreviewActive = false;
        
        // ���Ϳ�ʼ������Ϣ
        currentSource.sendSuccess(() -> 
            Component.literal("��a��ʼ���뽨������ " + pendingCommands.size() + " ������"), false);

        // �����µ�ִ����
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        executor = Executors.newSingleThreadScheduledExecutor();

        // ÿtickִ��һ������
        final Iterator<String> commandIterator = pendingCommands.iterator();
        final int[] blockCount = {0};
        
        executor.scheduleAtFixedRate(() -> {
            try {
                if (commandIterator.hasNext()) {
                    String command = commandIterator.next();
                    // ��鲢���������ʽ
                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }
                    if (!command.startsWith("s") && command.contains("etblock")) {
                        command = "s" + command;
                    }
                    
                    // �������������
                    Minecraft.getInstance().player.connection.sendCommand(command);
                    
                    blockCount[0]++;
                    
                    // ���½�����ʾ
                    float progress = (blockCount[0] * 100.0f) / pendingCommands.size();
                    
                    // ʹ��title��ʾ����
                    Minecraft.getInstance().player.connection.sendCommand(
                        String.format("title @s actionbar {\"text\":\"�������: %.1f%% (%d/%d)\",\"color\":\"green\"}",
                            progress, blockCount[0], pendingCommands.size())
                    );
                    
                } else {
                    // ��ɵ���
                    executor.shutdown();
                    // ���������ʾ
                    Minecraft.getInstance().player.connection.sendCommand(
                        "title @s actionbar {\"text\":\"����������ɣ�\",\"color\":\"green\"}"
                    );
                    currentSource.sendSuccess(() -> 
                        Component.literal("��a����������ɣ�"), false);
                    pendingCommands.clear();
                    previewBox = null;
                }
            } catch (Exception e) {
                currentSource.sendFailure(
                    Component.literal("��c��������г���: " + e.getMessage())
                );
                // ���������ʾ
                Minecraft.getInstance().player.connection.sendCommand(
                    "title @s actionbar {\"text\":\"����ʧ�ܣ�\",\"color\":\"red\"}"
                );
                executor.shutdown();
                pendingCommands.clear();
                previewBox = null;
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void cancelImport() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        isPreviewActive = false;
        pendingCommands.clear();
        previewBox = null;
        
        // ���������ʾ����ʾȡ����Ϣ
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.connection.sendCommand(
                "title @s actionbar {\"text\":\"��ȡ������\",\"color\":\"red\"}"
            );
        }
        
        if (currentSource != null) {
            currentSource.sendSuccess(() -> 
                Component.literal("��c��ȡ������"), false);
        }
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        
        if (isPreviewActive && previewBox != null) {
            // ��ȡ��Ⱦ����
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            
            // ��Ⱦ���߿�
            RenderUtils.drawBox(event.getPoseStack(), previewBox, 0.0F, 1.0F, 0.0F, 0.4F);
            
            // ��Ⱦ�߽���
            RenderUtils.drawEdgeLines(event.getPoseStack(), previewBox, 1.0F, 1.0F, 1.0F, 1.0F);
            
            // ��Ⱦ�ǵ���
            RenderUtils.drawCornerMarkers(event.getPoseStack(), previewBox, 1.0F, 0.0F, 0.0F, 1.0F);
            
            // ��Ⱦ��Ϣ�ı�
            if (Minecraft.getInstance().player != null) {
                double width = previewBox.maxX - previewBox.minX;
                double height = previewBox.maxY - previewBox.minY;
                double depth = previewBox.maxZ - previewBox.minZ;
                
                net.minecraft.world.phys.Vec3 center = previewBox.getCenter();
                
                // ��Ⱦ�ߴ���Ϣ
                RenderUtils.renderText(
                    event.getPoseStack(),
                    String.format("��e%.1f��%.1f��%.1f", width, height, depth),
                    center.x, center.y + height/2 + 0.5, center.z,
                    0xFFFFFF
                );
                
                // ��Ⱦ��������
                RenderUtils.renderText(
                    event.getPoseStack(),
                    String.format("��a%d������", blockCount),
                    center.x, center.y + height/2 + 0.2, center.z,
                    0xFFFFFF
                );
            }
            
            // ������Ⱦ
            bufferSource.endBatch();
        }
    }

    public boolean isPreviewActive() {
        return isPreviewActive;
    }
} 
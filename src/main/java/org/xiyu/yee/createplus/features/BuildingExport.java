package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class BuildingExport extends CreativePlusFeature {
    private BlockPos firstPos = null;
    private BlockPos secondPos = null;
    private Map<BlockPos, BlockState> selectedBlocks = new HashMap<>();

    public BuildingExport() {
        super("��������", "ʹ��ľ��ѡ�����򲢵�������");
    }

    @Override
    public void onEnable() {
        firstPos = null;
        secondPos = null;
        selectedBlocks.clear();
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b�������������ã�ʹ��ľ���Ҽ�ѡ��������")
        );
    }

    @Override
    public void onDisable() {
        firstPos = null;
        secondPos = null;
        selectedBlocks.clear();
    }

    public void handleClick(boolean isRightClick) {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) return;

        // ����Ƿ��ֳ�ľ��
        if (!mc.player.getMainHandItem().is(Items.WOODEN_SHOVEL)) {
            return;
        }

        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
        BlockPos pos = blockHit.getBlockPos();

        if (isRightClick) {
            if (firstPos == null) {
                firstPos = pos;
                mc.player.sendSystemMessage(
                    Component.literal("��a�����õ�һ����: " + pos.toShortString())
                );
            } else {
                secondPos = pos;
                mc.player.sendSystemMessage(
                    Component.literal("��a�����õڶ�����: " + pos.toShortString())
                );
                updateSelection();
            }
        } else {
            // �������ѡ��
            firstPos = null;
            secondPos = null;
            selectedBlocks.clear();
            mc.player.sendSystemMessage(Component.literal("��c������ѡ��"));
        }
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

        mc.player.sendSystemMessage(
            Component.literal("��a��ѡ�� " + selectedBlocks.size() + " �����飬����ʹ�� /exportbuilding <����> ����")
        );
    }

    public boolean hasSelection() {
        return firstPos != null && secondPos != null && !selectedBlocks.isEmpty();
    }

    public BlockPos getFirstPos() {
        return firstPos;
    }

    public Map<BlockPos, BlockState> getSelectedBlocks() {
        return selectedBlocks;
    }

    @Override
    public void onTick() {}
} 
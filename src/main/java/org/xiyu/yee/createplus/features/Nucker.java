package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.ArrayList;
import java.util.List;

public class Nucker extends CreativePlusFeature {
    private BlockPos lastPos = null;
    private static final int BREAK_DELAY = 1; // ticks between breaks
    private int tickCounter = 0;

    public Nucker() {
        super("��Χ�ƻ�", "�����ƻ���Χ�ڵķ���");
    }

    @Override
    public void onEnable() {
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b��Χ�ƻ�������")
        );
    }

    @Override
    public void onDisable() {
        lastPos = null;
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) return;

        tickCounter++;
        if (tickCounter < BREAK_DELAY) return;
        tickCounter = 0;

        // ��ȡ��ҵĴ�������
        double reach = mc.player.blockInteractionRange();
        
        // ��ȡ������߷���ķ���
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
            BlockPos centerPos = blockHit.getBlockPos();
            
            // ������λ���Ѿ������������
            if (centerPos.equals(lastPos)) return;
            lastPos = centerPos;

            // ��ȡ��Χ�ڵ����з���
            List<BlockPos> blocksToBreak = new ArrayList<>();
            int radius = 2; // �ƻ��뾶

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        
                        // ��鷽���Ƿ�����Ҵ�����Χ��
                        if (mc.player.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > reach * reach) {
                            continue;
                        }

                        BlockState state = mc.level.getBlockState(pos);
                        if (!state.isAir()) {
                            blocksToBreak.add(pos);
                        }
                    }
                }
            }

            // �����ƻ����ݰ�
            for (BlockPos pos : blocksToBreak) {
                mc.player.connection.send(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                    pos,
                    Direction.UP,
                    0
                ));
                mc.player.connection.send(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                    pos,
                    Direction.UP,
                    0
                ));
            }
        }
    }
} 
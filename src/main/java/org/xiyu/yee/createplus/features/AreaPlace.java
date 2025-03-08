package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

import java.util.ArrayList;
import java.util.List;

public class AreaPlace extends CreativePlusFeature {
    private static final int PLACE_DELAY = 1;
    private int tickCounter = 0;
    private BlockPos lastPos = null;

    public AreaPlace() {
        super("��Χ����", "ʹ�ø�����Ʒ���з�Χ����");
    }

    @Override
    public void onEnable() {
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b��Χ���������ã����ֳַ��鼴����Ч")
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

        // ��鸱���Ƿ���з���
        ItemStack offhandItem = mc.player.getOffhandItem();
        if (!(offhandItem.getItem() instanceof BlockItem)) return;

        tickCounter++;
        if (tickCounter < PLACE_DELAY) return;
        tickCounter = 0;

        // ��ȡ��ҵĴ�������
        double reach = mc.player.blockInteractionRange();
        
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
            BlockPos centerPos = blockHit.getBlockPos().relative(blockHit.getDirection());
            
            // ������λ���Ѿ������������
            if (centerPos.equals(lastPos)) return;
            lastPos = centerPos;

            // ��ȡ��Χ�ڵ�����λ��
            List<BlockPos> placementPositions = new ArrayList<>();
            int radius = 2;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        
                        // ���λ���Ƿ�����Ҵ�����Χ��
                        if (mc.player.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > reach * reach) {
                            continue;
                        }

                        // ���λ���Ƿ���Է��÷���
                        if (mc.level.getBlockState(pos).isAir()) {
                            placementPositions.add(pos);
                        }
                    }
                }
            }

            // ���ͷ������ݰ�
            for (BlockPos pos : placementPositions) {
                mc.player.connection.send(new ServerboundUseItemOnPacket(
                    InteractionHand.OFF_HAND,
                    new BlockHitResult(mc.player.position(), blockHit.getDirection(), pos, false),
                    0
                ));
            }
        }
    }
} 
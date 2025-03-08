package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

public class Scaffold extends CreativePlusFeature {
    private boolean wasJumping = false;
    private static final int PLACE_DELAY = 1;
    private int tickCounter = 0;

    public Scaffold() {
        super("���ּ�", "��Ծʱ�Զ��ڽ��·��÷���");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) return;

        tickCounter++;
        if (tickCounter < PLACE_DELAY) return;
        tickCounter = 0;

        // ����Ƿ�����Ծ
        boolean isJumping = mc.player.input.jumping;
        
        // ������������Ծ
        if (isJumping && !wasJumping) {
            // ��ȡ������е���Ʒ
            ItemStack heldItem = mc.player.getMainHandItem();
            if (!(heldItem.getItem() instanceof BlockItem)) return;

            // ��ȡ��ҽ��µ�λ��
            BlockPos playerPos = mc.player.blockPosition();
            BlockPos placePos = playerPos.below();
            
            // ��������ǿ��������÷���
            if (mc.level != null && mc.level.getBlockState(placePos).isAir()) {
                // ����������ý��
                BlockHitResult hitResult = new BlockHitResult(
                    Vec3.atBottomCenterOf(placePos.above()), // ���λ��
                    Direction.DOWN, // ���Ϸ�����
                    placePos, // Ŀ�귽��λ��
                    false // inside
                );

                // ���ͷ������ݰ�
                mc.player.connection.send(new ServerboundUseItemOnPacket(
                    InteractionHand.MAIN_HAND,
                    hitResult,
                    0
                ));

                // ���ŷ�����Ч
                mc.player.playSound(
                    net.minecraft.sounds.SoundEvents.STONE_PLACE,
                    1.0F,
                    1.0F
                );
            }
        }
        
        wasJumping = isJumping;
    }

    @Override
    public void onEnable() {
        wasJumping = false;
        tickCounter = 0;
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b���ּ�������")
        );
    }

    @Override
    public void onDisable() {
        wasJumping = false;
        tickCounter = 0;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("��7���ּ��ѽ���")
            );
        }
    }
} 
package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

public class MirrorBuild extends CreativePlusFeature {
    private BlockPos mirrorPoint = null;
    private Direction.Axis mirrorAxis = Direction.Axis.X; // Ĭ��X�᾵��

    public MirrorBuild() {
        super("������", "ͬʱ�ڶԳ�λ�÷��÷���");
    }

    @Override
    public void onEnable() {
        mirrorPoint = null;
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b��ʹ��ľ��: �Ҽ�ѡ����㣬Shift+�Ҽ��л�������(��ǰ: " + getMirrorAxisName() + ")")
        );
    }

    @Override
    public void onDisable() {
        mirrorPoint = null;
    }

    private String getMirrorAxisName() {
        return switch (mirrorAxis) {
            case X -> "X��(����)";
            case Z -> "Z��(�ϱ�)";
            default -> "δ֪";
        };
    }

    public void toggleMirrorAxis() {
        mirrorAxis = (mirrorAxis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("��b���л�������: " + getMirrorAxisName())
        );
    }

    @Override
    public void handleClick(boolean isRightClick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) return;

        // �������Ƿ��ֳ�ľ��
        if (!mc.player.getMainHandItem().is(net.minecraft.world.item.Items.WOODEN_SWORD)) {
            return;
        }

        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) mc.hitResult;

        if (isRightClick) {
            if (mc.player.isShiftKeyDown()) {
                // Shift+�Ҽ����л�������
                toggleMirrorAxis();
            } else {
                // ��ͨ�Ҽ������þ����
                if (mirrorPoint == null) {
                    mirrorPoint = blockHit.getBlockPos();
                    mc.player.sendSystemMessage(
                        Component.literal("��a�����þ����: " + mirrorPoint.toShortString())
                    );
                }
            }
        } else {
            // ��������þ����
            mirrorPoint = null;
            mc.player.sendSystemMessage(Component.literal("��c�����þ����"));
        }
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) return;

        // �����������
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
            
            // ������ڷ��÷������о���㣬���ھ���λ��Ҳ����
            if (mc.options.keyUse.isDown() && mirrorPoint != null) {
                handleBlockPlace((BlockHitResult) mc.hitResult);
            }
        }
    }

    public void handleBlockPlace(BlockHitResult hitResult) {
        if (mirrorPoint == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        BlockPos placePos = hitResult.getBlockPos();
        BlockPos mirroredPos = getMirroredPosition(placePos);

        // ���ͷ��÷������ݰ�������λ��
        mc.player.connection.send(new ServerboundUseItemOnPacket(
            InteractionHand.MAIN_HAND,
            new BlockHitResult(mc.player.position(), hitResult.getDirection(), mirroredPos, false),
            0
        ));
    }

    private BlockPos getMirroredPosition(BlockPos pos) {
        if (mirrorAxis == Direction.Axis.X) {
            // X�᾵�񣺾�����X���� + (������X���� - ԭʼ���X����)
            int deltaX = mirrorPoint.getX() - pos.getX();
            return new BlockPos(mirrorPoint.getX() + deltaX, pos.getY(), pos.getZ());
        } else {
            // Z�᾵�񣺾�����Z���� + (������Z���� - ԭʼ���Z����)
            int deltaZ = mirrorPoint.getZ() - pos.getZ();
            return new BlockPos(pos.getX(), pos.getY(), mirrorPoint.getZ() + deltaZ);
        }
    }
} 
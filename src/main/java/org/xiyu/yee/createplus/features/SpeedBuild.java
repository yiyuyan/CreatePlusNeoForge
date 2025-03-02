package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class SpeedBuild extends CreativePlusFeature {
    private static final int PLACE_DELAY = 1; // 放置延迟（tick）
    private int tickCounter = 0;
    
    public SpeedBuild() {
        super("快速建造", "提高创造模式下的方块放置速度");
    }

    @Override
    public void onEnable() {
        tickCounter = 0;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.gameMode != null && mc.hitResult != null) {
            if (mc.hitResult.getType() == HitResult.Type.BLOCK && mc.player.getMainHandItem().getItem() instanceof BlockItem) {
                if (mc.options.keyUse.isDown() && tickCounter >= PLACE_DELAY) {
                    BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
                    BlockPos placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
                    
                    // 检查是否可以放置方块
                    if (mc.level != null && mc.level.getBlockState(placePos).isAir()) {
                        // 使用正确的方法模拟右键点击
                        mc.gameMode.useItemOn(
                            mc.player,
                            InteractionHand.MAIN_HAND,
                            blockHit
                        );
                        tickCounter = 0;
                    }
                }
            }
        }
        tickCounter++;
    }
} 
package org.xiyu.yee.createplus.tabs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.xiyu.yee.createplus.Createplus;

import java.util.Objects;

public class SpawnEggsTab {
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Createplus.SPAWN_EGGS_TAB.getKey()) {
            // 遍历所有注册的实体类型
            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                // 跳过一些特殊实体
                if (shouldSkipEntity(entityType)) continue;

                // 获取实体ID
                String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
                
                // 检查是否有原版刷怪蛋
                ItemStack spawnEgg = getVanillaSpawnEgg(entityType);
                
                if (spawnEgg == null) {
                    // 如果没有原版刷怪蛋，创建自定义的
                    spawnEgg = createCustomSpawnEgg(entityId);
                }

                // 添加到创造标签页
                try {
                    event.accept(spawnEgg);
                } catch (Exception e) {
                    if(!e.getMessage().contains("already exists in the tab's list")) e.printStackTrace();
                }
            }
        }
    }

    private static boolean shouldSkipEntity(EntityType<?> entityType) {
        // 跳过一些不应该有刷怪蛋的实体
        return entityType == EntityType.PLAYER || 
               entityType == EntityType.FISHING_BOBBER ||
               entityType == EntityType.LIGHTNING_BOLT ||
               entityType == EntityType.AREA_EFFECT_CLOUD ||
               entityType == EntityType.MARKER ||
               entityType == EntityType.ITEM ||
               entityType == EntityType.EXPERIENCE_ORB ||
               entityType == EntityType.TNT ||
               entityType == EntityType.FALLING_BLOCK;
    }

    private static ItemStack getVanillaSpawnEgg(EntityType<?> entityType) {
        // 遍历所有物品查找对应的刷怪蛋
        for (var item : BuiltInRegistries.ITEM) {
            if (item instanceof net.minecraft.world.item.SpawnEggItem spawnEgg) {
                if (spawnEgg.getType(new ItemStack(spawnEgg)) == entityType) {
                    return new ItemStack(item);
                }
            }
        }
        return null;
    }

    private static ItemStack createCustomSpawnEgg(String entityId) {
        try {
            return new ItemStack(Objects.requireNonNull(SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId)))),1);
        } catch (Exception e) {
            return new ItemStack(Items.ALLAY_SPAWN_EGG);
        }
    }
} 
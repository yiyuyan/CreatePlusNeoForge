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
            // ��������ע���ʵ������
            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                // ����һЩ����ʵ��
                if (shouldSkipEntity(entityType)) continue;

                // ��ȡʵ��ID
                String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
                
                // ����Ƿ���ԭ��ˢ�ֵ�
                ItemStack spawnEgg = getVanillaSpawnEgg(entityType);
                
                if (spawnEgg == null) {
                    // ���û��ԭ��ˢ�ֵ��������Զ����
                    spawnEgg = createCustomSpawnEgg(entityId);
                }

                // ��ӵ������ǩҳ
                try {
                    event.accept(spawnEgg);
                } catch (Exception e) {
                    if(!e.getMessage().contains("already exists in the tab's list")) e.printStackTrace();
                }
            }
        }
    }

    private static boolean shouldSkipEntity(EntityType<?> entityType) {
        // ����һЩ��Ӧ����ˢ�ֵ���ʵ��
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
        // ����������Ʒ���Ҷ�Ӧ��ˢ�ֵ�
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
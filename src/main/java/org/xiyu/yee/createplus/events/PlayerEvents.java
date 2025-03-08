package org.xiyu.yee.createplus.events;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.xiyu.yee.createplus.Createplus;
import net.minecraft.ChatFormatting;

@EventBusSubscriber(modid = Createplus.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().sendSystemMessage(Component.literal("=== CreatePlus ģ����� ===").withStyle(ChatFormatting.GOLD));
        event.getEntity().sendSystemMessage(Component.literal("�� F9 �򿪹��ܲ˵�").withStyle(ChatFormatting.YELLOW));
        event.getEntity().sendSystemMessage(Component.literal("���ܲ˵���:").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("- ���·����ѡ����").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("- �س�������/�رչ���").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("- �ҷ�������Ӳ˵�(�����)").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("������ع���:").withStyle(ChatFormatting.AQUA));
        event.getEntity().sendSystemMessage(Component.literal("- ʹ��ľ��ѡ������").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("- /exportbuilding <����> ��������").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("- /importbuilding <����> ���뽨��").withStyle(ChatFormatting.GRAY));
        event.getEntity().sendSystemMessage(Component.literal("���������ʹ�� /features help").withStyle(ChatFormatting.GREEN));
        event.getEntity().sendSystemMessage(Component.literal("===================").withStyle(ChatFormatting.GOLD));
    }
}
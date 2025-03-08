package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeWeatherControl extends CreativePlusFeature {
    private static final List<String> WEATHER_TYPES = Arrays.asList("����", "����", "�ױ�");
    private static final List<String> TIME_PRESETS = Arrays.asList("�ճ�", "����", "����", "ǰҹ", "��ҹ");
    
    public TimeWeatherControl() {
        super("ʱ������", "���Ʊ���ʱ�������");
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage().trim();
        
        if (message.startsWith(".weather ")) {
            event.setCanceled(true);
            handleWeatherCommand(message.substring(9));
        } else if (message.startsWith(".time ")) {
            event.setCanceled(true);
            handleTimeCommand(message.substring(6));
        }
    }

    private void handleWeatherCommand(String type) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        switch (type) {
            case "����" -> mc.level.setRainLevel(0);
            case "����" -> {
                mc.level.setRainLevel(1);
                mc.level.setThunderLevel(0);
            }
            case "�ױ�" -> {
                mc.level.setRainLevel(1);
                mc.level.setThunderLevel(1);
            }
            default -> mc.player.displayClientMessage(
                Component.literal("��c��Ч���������͡���������: ����, ����, �ױ�"), 
                false
            );
        }
    }

    private void handleTimeCommand(String timeStr) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        try {
            long time = switch (timeStr) {
                case "�ճ�" -> 0;
                case "����" -> 6000;
                case "����" -> 12000;
                case "ǰҹ" -> 13000;
                case "��ҹ" -> 18000;
                default -> Long.parseLong(timeStr);
            };
            
            if (time >= 0 && time <= 24000) {
                mc.level.setDayTime(time);
            } else {
                mc.player.displayClientMessage(
                    Component.literal("��cʱ������� 0-24000 ֮��"), 
                    false
                );
            }
        } catch (NumberFormatException e) {
            mc.player.displayClientMessage(
                Component.literal("��c��Ч��ʱ���ʽ������Ԥ��: �ճ�, ����, ����, ǰҹ, ��ҹ"), 
                false
            );
        }
    }

    // TAB��ȫ֧��
    public static List<String> getWeatherSuggestions(String input) {
        return WEATHER_TYPES.stream()
            .filter(type -> type.startsWith(input))
            .collect(Collectors.toList());
    }

    public static List<String> getTimeSuggestions(String input) {
        return TIME_PRESETS.stream()
            .filter(preset -> preset.startsWith(input))
            .collect(Collectors.toList());
    }

    @Override
    public void onEnable() {
        // ����Ҫ�����ʼ��
    }

    @Override
    public void onDisable() {
        // ����Ҫ��������
    }

    @Override
    public void onTick() {
        // ����Ҫtick����
    }
} 
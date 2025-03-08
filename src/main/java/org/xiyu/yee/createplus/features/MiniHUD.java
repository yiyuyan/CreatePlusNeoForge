package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MiniHUD extends CreativePlusFeature {
    private static final int SHADOW_COLOR = 0x000000;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    // ��ɫ����
    private final Map<String, Integer> textColors = new HashMap<>();
    private final Map<String, Integer> valueColors = new HashMap<>();
    
    public MiniHUD() {
        super("����HUD", "�����Ͻ���ʾ��Ϸ��Ϣ");
        NeoForge.EVENT_BUS.register(this);
        initDefaultColors();
    }

    private void initDefaultColors() {
        // Ĭ���ı���ɫ
        textColors.put("fps", 0xFFFFFF);
        textColors.put("time", 0xFFFFFF);
        textColors.put("pos", 0xFFFFFF);
        textColors.put("biome", 0xFFFFFF);
        textColors.put("localtime", 0xFFFFFF);

        // Ĭ����ֵ��ɫ
        valueColors.put("fps", 0xFFFFFF);
        valueColors.put("time", 0xFFFFFF);
        valueColors.put("pos", 0xFFFFFF);
        valueColors.put("biome", 0xFFFFFF);
        valueColors.put("localtime", 0xFFFFFF);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage().trim();
        if (message.startsWith(".hudcolor ")) {
            event.setCanceled(true);
            handleColorCommand(message.substring(10));
        }
    }

    private void handleColorCommand(String args) {
        Minecraft mc = Minecraft.getInstance();
        String[] parts = args.split(" ");
        
        if (parts.length < 3) {
            sendHelp(mc);
            return;
        }

        try {
            String element = parts[0].toLowerCase();
            String type = parts[1].toLowerCase();
            int color = Integer.parseInt(parts[2], 16) | 0xFF000000; // ȷ�� alpha ͨ��Ϊ FF

            if (!isValidElement(element)) {
                mc.player.displayClientMessage(
                    Component.literal("��c��Ч��Ԫ�ء�����Ԫ��: fps, time, pos, biome, localtime"), 
                    false
                );
                return;
            }

            switch (type) {
                case "text" -> textColors.put(element, color);
                case "value" -> valueColors.put(element, color);
                default -> {
                    mc.player.displayClientMessage(
                        Component.literal("��c��Ч�����͡���������: text, value"), 
                        false
                    );
                    return;
                }
            }

            mc.player.displayClientMessage(
                Component.literal(String.format("��a������ %s ��%s��ɫΪ: #%06X", 
                    element, type.equals("text") ? "�ı�" : "��ֵ", color & 0xFFFFFF)), 
                false
            );

        } catch (NumberFormatException e) {
            mc.player.displayClientMessage(
                Component.literal("��c��Ч����ɫ���롣��ʹ��ʮ�����Ƹ�ʽ (����: FFFFFF)"), 
                false
            );
        }
    }

    private void sendHelp(Minecraft mc) {
        mc.player.displayClientMessage(Component.literal("��6=== MiniHUD ��ɫ���ð��� ==="), false);
        mc.player.displayClientMessage(Component.literal("��7�÷�: .hudcolor <Ԫ��> <����> <��ɫ>"), false);
        mc.player.displayClientMessage(Component.literal("��7Ԫ��: fps, time, pos, biome, localtime"), false);
        mc.player.displayClientMessage(Component.literal("��7����: text (�ı�), value (��ֵ)"), false);
        mc.player.displayClientMessage(Component.literal("��7��ɫ: ��λʮ��������ɫ����"), false);
        mc.player.displayClientMessage(Component.literal("��7ʾ��: .hudcolor fps value FF0000"), false);
    }

    private boolean isValidElement(String element) {
        return textColors.containsKey(element);
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

    public void render(GuiGraphics graphics, float partialTicks) {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        int y = 5;
        int lineHeight = 10;

        Player player = mc.player;
        BlockPos pos = player.blockPosition();
        String biome = mc.level.getBiome(pos).unwrapKey()
                .map(key -> key.location().getPath())
                .orElse("unknown");

        // FPS
        renderSplitLine(graphics, "FPS: ", String.valueOf(mc.getFps()), 
            "fps", 5, y);
        y += lineHeight;

        // ��Ϸʱ��
        renderSplitLine(graphics, "����: ", String.valueOf(mc.level.getDayTime() / 24000L), 
            "time", 5, y);
        renderSplitLine(graphics, " ʱ��: ", formatGameTime(mc.level.getDayTime() % 24000L), 
            "time", 5 + mc.font.width("����: " + mc.level.getDayTime() / 24000L), y);
        y += lineHeight;

        // ����
        renderSplitLine(graphics, "XYZ: ", 
            String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ()),
            "pos", 5, y);
        y += lineHeight;

        // ����Ⱥϵ
        renderSplitLine(graphics, "����Ⱥϵ: ", formatBiomeName(biome),
            "biome", 5, y);
        y += lineHeight;

        // ����ʱ��
        renderSplitLine(graphics, "����ʱ��: ", TIME_FORMAT.format(new Date()),
            "localtime", 5, y);
    }

    private void renderSplitLine(GuiGraphics graphics, String label, String value, 
                               String element, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        // ��Ⱦ�ı���ǩ
        renderText(graphics, label, x, y, textColors.get(element));
        // ��Ⱦ��ֵ
        renderText(graphics, value, x + mc.font.width(label), y, valueColors.get(element));
    }

    private void renderText(GuiGraphics graphics, String text, int x, int y, int color) {
        // ��Ⱦ��Ӱ
        graphics.drawString(Minecraft.getInstance().font, text, x + 1, y + 1, SHADOW_COLOR);
        // ��Ⱦ�ı�
        graphics.drawString(Minecraft.getInstance().font, text, x, y, color);
    }

    private String formatGameTime(long time) {
        int hours = (int) ((time / 1000 + 6) % 24);
        int minutes = (int) ((time % 1000) * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    private String formatBiomeName(String biomeName) {
        // ������Ⱥϵ����ת��Ϊ���Ѻõ���ʾ��ʽ
        return biomeName.replace('_', ' ')
                .replace("plains", "ƽԭ")
                .replace("forest", "ɭ��")
                .replace("desert", "ɳĮ")
                .replace("ocean", "����")
                .replace("mountain", "ɽ��")
                .replace("river", "����")
                .replace("beach", "��̲")
                .replace("jungle", "����")
                .replace("savanna", "�ȴ���ԭ")
                .replace("taiga", "��Ҷ��")
                .replace("snowy", "ѩ��")
                .replace("frozen", "����")
                .replace("deep", "��")
                .replace("cold", "����")
                .replace("warm", "��ů")
                .replace("lukewarm", "�º�");
    }

    public int getTextColor(String element) {
        return textColors.getOrDefault(element, 0xFFFFFF);
    }

    public void setTextColor(String element, int color) {
        textColors.put(element, color);
    }

    public int getValueColor(String element) {
        return valueColors.getOrDefault(element, 0xFFFFFF);
    }

    public void setValueColor(String element, int color) {
        valueColors.put(element, color);
    }
} 
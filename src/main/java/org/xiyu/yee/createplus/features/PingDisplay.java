package org.xiyu.yee.createplus.features;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;

public class PingDisplay extends CreativePlusFeature {
    private int backgroundColor = 0x80000000; // Ĭ�ϰ�͸����ɫ����
    private float backgroundOpacity = 0.5f;   // Ĭ�ϱ���͸����
    
    public PingDisplay() {
        super("�ӳ���ʾ", "�������������ʾ�ӳ�");
    }

    @Override
    public void onEnable() {
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        NeoForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        if (!isEnabled() || !(event.getEntity() instanceof Player player)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;

        // ��ȡ����ӳ�
        int ping = mc.getConnection().getPlayerInfo(player.getUUID()) != null ? 
                  mc.getConnection().getPlayerInfo(player.getUUID()).getLatency() : 0;

        // �����ӳ�ֵѡ����ɫ
        String pingColor = getPingColor(ping);
        
        // ��ȡԭʼ�����
        String originalName = event.getContent().getString();
        
        // �����µ���ʾ�ı�
        Component newContent = Component.literal(originalName)
            .append(Component.literal(" ��8[��9Ping: " + pingColor + ping + "��8]"));

        // �����µ���ʾ����
        event.setContent(newContent);

        // ������Ҫ�ֶ���Ⱦ����Minecraft������Ⱦ
        // �Ƴ����Զ�����Ⱦ���룬��Ϊ���������ظ���Ⱦ
    }

    private String getPingColor(int ping) {
        if (ping < 50) return "��a"; // ��ɫ
        if (ping < 100) return "��e"; // ��ɫ
        if (ping < 200) return "��6"; // ��ɫ
        return "��c"; // ��ɫ
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n��7�������������ʾ�����ӳ�");
            desc.append("\n��7�ӳ���ɫ˵��:");
            desc.append("\n��a��ɫ ��7- ���� (<50ms)");
            desc.append("\n��e��ɫ ��7- ���� (<100ms)");
            desc.append("\n��6��ɫ ��7- һ�� (<200ms)");
            desc.append("\n��c��ɫ ��7- �ϲ� (>200ms)");
        }
        return desc.toString();
    }

    @Override
    public void onTick() {
        // ����Ҫtick����
    }

    // Getter �� Setter ����
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getBackgroundOpacity() {
        return backgroundOpacity;
    }

    public void setBackgroundOpacity(float backgroundOpacity) {
        this.backgroundOpacity = backgroundOpacity;
    }
} 
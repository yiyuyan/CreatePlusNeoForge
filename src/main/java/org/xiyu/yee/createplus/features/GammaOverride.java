package org.xiyu.yee.createplus.features;

import cn.ksmcbrigade.el.events.misc.GetOptionValueEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

public class GammaOverride extends CreativePlusFeature {
    private double originalGamma;
    private OptionInstance<Double> gammaOption;
    private static final double NIGHT_VISION_GAMMA = 240.0D;
    private boolean isFirstTick = true;
    private MobEffectInstance nightVisionEffect;
    private boolean wasEnabled = false;
    private double nightVisionGamma = 240.0D;
    private double gamma = 15d;

    public GammaOverride() {
        super("ҹ��", "�ṩ���õ�ҹ��Ч��");
        // ����һ������ʱ��ܳ���������Ч����ҹ��Ч��
        nightVisionEffect = new MobEffectInstance(
            MobEffects.NIGHT_VISION,
            Integer.MAX_VALUE,
            0,
            false,  // �Ƿ���ʾ����
            false,  // �Ƿ���ʾͼ��
            false    // �Ƿ���ʾ�ڱ���
        );
    }

    @Override
    public void onEnable() {
        NeoForge.EVENT_BUS.register(this);
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // ����ԭʼ����ֵ
            originalGamma = mc.options.gamma().get();
            // �����������
            setGamma(NIGHT_VISION_GAMMA);
            // ���ҹ��Ч��
            mc.player.addEffect(new MobEffectInstance(nightVisionEffect));
            isFirstTick = true;
            wasEnabled = true;
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            // �ָ�ԭʼ����ֵ
            setGamma(originalGamma);
        }
        if (mc.player != null && wasEnabled) {
            // �Ƴ�ҹ��Ч��
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
            wasEnabled = false;
        }
    }

    private void setGamma(double value) {
        gamma = value;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder(super.getDescription());
        if (isEnabled()) {
            desc.append("\n��7��ǰ����: ��e").append(String.format("%.1f", NIGHT_VISION_GAMMA));
            desc.append("\n��7ԭʼ����: ��7").append(String.format("%.1f", originalGamma));
            if (Minecraft.getInstance().player != null && 
                Minecraft.getInstance().player.hasEffect(MobEffects.NIGHT_VISION)) {
                desc.append("\n��aҹ��Ч��: ��a�Ѽ���");
            }
        }
        return desc.toString();
    }

    @SubscribeEvent
    public void applyGamma(GetOptionValueEvent event){
        if(event.cap.equals(Minecraft.getInstance().options.gamma().toString())){
            event.value = gamma;
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // ȷ�����ȱ��������ֵ
            double currentGamma = mc.options.gamma().get();
            if (currentGamma != NIGHT_VISION_GAMMA || isFirstTick) {
                setGamma(NIGHT_VISION_GAMMA);
                isFirstTick = false;
            }
            
            // ȷ��ҹ��Ч����������
            if (!mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
                mc.player.addEffect(new MobEffectInstance(nightVisionEffect));
            }
        }
    }

    // Getter �� Setter ����
    public double getOriginalGamma() {
        return originalGamma;
    }

    public void setOriginalGamma(double originalGamma) {
        this.originalGamma = originalGamma;
    }

    public double getNightVisionGamma() {
        return nightVisionGamma;
    }

    public void setNightVisionGamma(double nightVisionGamma) {
        this.nightVisionGamma = nightVisionGamma;
    }
} 
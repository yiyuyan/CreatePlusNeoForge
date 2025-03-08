package org.xiyu.yee.createplus.mixin.render;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.Performance;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addPerformanceButton(CallbackInfo ci) {
        Performance performance = (Performance) Createplus.FEATURE_MANAGER.getFeature("�����Ż�");
        if (performance != null && performance.isEnabled()) {
            // ��ȡ��ť��λ�� - �������ֺ�������ť���ұ�
            int buttonY = this.height / 6 + 24 - 6;
            
            // ��������Ż���ť
            Button perfButton = performance.createSettingsButton((Screen)(Object)this);
            perfButton.setPosition(this.width / 2 + 5, buttonY);
            perfButton.setWidth(150); // ����Ϊ���ť
            this.addRenderableWidget(perfButton);
        }
    }
} 
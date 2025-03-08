package org.xiyu.yee.createplus.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.ui.FeatureScreen;
import org.xiyu.yee.createplus.utils.KeyBindings;

@EventBusSubscriber(modid = Createplus.MODID, value = Dist.CLIENT)
public class KeyHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null && KeyBindings.TOGGLE_HUD.consumeClick()) {
            FeatureScreen.toggleVisibility();
        }
    }
} 
package org.xiyu.yee.createplus.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.ui.FeatureScreen;
import org.xiyu.yee.createplus.utils.KeyBindings;

@Mod.EventBusSubscriber(modid = Createplus.MODID, value = Dist.CLIENT)
public class KeyHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null && KeyBindings.TOGGLE_HUD.consumeClick()) {
            FeatureScreen.toggleVisibility();
        }
    }
} 
package org.xiyu.yee.createplus.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.xiyu.yee.createplus.Createplus;

public class KeyBindings {
    public static final KeyMapping TOGGLE_HUD = new KeyMapping(
        "key." + Createplus.MODID + ".toggle_hud", // 翻译键
        KeyConflictContext.IN_GAME,  // 只在游戏中生效
        InputConstants.Type.KEYSYM,   // 键盘按键
        InputConstants.KEY_F9,        // 默认F9
        "key.categories." + Createplus.MODID  // 按键分类
    );
} 
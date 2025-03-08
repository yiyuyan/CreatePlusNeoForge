package org.xiyu.yee.createplus.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.xiyu.yee.createplus.Createplus;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping TOGGLE_HUD = new KeyMapping(
        "key." + Createplus.MODID + ".toggle_hud", // �����
        KeyConflictContext.IN_GAME,  // ֻ����Ϸ����Ч
        InputConstants.Type.KEYSYM,   // ���̰���
        InputConstants.KEY_F9,        // Ĭ��F9
        "key.categories." + Createplus.MODID  // ��������
    );

    // ����µİ�����
    public static final KeyMapping TOGGLE_FREECAM = new KeyMapping(
            "key.createplus.toggle_freecam",
            GLFW.GLFW_KEY_F6,
            "key.categories.createplus"
    );

    public static final KeyMapping TOGGLE_ZOOM = new KeyMapping(
            "key.createplus.toggle_zoom",
            GLFW.GLFW_KEY_C,
            "key.categories.createplus"
    );

    public static final KeyMapping MODIFIER = new KeyMapping(
        "key.createplus.modifier",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_ALT,
        "key.categories.createplus"
    );
} 
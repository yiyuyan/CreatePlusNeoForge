package org.xiyu.yee.createplus.features;

import net.minecraft.client.gui.GuiGraphics;

/**
 * ������HUD�Ĺ��ܽӿ�
 */
public interface SubHUDFeature {
    /**
     * �����������
     * @param keyCode ��������
     * @return �Ƿ����˸ð���
     */
    boolean handleKeyPress(int keyCode);

    /**
     * �����HUD�Ƿ�ɼ�
     */
    boolean isSubHUDVisible();

    /**
     * �л���HUD����ʾ״̬
     */
    void toggleSubHUD();

    /**
     * ��Ⱦ��HUD
     * @param graphics GuiGraphicsʵ��
     * @param x ��Ⱦλ��X
     * @param y ��Ⱦλ��Y
     */
    void renderSubHUD(GuiGraphics graphics, int x, int y);
}
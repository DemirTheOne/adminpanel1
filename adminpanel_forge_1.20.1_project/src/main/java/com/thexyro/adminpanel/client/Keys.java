package com.thexyro.adminpanel.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Keys {
    public static final String CATEGORY = "key.categories.adminpanel";

    /** Paneli acar. Varsayilan: sag Shift. Panelin icinden de degistirilebilir. */
    public static final KeyMapping OPEN = new KeyMapping(
            "key.adminpanel.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY);

    /** SpinBot'u kendin icin acip kapatir. Varsayilan: atanmamis (panelden sec). */
    public static final KeyMapping TOGGLE = new KeyMapping(
            "key.adminpanel.toggle_spin", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY);
}

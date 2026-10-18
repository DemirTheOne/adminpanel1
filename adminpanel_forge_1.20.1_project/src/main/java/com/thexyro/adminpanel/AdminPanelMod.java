package com.thexyro.adminpanel;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(AdminPanelMod.MODID)
public class AdminPanelMod {
    public static final String MODID = "adminpanel";
    /** Varsayilan olarak sadece bu nick yetkili. */
    public static final String OWNER_NAME = "TheXyro0";

    public AdminPanelMod() {
        // Modu sadece host kursa bile modsuz arkadaslar sunucuya girebilsin.
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                        (remote, isServer) -> true));
        Net.register();
    }
}

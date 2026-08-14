package gg.moonflower.etched.core;

import gg.moonflower.etched.client.screen.AlbumCoverScreen;
import gg.moonflower.etched.client.screen.AlbumJukeboxScreen;
import gg.moonflower.etched.client.screen.BoomboxScreen;
import gg.moonflower.etched.client.screen.EtchingScreen;
import gg.moonflower.etched.client.screen.RadioScreen;
import gg.moonflower.etched.core.registry.EtchedMenus;
import gg.moonflower.etched.common.network.EtchedMessages;
import gg.moonflower.etched.client.render.JukeboxMinecartRenderer;
import gg.moonflower.etched.core.registry.EtchedEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public final class EtchedClient {
    private static boolean initialized;

    private EtchedClient() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        EtchedMessages.initClient();
        EtchedClientEvents.initialize();
        EntityRendererRegistry.register(EtchedEntities.JUKEBOX_MINECART.get(), JukeboxMinecartRenderer::new);

        MenuScreens.register(EtchedMenus.ETCHING_MENU.get(), EtchingScreen::new);
        MenuScreens.register(EtchedMenus.ALBUM_JUKEBOX_MENU.get(), AlbumJukeboxScreen::new);
        MenuScreens.register(EtchedMenus.BOOMBOX_MENU.get(), BoomboxScreen::new);
        MenuScreens.register(EtchedMenus.ALBUM_COVER_MENU.get(), AlbumCoverScreen::new);
        MenuScreens.register(EtchedMenus.RADIO_MENU.get(), RadioScreen::new);
    }
}

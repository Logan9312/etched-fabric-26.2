package gg.moonflower.etched.core;

import gg.moonflower.etched.client.screen.AlbumCoverScreen;
import gg.moonflower.etched.client.screen.AlbumJukeboxScreen;
import gg.moonflower.etched.client.screen.BoomboxScreen;
import gg.moonflower.etched.client.screen.EtchingScreen;
import gg.moonflower.etched.client.screen.RadioScreen;
import gg.moonflower.etched.core.registry.EtchedMenus;
import gg.moonflower.etched.common.network.EtchedMessages;
import gg.moonflower.etched.client.render.JukeboxMinecartRenderer;
import gg.moonflower.etched.client.render.item.DiscAppearanceTint;
import gg.moonflower.etched.client.render.item.DiscPatternProperty;
import gg.moonflower.etched.client.render.item.MusicLabelTint;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.client.render.item.AlbumCoverSpecialRenderer;
import gg.moonflower.etched.core.registry.EtchedEntities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class EtchedClient {
    private static boolean initialized;

    private EtchedClient() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        ItemTintSources.ID_MAPPER.put(Etched.etchedPath("disc_appearance"), DiscAppearanceTint.MAP_CODEC);
        ItemTintSources.ID_MAPPER.put(Etched.etchedPath("music_label"), MusicLabelTint.MAP_CODEC);
        SelectItemModelProperties.ID_MAPPER.put(Etched.etchedPath("disc_pattern"), DiscPatternProperty.TYPE);
        SpecialModelRenderers.ID_MAPPER.put(AlbumCoverSpecialRenderer.TYPE_ID, AlbumCoverSpecialRenderer.Unbaked.MAP_CODEC);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            AlbumCoverSpecialRenderer.INSTANCE.close();
            Etched.CLIENT_SERVER_CONFIG.copyFrom(Etched.SERVER_CONFIG);
        });
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Etched.etchedPath("album_cover_renderer");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                AlbumCoverSpecialRenderer.INSTANCE.close();
                AlbumCoverItemRenderer.clearOverlay();
            }
        });

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

package gg.moonflower.etched.client.render.item;

import com.mojang.blaze3d.platform.NativeImage;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * Album-cover resource support shared by the downloader and the 26.2 item-model path.
 * The legacy immediate-mode renderer was removed in 26.2; custom cover submission is
 * handled separately while this class owns the overlay resource.
 */
public final class AlbumCoverItemRenderer {
    public static final String FOLDER_NAME = "item/" + Etched.MOD_ID + "_album_cover";
    public static final Identifier BLANK_ALBUM_COVER = Etched.etchedPath(FOLDER_NAME + "/blank");
    public static final Identifier DEFAULT_ALBUM_COVER = Etched.etchedPath(FOLDER_NAME + "/default");
    private static final Identifier ALBUM_COVER_OVERLAY = Etched.etchedPath("textures/item/album_cover_overlay.png");

    private static NativeImage overlay;

    private AlbumCoverItemRenderer() {
    }

    public static synchronized NativeImage getOverlayImage() {
        if (overlay == null) {
            overlay = loadOverlay();
        }
        return overlay;
    }

    private static NativeImage loadOverlay() {
        try (InputStream stream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(ALBUM_COVER_OVERLAY).open()) {
            return NativeImage.read(stream);
        } catch (IOException exception) {
            Etched.LOGGER.error("Failed to load album cover overlay", exception);
            NativeImage missing = new NativeImage(16, 16, false);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    missing.setPixel(x, y, (x < 8 ^ y < 8) ? 0xFFF800F8 : 0xFF000000);
                }
            }
            return missing;
        }
    }
}

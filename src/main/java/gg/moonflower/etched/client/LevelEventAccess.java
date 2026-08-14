package gg.moonflower.etched.client;

import gg.moonflower.etched.core.mixin.client.render.ClientLevelAccessor;
import gg.moonflower.etched.core.mixin.client.render.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;

import java.util.Map;

public final class LevelEventAccess {
    private LevelEventAccess() {
    }

    public static Map<BlockPos, SoundInstance> playingJukeboxSongs(Minecraft client) {
        return ((LevelRendererAccessor) ((ClientLevelAccessor) client.level).etched$getLevelEventHandler()).getPlayingJukeboxSongs();
    }
}

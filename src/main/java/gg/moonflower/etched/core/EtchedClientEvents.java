package gg.moonflower.etched.core;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public final class EtchedClientEvents {
    private EtchedClientEvents() {
    }

    public static void initialize() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            var album = stack.get(EtchedComponents.ALBUM_COVER.get());
            if (album != null) album.addToTooltip(context, lines::add, flag, stack);
            var label = stack.get(EtchedComponents.MUSIC_LABEL.get());
            if (label != null) label.addToTooltip(context, lines::add, flag, stack);
            var playing = stack.get(EtchedComponents.PLAYING_RECORD.get());
            if (playing != null) playing.addToTooltip(context, lines::add, flag, stack);
            PlayableRecord.addToTooltip(stack, context, lines::add);
            var paused = stack.get(EtchedComponents.PAUSED.get());
            if (paused != null) paused.addToTooltip(context, lines::add, flag, stack);
        });
    }
}

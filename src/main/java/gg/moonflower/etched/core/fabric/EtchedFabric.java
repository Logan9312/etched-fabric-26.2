package gg.moonflower.etched.core.fabric;

import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedItems;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.CreativeModeTabs;

public final class EtchedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Etched.initialize();
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(EtchedItems.MUSIC_LABEL.get());
            output.accept(EtchedItems.BLANK_MUSIC_DISC.get());
            output.accept(EtchedItems.BOOMBOX.get());
            output.accept(EtchedItems.ALBUM_COVER.get());
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(output -> output.accept(EtchedItems.JUKEBOX_MINECART.get()));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
            output.accept(EtchedBlocks.ETCHING_TABLE.get());
            output.accept(EtchedBlocks.ALBUM_JUKEBOX.get());
            output.accept(EtchedBlocks.RADIO.get());
        });
    }
}

package net.minecraft.core.cauldron;

import net.minecraft.world.item.Item;

public final class EtchedCauldronAccess {
    private EtchedCauldronAccess() {
    }

    public static void put(CauldronInteraction.Dispatcher dispatcher, Item item, CauldronInteraction interaction) {
        dispatcher.put(item, interaction);
    }
}

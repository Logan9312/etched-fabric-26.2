package gg.moonflower.etched.core;

import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedVillagers;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class EtchedEvents {
    private EtchedEvents() {
    }

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(EtchedVillagers::addVillageHouses);
        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register((original, replacement, origin, destination) -> {
            if (replacement instanceof ItemEntity itemEntity && destination.dimension() == Level.NETHER) {
                ItemStack oldStack = itemEntity.getItem();
                if (oldStack.is(EtchedBlocks.RADIO.get().asItem())) {
                    ItemStack newStack = new ItemStack(EtchedBlocks.PORTAL_RADIO_ITEM.get(), oldStack.getCount());
                    newStack.applyComponents(oldStack.getComponents());
                    itemEntity.setItem(newStack);
                }
            }
        });
    }
}

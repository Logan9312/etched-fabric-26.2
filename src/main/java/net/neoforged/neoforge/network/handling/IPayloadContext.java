package net.neoforged.neoforge.network.handling;

import net.minecraft.world.entity.player.Player;

/** Temporary source-compatibility bridge for Etched's packet handlers. */
@FunctionalInterface
public interface IPayloadContext {
    Player player();
}

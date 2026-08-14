package net.neoforged.neoforge.registries;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> implements ItemLike {
    DeferredBlock(Identifier id, Supplier<? extends T> factory) {
        super(id, factory);
    }

    @Override
    public Item asItem() {
        return this.get().asItem();
    }
}

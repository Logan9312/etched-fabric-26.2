package net.neoforged.neoforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Fabric-backed transition layer for the handful of DeferredRegister operations
 * used by Etched. It deliberately supports only the API surface present here.
 */
public class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String namespace;
    private final List<DeferredHolder<T, ? extends T>> entries = new ArrayList<>();
    private boolean initialized;

    protected DeferredRegister(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
        return new DeferredRegister<>(registry, namespace);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValueOrThrow((ResourceKey) registryKey);
        return new DeferredRegister<>(registry, namespace);
    }

    public static Blocks createBlocks(String namespace) {
        return new Blocks(namespace);
    }

    public static Items createItems(String namespace) {
        return new Items(namespace);
    }

    public static DataComponents createDataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> ignored, String namespace) {
        return new DataComponents(namespace);
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> factory) {
        DeferredHolder<T, I> handle = new DeferredHolder<>(id(name), factory);
        this.entries.add(handle);
        return handle;
    }

    protected Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(this.namespace, name);
    }

    protected <I extends T, H extends DeferredHolder<T, I>> H add(H handle) {
        this.entries.add(handle);
        return handle;
    }

    public final void registerAll() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        for (DeferredHolder<T, ? extends T> raw : this.entries) {
            register(raw);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void register(DeferredHolder raw) {
        Object value = raw.factory().get();
        raw.bind(Registry.register((Registry) this.registry, raw.getId(), value));
    }

    public static final class Blocks extends DeferredRegister<Block> {
        private Blocks(String namespace) {
            super(BuiltInRegistries.BLOCK, namespace);
        }

        @Override
        public <I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> factory) {
            return add(new DeferredBlock<>(id(name), factory));
        }
    }

    public static final class Items extends DeferredRegister<Item> {
        private Items(String namespace) {
            super(BuiltInRegistries.ITEM, namespace);
        }

        @Override
        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> factory) {
            return add(new DeferredItem<>(id(name), factory));
        }
    }

    public static final class DataComponents extends DeferredRegister<DataComponentType<?>> {
        private DataComponents(String namespace) {
            super(BuiltInRegistries.DATA_COMPONENT_TYPE, namespace);
        }
    }
}

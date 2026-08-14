package net.neoforged.neoforge.registries;

import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Small source-compatibility handle used while moving Etched from NeoForge's
 * deferred registries to Fabric's vanilla registries.
 */
public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final Identifier id;
    private final Supplier<? extends T> factory;
    private T value;

    DeferredHolder(Identifier id, Supplier<? extends T> factory) {
        this.id = id;
        this.factory = factory;
    }

    void bind(T value) {
        if (this.value != null) {
            throw new IllegalStateException("Registry handle already bound: " + this.id);
        }
        this.value = Objects.requireNonNull(value, "Registry factory returned null for " + this.id);
    }

    Supplier<? extends T> factory() {
        return this.factory;
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public T get() {
        if (this.value == null) {
            throw new IllegalStateException("Registry value requested before initialization: " + this.id);
        }
        return this.value;
    }
}

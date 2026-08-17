package gg.moonflower.etched.client.render.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record DiscAppearanceTint(int layer) implements ItemTintSource {
    public static final com.mojang.serialization.MapCodec<DiscAppearanceTint> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("layer").forGetter(DiscAppearanceTint::layer)
    ).apply(instance, DiscAppearanceTint::new));

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        DiscAppearanceComponent appearance = stack.getOrDefault(EtchedComponents.DISC_APPEARANCE.get(), DiscAppearanceComponent.DEFAULT);
        return switch (this.layer) {
            case 0 -> appearance.discColor();
            case 1 -> appearance.pattern().isColorable() ? appearance.labelPrimaryColor() : 0xFFFFFFFF;
            case 2 -> appearance.pattern().isColorable() ? appearance.labelSecondaryColor() : 0xFFFFFFFF;
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    public com.mojang.serialization.MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}

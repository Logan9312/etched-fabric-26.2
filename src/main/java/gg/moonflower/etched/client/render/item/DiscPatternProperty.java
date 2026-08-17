package gg.moonflower.etched.client.render.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record DiscPatternProperty() implements SelectItemModelProperty<DiscAppearanceComponent.LabelPattern> {
    public static final Codec<DiscAppearanceComponent.LabelPattern> VALUE_CODEC = DiscAppearanceComponent.LabelPattern.CODEC;
    public static final Type<DiscPatternProperty, DiscAppearanceComponent.LabelPattern> TYPE =
            Type.create(MapCodec.unit(new DiscPatternProperty()), VALUE_CODEC);

    @Override
    public DiscAppearanceComponent.LabelPattern get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed,
                                                     ItemDisplayContext displayContext) {
        return stack.getOrDefault(EtchedComponents.DISC_APPEARANCE.get(), DiscAppearanceComponent.DEFAULT).pattern();
    }

    @Override
    public Codec<DiscAppearanceComponent.LabelPattern> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    public Type<DiscPatternProperty, DiscAppearanceComponent.LabelPattern> type() {
        return TYPE;
    }
}

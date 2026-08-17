package gg.moonflower.etched.client.render.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record MusicLabelTint(int layer) implements ItemTintSource {
    public static final com.mojang.serialization.MapCodec<MusicLabelTint> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("layer").forGetter(MusicLabelTint::layer)
    ).apply(instance, MusicLabelTint::new));

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        MusicLabelComponent label = stack.getOrDefault(EtchedComponents.MUSIC_LABEL.get(), MusicLabelComponent.DEFAULT);
        return this.layer == 0 ? label.primaryColor() : label.secondaryColor();
    }

    @Override
    public com.mojang.serialization.MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}

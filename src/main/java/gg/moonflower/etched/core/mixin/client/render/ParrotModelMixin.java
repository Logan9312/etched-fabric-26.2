package gg.moonflower.etched.core.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Preserves Etched's option to use tick-stepped parrot party animation. */
@Mixin(ParrotModel.class)
public class ParrotModelMixin {
    @ModifyExpressionValue(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ParrotRenderState;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/ParrotRenderState;ageInTicks:F")
    )
    private float etched$configurePartyAnimationSmoothing(float ageInTicks) {
        return Etched.CLIENT_CONFIG.smoothParrotAnimation.get() ? ageInTicks : (float) Math.floor(ageInTicks);
    }
}

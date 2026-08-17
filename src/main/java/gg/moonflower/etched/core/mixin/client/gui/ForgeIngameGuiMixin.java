package gg.moonflower.etched.core.mixin.client.gui;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Hud.class)
public class ForgeIngameGuiMixin {

    @ModifyArg(method = "extractOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;hsvToArgb(FFFI)I"), index = 0)
    public float modifyHue(float hue) {
        return ((hue * 50.0F) % 50.0F) / 50.0F;
    }
}

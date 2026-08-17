package gg.moonflower.etched.core.mixin;

import gg.moonflower.etched.common.component.AlbumCoverComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    private void etched$removeAlbumCover(ItemStack top, ItemStack bottom, CallbackInfoReturnable<ItemStack> callback) {
        if (top.isEmpty() == bottom.isEmpty()) {
            return;
        }

        ItemStack input = top.isEmpty() ? bottom : top;
        AlbumCoverComponent albumCover = input.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover == null || albumCover.getCoverStack().isEmpty()) {
            return;
        }

        ItemStack result = input.copyWithCount(1);
        result.set(EtchedComponents.ALBUM_COVER.get(), albumCover.toBuilder().setCoverStack(ItemStack.EMPTY).build());
        callback.setReturnValue(result);
    }
}

package gg.moonflower.etched.core.mixin.client.gui;

import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface GuiAccessor {

    @Accessor
    Component getOverlayMessageString();

    @Accessor
    void setOverlayMessageTime(int overlayMessageTime);
}

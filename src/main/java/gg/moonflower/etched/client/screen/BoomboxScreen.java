package gg.moonflower.etched.client.screen;

import net.minecraft.client.renderer.RenderPipelines;

import gg.moonflower.etched.common.menu.BoomboxMenu;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author Ocelot
 */
public class BoomboxScreen extends AbstractContainerScreen<BoomboxMenu> {

    private static final Identifier BOOMBOX_LOCATION = Etched.etchedPath("textures/gui/container/boombox.png");

    public BoomboxScreen(BoomboxMenu hopperMenu, Inventory inventory, Component component) {
        super(hopperMenu, inventory, component, 176, 133);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BOOMBOX_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}

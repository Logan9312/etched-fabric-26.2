package gg.moonflower.etched.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

public class JukeboxMinecartRenderer extends MinecartRenderer {

    public JukeboxMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.MINECART);
    }
}

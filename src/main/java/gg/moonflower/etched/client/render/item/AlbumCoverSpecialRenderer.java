package gg.moonflower.etched.client.render.item;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import gg.moonflower.etched.api.record.AlbumCover;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.common.component.AlbumCoverComponent;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** Renders downloaded album artwork through Minecraft 26.2's special-item pipeline. */
public final class AlbumCoverSpecialRenderer implements SpecialModelRenderer<AlbumCoverSpecialRenderer.CoverArgument> {
    public static final AlbumCoverSpecialRenderer INSTANCE = new AlbumCoverSpecialRenderer();
    public static final Identifier TYPE_ID = Etched.etchedPath("album_cover");
    private static final Identifier DEFAULT_TEXTURE = Etched.etchedPath("textures/item/default_album_cover.png");
    private static final Identifier VANILLA_TEXTURE = Etched.etchedPath("textures/item/vanilla_album_cover.png");

    private final Map<Integer, CompletableFuture<AlbumCover>> covers = new ConcurrentHashMap<>();
    private final Map<Integer, Identifier> dynamicTextures = new HashMap<>();

    private AlbumCoverSpecialRenderer() {
    }

    @Override
    public CoverArgument extractArgument(ItemStack stack) {
        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover == null || albumCover.getCoverStack().isEmpty()) {
            return CoverArgument.DEFAULT;
        }

        ItemStack coverStack = albumCover.getCoverStack();
        int key = ItemStack.hashItemAndComponents(coverStack);
        CompletableFuture<AlbumCover> future = this.covers.computeIfAbsent(key, ignored -> PlayableRecord.getAlbumCover(
                coverStack.copy(),
                Minecraft.getInstance().getProxy(),
                Minecraft.getInstance().getResourceManager()
        ).exceptionally(throwable -> {
            Etched.LOGGER.error("Error retrieving album cover", throwable);
            return AlbumCover.EMPTY;
        }));
        return new CoverArgument(key, future);
    }

    @Override
    public void submit(CoverArgument argument, PoseStack poseStack, SubmitNodeCollector collector, int packedLight,
                       int packedOverlay, boolean foil, int outlineColor) {
        Identifier texture = this.resolveTexture(argument);
        collector.submitCustomGeometry(poseStack, RenderTypes.itemCutout(texture),
                (pose, vertices) -> renderSquare(pose, vertices, packedLight, packedOverlay));
        if (foil) {
            collector.order(1).submitCustomGeometry(poseStack, RenderTypes.glint(),
                    (pose, vertices) -> renderSquare(pose, vertices, packedLight, packedOverlay));
        }
    }

    private Identifier resolveTexture(CoverArgument argument) {
        AlbumCover cover = argument.future == null ? AlbumCover.EMPTY : argument.future.getNow(AlbumCover.EMPTY);
        if (cover instanceof AlbumCover.ImageAlbumCover imageCover) {
            return this.dynamicTextures.computeIfAbsent(argument.key, key -> {
                Identifier texture = Etched.etchedPath("album_cover/" + Integer.toUnsignedString(key));
                DynamicTexture dynamicTexture = new DynamicTexture(() -> "Etched album cover " + key, imageCover.image());
                Minecraft.getInstance().getTextureManager().register(texture, dynamicTexture);
                return texture;
            });
        }
        if (cover instanceof AlbumCover.ModelAlbumCover) {
            return VANILLA_TEXTURE;
        }
        return DEFAULT_TEXTURE;
    }

    private static void renderSquare(PoseStack.Pose pose, VertexConsumer vertices, int light, int overlay) {
        vertex(vertices, pose, -0.5F, -0.5F, 0.03125F, 0.0F, 1.0F, light, overlay, 1.0F);
        vertex(vertices, pose, 0.5F, -0.5F, 0.03125F, 1.0F, 1.0F, light, overlay, 1.0F);
        vertex(vertices, pose, 0.5F, 0.5F, 0.03125F, 1.0F, 0.0F, light, overlay, 1.0F);
        vertex(vertices, pose, -0.5F, 0.5F, 0.03125F, 0.0F, 0.0F, light, overlay, 1.0F);

        vertex(vertices, pose, 0.5F, -0.5F, -0.03125F, 0.0F, 1.0F, light, overlay, -1.0F);
        vertex(vertices, pose, -0.5F, -0.5F, -0.03125F, 1.0F, 1.0F, light, overlay, -1.0F);
        vertex(vertices, pose, -0.5F, 0.5F, -0.03125F, 1.0F, 0.0F, light, overlay, -1.0F);
        vertex(vertices, pose, 0.5F, 0.5F, -0.03125F, 0.0F, 0.0F, light, overlay, -1.0F);

        edgeVertex(vertices, pose, -0.5F, -0.5F, -0.03125F, 0.0F, 1.0F, light, overlay, -1.0F, 0.0F);
        edgeVertex(vertices, pose, -0.5F, -0.5F, 0.03125F, 1.0F, 1.0F, light, overlay, -1.0F, 0.0F);
        edgeVertex(vertices, pose, -0.5F, 0.5F, 0.03125F, 1.0F, 0.0F, light, overlay, -1.0F, 0.0F);
        edgeVertex(vertices, pose, -0.5F, 0.5F, -0.03125F, 0.0F, 0.0F, light, overlay, -1.0F, 0.0F);

        edgeVertex(vertices, pose, 0.5F, -0.5F, 0.03125F, 0.0F, 1.0F, light, overlay, 1.0F, 0.0F);
        edgeVertex(vertices, pose, 0.5F, -0.5F, -0.03125F, 1.0F, 1.0F, light, overlay, 1.0F, 0.0F);
        edgeVertex(vertices, pose, 0.5F, 0.5F, -0.03125F, 1.0F, 0.0F, light, overlay, 1.0F, 0.0F);
        edgeVertex(vertices, pose, 0.5F, 0.5F, 0.03125F, 0.0F, 0.0F, light, overlay, 1.0F, 0.0F);

        edgeVertex(vertices, pose, -0.5F, 0.5F, 0.03125F, 0.0F, 1.0F, light, overlay, 0.0F, 1.0F);
        edgeVertex(vertices, pose, 0.5F, 0.5F, 0.03125F, 1.0F, 1.0F, light, overlay, 0.0F, 1.0F);
        edgeVertex(vertices, pose, 0.5F, 0.5F, -0.03125F, 1.0F, 0.0F, light, overlay, 0.0F, 1.0F);
        edgeVertex(vertices, pose, -0.5F, 0.5F, -0.03125F, 0.0F, 0.0F, light, overlay, 0.0F, 1.0F);

        edgeVertex(vertices, pose, -0.5F, -0.5F, -0.03125F, 0.0F, 1.0F, light, overlay, 0.0F, -1.0F);
        edgeVertex(vertices, pose, 0.5F, -0.5F, -0.03125F, 1.0F, 1.0F, light, overlay, 0.0F, -1.0F);
        edgeVertex(vertices, pose, 0.5F, -0.5F, 0.03125F, 1.0F, 0.0F, light, overlay, 0.0F, -1.0F);
        edgeVertex(vertices, pose, -0.5F, -0.5F, 0.03125F, 0.0F, 0.0F, light, overlay, 0.0F, -1.0F);
    }

    private static void vertex(VertexConsumer vertices, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                               int light, int overlay, float normalZ) {
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, normalZ);
    }

    private static void edgeVertex(VertexConsumer vertices, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                   int light, int overlay, float normalX, float normalY) {
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, normalX, normalY, 0.0F);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.5F, -0.5F, -0.03125F));
        output.accept(new Vector3f(0.5F, 0.5F, 0.03125F));
    }

    public void close() {
        Map<Integer, Identifier> textures = Map.copyOf(this.dynamicTextures);
        this.dynamicTextures.clear();
        this.covers.forEach((key, future) -> {
            if (!textures.containsKey(key)) {
                future.thenAccept(cover -> {
                    if (cover instanceof AlbumCover.ImageAlbumCover imageCover) {
                        imageCover.image().close();
                    }
                });
            }
        });
        this.covers.clear();
        textures.values().forEach(Minecraft.getInstance().getTextureManager()::release);
    }

    public record CoverArgument(int key, CompletableFuture<AlbumCover> future) {
        private static final CoverArgument DEFAULT = new CoverArgument(0, null);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<CoverArgument> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<CoverArgument> bake(BakingContext context) {
            return INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CoverArgument>> type() {
            return MAP_CODEC;
        }
    }
}

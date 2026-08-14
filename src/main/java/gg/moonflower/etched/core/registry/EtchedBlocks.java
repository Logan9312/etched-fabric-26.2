package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.common.block.EtchingTableBlock;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.common.item.PortalRadioItem;
import gg.moonflower.etched.core.Etched;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class EtchedBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Etched.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Etched.MOD_ID);

    public static final DeferredBlock<Block> ETCHING_TABLE = registerWithItem("etching_table", () -> new EtchingTableBlock(blockProperties("etching_table").mapColor(MapColor.PODZOL).strength(2.5F).sound(SoundType.WOOD)), new Item.Properties());
    public static final DeferredBlock<Block> ALBUM_JUKEBOX = registerWithItem("album_jukebox", () -> new AlbumJukeboxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX).setId(blockKey("album_jukebox"))), new Item.Properties());
    public static final DeferredBlock<Block> RADIO = registerWithItem("radio", () -> new RadioBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX).setId(blockKey("radio")).noOcclusion()), new Item.Properties());
    public static final DeferredItem<Item> PORTAL_RADIO_ITEM = EtchedItems.REGISTRY.register("portal_radio", () -> new PortalRadioItem(RADIO.get(), new Item.Properties().setId(itemKey("portal_radio"))));

    public static final Supplier<BlockEntityType<AlbumJukeboxBlockEntity>> ALBUM_JUKEBOX_BE = BLOCK_ENTITIES.register("album_jukebox", () -> new BlockEntityType<>(AlbumJukeboxBlockEntity::new, java.util.Set.of(ALBUM_JUKEBOX.get())));
    public static final Supplier<BlockEntityType<RadioBlockEntity>> RADIO_BE = BLOCK_ENTITIES.register("radio", () -> new BlockEntityType<>(RadioBlockEntity::new, java.util.Set.of(RADIO.get())));

    private static ResourceKey<Block> blockKey(String id) {
        return ResourceKey.create(Registries.BLOCK, Etched.etchedPath(id));
    }

    private static ResourceKey<Item> itemKey(String id) {
        return ResourceKey.create(Registries.ITEM, Etched.etchedPath(id));
    }

    private static BlockBehaviour.Properties blockProperties(String id) {
        return BlockBehaviour.Properties.of().setId(blockKey(id));
    }

    /**
     * Registers a block with a simple item.
     *
     * @param id         The id of the block
     * @param block      The block to register
     * @param properties The properties of the item to register
     * @param <R>        The type of block being registered
     * @return The registered block
     */
    private static <R extends Block> DeferredBlock<R> registerWithItem(String id, Supplier<R> block, Item.Properties properties) {
        properties.setId(itemKey(id));
        return registerWithItem(id, block, object -> new BlockItem(object, properties));
    }

    /**
     * Registers a block with an item.
     *
     * @param id          The id of the block
     * @param block       The block to register
     * @param itemFactory The factory to create a new item from the registered block
     * @param <R>         The type of block being registered
     * @return The registered block
     */
    private static <R extends Block> DeferredBlock<R> registerWithItem(String id, Supplier<R> block, Function<R, Item> itemFactory) {
        DeferredBlock<R> register = BLOCKS.register(id, block);
        EtchedItems.REGISTRY.register(id, () -> itemFactory.apply(register.get()));
        return register;
    }
}

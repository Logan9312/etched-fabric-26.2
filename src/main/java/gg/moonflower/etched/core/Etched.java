package gg.moonflower.etched.core;

import com.mojang.logging.LogUtils;
import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.sound.download.BandcampSource;
import gg.moonflower.etched.common.sound.download.SoundCloudSource;
import gg.moonflower.etched.common.network.EtchedMessages;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedComponents;
import gg.moonflower.etched.core.registry.EtchedEntities;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedMenus;
import gg.moonflower.etched.core.registry.EtchedRecipes;
import gg.moonflower.etched.core.registry.EtchedSounds;
import gg.moonflower.etched.core.registry.EtchedVillagers;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.cauldron.EtchedCauldronAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import org.slf4j.Logger;


public final class Etched {
    public static final String MOD_ID = "etched";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean SOPHSTICATED_CORE_LOADED = FabricLoader.getInstance().isModLoaded("sophisticatedcore");
    public static final EtchedConfig.Client CLIENT_CONFIG = new EtchedConfig.Client();
    public static final EtchedConfig.Server SERVER_CONFIG = new EtchedConfig.Server();

    private static boolean initialized;

    private Etched() {
    }

    public static Identifier etchedPath(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        EtchedComponents.REGISTRY.registerAll();
        EtchedBlocks.BLOCKS.registerAll();
        EtchedItems.REGISTRY.registerAll();
        EtchedBlocks.BLOCK_ENTITIES.registerAll();
        EtchedEntities.REGISTRY.registerAll();
        EtchedMenus.REGISTRY.registerAll();
        EtchedRecipes.REGISTRY.registerAll();
        EtchedSounds.REGISTRY.registerAll();
        EtchedVillagers.POI_REGISTRY.registerAll();
        EtchedVillagers.PROFESSION_REGISTRY.registerAll();
        EtchedMessages.initCommon();
        EtchedEvents.initialize();

        SoundSourceManager.registerSource(new SoundCloudSource());
        SoundSourceManager.registerSource(new BandcampSource());
        registerCauldronInteractions();
    }

    private static void registerCauldronInteractions() {
        EtchedCauldronAccess.put(CauldronInteractions.WATER, EtchedItems.BLANK_MUSIC_DISC.get(), (state, level, pos, player, hand, stack) -> {
            if (!stack.has(DataComponents.DYED_COLOR)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            if (!level.isClientSide()) {
                stack.remove(DataComponents.DYED_COLOR);
                player.awardStat(Stats.CLEAN_ARMOR);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }
            return InteractionResult.SUCCESS;
        });
        EtchedCauldronAccess.put(CauldronInteractions.WATER, EtchedItems.MUSIC_LABEL.get(), (state, level, pos, player, hand, stack) -> {
            MusicLabelComponent label = stack.get(EtchedComponents.MUSIC_LABEL.get());
            if (label == null || !label.isColored()) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                ItemStack newStack = stack.copy();
                newStack.set(EtchedComponents.MUSIC_LABEL.get(), label.withColor(-1, -1));
                player.setItemInHand(hand, newStack);
                player.awardStat(Stats.CLEAN_ARMOR);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }

            return InteractionResult.SUCCESS;
        });
    }
}

package gg.moonflower.etched.core.registry;

import com.google.common.collect.ImmutableSet;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.mixin.StructureTemplatePoolAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class EtchedVillagers {
    public static final DeferredRegister<PoiType> POI_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Etched.MOD_ID);
    public static final DeferredRegister<VillagerProfession> PROFESSION_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, Etched.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> BARD_POI = POI_REGISTRY.register("bard", () ->
            new PoiType(ImmutableSet.<BlockState>builder()
                    .addAll(Blocks.NOTE_BLOCK.getStateDefinition().getPossibleStates())
                    .build(), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> BARD = PROFESSION_REGISTRY.register("bard", () ->
            new VillagerProfession(
                    Component.translatable("entity.minecraft.villager.etched.bard"),
                    poi -> poi.is(BARD_POI.getId()),
                    poi -> poi.is(BARD_POI.getId()),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    null,
                    Int2ObjectMap.ofEntries(
                            Int2ObjectMap.entry(1, tradeSet(1)),
                            Int2ObjectMap.entry(2, tradeSet(2)),
                            Int2ObjectMap.entry(3, tradeSet(3)),
                            Int2ObjectMap.entry(4, tradeSet(4)),
                            Int2ObjectMap.entry(5, tradeSet(5)))));

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSORS = ResourceKey.create(
            Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace("empty"));

    private EtchedVillagers() {
    }

    private static ResourceKey<net.minecraft.world.item.trading.TradeSet> tradeSet(int level) {
        return ResourceKey.create(Registries.TRADE_SET, Etched.etchedPath("bard/level_" + level));
    }

    public static void addVillageHouses(MinecraftServer server) {
        RegistryAccess.Frozen access = server.registryAccess();
        Registry<StructureTemplatePool> templatePools = access.lookupOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorLists = access.lookupOrThrow(Registries.PROCESSOR_LIST);

        addVillageHouse(templatePools, processorLists, "plains", 2, ProcessorLists.MOSSIFY_10_PERCENT, ProcessorLists.ZOMBIE_PLAINS);
        addVillageHouse(templatePools, processorLists, "desert", 2, EMPTY_PROCESSORS, ProcessorLists.ZOMBIE_DESERT);
        addVillageHouse(templatePools, processorLists, "savanna", 4, EMPTY_PROCESSORS, ProcessorLists.ZOMBIE_SAVANNA);
        addVillageHouse(templatePools, processorLists, "snowy", 4, EMPTY_PROCESSORS, ProcessorLists.ZOMBIE_SNOWY);
        addVillageHouse(templatePools, processorLists, "taiga", 4, ProcessorLists.MOSSIFY_10_PERCENT, ProcessorLists.ZOMBIE_TAIGA);
    }

    private static void addVillageHouse(Registry<StructureTemplatePool> templatePools,
                                        Registry<StructureProcessorList> processorLists,
                                        String village,
                                        int weight,
                                        ResourceKey<StructureProcessorList> normalProcessor,
                                        ResourceKey<StructureProcessorList> zombieProcessor) {
        Identifier poolId = Identifier.withDefaultNamespace("village/" + village + "/houses");
        Identifier zombiePoolId = Identifier.withDefaultNamespace("village/" + village + "/zombie/houses");
        Identifier pieceId = Etched.etchedPath("village/" + village + "/houses/" + village + "_bard_house_1");

        addToPool(templatePools.getValue(poolId), pieceId, processorLists.get(normalProcessor), weight);
        addToPool(templatePools.getValue(zombiePoolId), pieceId, processorLists.get(zombieProcessor), weight);
    }

    private static void addToPool(StructureTemplatePool pool,
                                  Identifier pieceId,
                                  Optional<Holder.Reference<StructureProcessorList>> processor,
                                  int weight) {
        if (pool == null || processor.isEmpty()) {
            Etched.LOGGER.warn("Unable to add bard house {} to a village pool", pieceId);
            return;
        }

        StructurePoolElement piece = StructurePoolElement.legacy(pieceId.toString(), processor.get())
                .apply(StructureTemplatePool.Projection.RIGID);
        for (int i = 0; i < weight; i++) {
            ((StructureTemplatePoolAccessor) pool).getTemplates().add(piece);
        }
    }
}

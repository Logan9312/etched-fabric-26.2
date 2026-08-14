package gg.moonflower.etched.core.registry;

import com.google.common.collect.ImmutableSet;
import gg.moonflower.etched.core.Etched;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
                    Component.translatable("entity.etched.villager.bard"),
                    poi -> poi.is(BARD_POI.getId()),
                    poi -> poi.is(BARD_POI.getId()),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    null,
                    Int2ObjectMap.ofEntries()));

    private EtchedVillagers() {
    }
}

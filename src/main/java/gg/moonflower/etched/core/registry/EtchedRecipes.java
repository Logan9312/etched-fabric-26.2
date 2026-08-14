package gg.moonflower.etched.core.registry;

import com.mojang.serialization.MapCodec;
import gg.moonflower.etched.common.recipe.MusicDiscCloningRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelDyeRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelMergeRecipe;
import gg.moonflower.etched.core.Etched;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EtchedRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Etched.MOD_ID);

    public static final Supplier<RecipeSerializer<MusicLabelMergeRecipe>> COMPLEX_MUSIC_LABEL = REGISTRY.register("merge_music_label", () -> unit(new MusicLabelMergeRecipe()));
    public static final Supplier<RecipeSerializer<MusicDiscCloningRecipe>> CLONE_MUSIC_DISC = REGISTRY.register("music_disc_cloning", () -> unit(new MusicDiscCloningRecipe()));
    public static final Supplier<RecipeSerializer<MusicLabelDyeRecipe>> DYE_MUSIC_LABEL = REGISTRY.register("dye_music_label", () -> unit(new MusicLabelDyeRecipe()));

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeSerializer<T> unit(T recipe) {
        return new RecipeSerializer<>(MapCodec.unit(recipe), StreamCodec.unit(recipe));
    }
}

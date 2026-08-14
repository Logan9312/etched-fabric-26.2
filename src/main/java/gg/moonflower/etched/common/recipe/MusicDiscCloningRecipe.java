package gg.moonflower.etched.common.recipe;

import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MusicDiscCloningRecipe extends CustomRecipe {
    private static final TagKey<Item> MUSIC_DISCS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("music_discs"));

    public MusicDiscCloningRecipe() {
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack base = ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(MUSIC_DISCS)) {
                if (!base.isEmpty()) {
                    return false;
                }

                base = stack;
            } else {
                if (!copy.isEmpty()) {
                    return false;
                }
                if (!stack.is(EtchedItems.BLANK_MUSIC_DISC.get())) {
                    return false;
                }

                copy = stack;
            }
        }

        return !base.isEmpty() && !copy.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container) {
        ItemStack base = ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;

        for (int j = 0; j < container.size(); ++j) {
            ItemStack stack = container.getItem(j);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(MUSIC_DISCS)) {
                if (!base.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                base = stack;
            } else {
                if (!copy.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                if (!stack.is(EtchedItems.BLANK_MUSIC_DISC.get())) {
                    return ItemStack.EMPTY;
                }

                copy = stack;
            }
        }

        if (base.isEmpty() || copy.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return base.copyWithCount(1);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = CraftingRecipe.defaultCraftingReminder(inv);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && !stack.is(EtchedItems.BLANK_MUSIC_DISC.get())) {
                list.set(i, stack.copyWithCount(1));
            }
        }

        return list;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return EtchedRecipes.CLONE_MUSIC_DISC.get();
    }
}

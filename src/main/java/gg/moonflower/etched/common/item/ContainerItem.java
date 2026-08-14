package gg.moonflower.etched.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ContainerItem {

    static int findSlotMatchingItem(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getNonEquipmentItems().size(); ++i) {
            ItemStack slotStack = inventory.getNonEquipmentItems().get(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, slotStack)) {
                return i;
            }
        }

        return -1;
    }

    default InteractionResult use(Item item, Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int index = findSlotMatchingItem(player.getInventory(), stack);
        if (index == -1) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.awardStat(Stats.ITEM_USED.get(item));
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    return ContainerItem.this.constructMenu(containerId, inventory, player, index);
                }
            });
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    AbstractContainerMenu constructMenu(int containerId, Inventory inventory, Player player, int index);
}

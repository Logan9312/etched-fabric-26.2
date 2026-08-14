package gg.moonflower.etched.common.item;

import gg.moonflower.etched.client.screen.EditMusicLabelScreen;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MusicLabelItem extends Item {

    public MusicLabelItem(Properties properties) {
        super(properties);
    }
    @Environment(EnvType.CLIENT)
    private void openMusicLabelEditScreen(Player player, InteractionHand hand, ItemStack stack) {
        Minecraft.getInstance().gui.setScreen(new EditMusicLabelScreen(player, hand, stack));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            this.openMusicLabelEditScreen(player, hand, stack);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        MusicLabelComponent label = stack.getOrDefault(EtchedComponents.MUSIC_LABEL.get(), MusicLabelComponent.DEFAULT);
        if (label.artist().isEmpty()) {
            stack.set(EtchedComponents.MUSIC_LABEL.get(), label.withArtist(entity.getDisplayName().getString()));
        }
    }

//    @Override
//    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
//        if (!getAuthor(itemStack).isEmpty() && !getTitle(itemStack).isEmpty()) {
//            list.add(Component.translatable("sound_source." + Etched.MOD_ID + ".info", getAuthor(itemStack), getTitle(itemStack)).withStyle(ChatFormatting.GRAY));
//        }
//    }
}

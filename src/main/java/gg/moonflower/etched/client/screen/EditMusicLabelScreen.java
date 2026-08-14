package gg.moonflower.etched.client.screen;

import net.minecraft.client.renderer.RenderPipelines;

import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.network.play.ServerboundEditMusicLabelPacket;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class EditMusicLabelScreen extends Screen {

    private static final Identifier TEXTURE = Etched.etchedPath("textures/gui/edit_music_label.png");
    private static final Identifier LABEL = Etched.etchedPath("textures/gui/label.png");
    private static final Component TITLE_COMPONENT = Component.translatable("screen.etched.edit_music_label.title");
    private static final Component AUTHOR_COMPONENT = Component.translatable("screen.etched.edit_music_label.author");

    private final Player player;
    private final MusicLabelComponent musicLabel;
    private final InteractionHand hand;
    private final int imageWidth = 176;
    private final int imageHeight = 139;

    private Button doneButton;
    private EditBox title;
    private EditBox author;

    public EditMusicLabelScreen(Player player, InteractionHand hand, ItemStack stack) {
        super(TITLE_COMPONENT);
        this.player = player;
        this.hand = hand;

        MusicLabelComponent musicLabel = stack.get(EtchedComponents.MUSIC_LABEL.get());
        if (musicLabel == null) {
            musicLabel = MusicLabelComponent.DEFAULT.withArtist(player.getDisplayName().getString());
        }
        this.musicLabel = musicLabel;
    }

    @Override
    protected void init() {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        this.doneButton = Button.builder(CommonComponents.GUI_DONE, button -> {
            this.saveChanges();
            this.minecraft.gui.setScreen(null);
        }).bounds(leftPos, topPos + this.imageHeight + 5, this.imageWidth, 20).build();
        this.addRenderableWidget(this.doneButton);

        this.title = new EditBox(this.font, leftPos + 10, topPos + 91, 154, 10, TITLE_COMPONENT);
        this.title.setValue(this.musicLabel.title());
        this.title.setTextColorUneditable(-1);
        this.title.setTextColor(-1);
        this.title.setMaxLength(128);
        this.title.setBordered(false);
        this.title.setCanLoseFocus(true);
        this.title.setFocused(true);
        this.setFocused(this.title);

        this.author = new EditBox(this.font, leftPos + 10, topPos + 121, 154, 10, AUTHOR_COMPONENT);
        this.author.setValue(this.musicLabel.artist());
        this.author.setTextColorUneditable(-1);
        this.author.setTextColor(-1);
        this.author.setMaxLength(128);
        this.author.setBordered(false);
        this.author.setCanLoseFocus(true);

        this.title.setResponder(string -> this.doneButton.active = !this.author.getValue().isEmpty() && !string.isEmpty());
        this.addRenderableWidget(this.title);

        this.author.setResponder(string -> this.doneButton.active = !this.title.getValue().isEmpty() && !string.isEmpty());
        this.addRenderableWidget(this.author);
    }

    @Override
    public void resize(int i, int j) {
        String title = this.title.getValue();
        String author = this.author.getValue();

        boolean titleFocused = this.title.isFocused();
        boolean authorFocused = this.author.isFocused();
        GuiEventListener focused = this.getFocused();

        this.init(i, j);
        this.title.setValue(title);
        this.title.setFocused(titleFocused);
        this.author.setValue(author);
        this.author.setFocused(authorFocused);
        this.setFocused(focused);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractTransparentBackground(graphics);

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        graphics.text(this.font, TITLE_COMPONENT, leftPos + 7, topPos + 77, 4210752, false);
        graphics.text(this.font, AUTHOR_COMPONENT, leftPos + 7, topPos + 77 + 30, 4210752, false);

        int primaryLabelColor = this.musicLabel.primaryColor();
        int secondaryLabelColor = this.musicLabel.secondaryColor();

        graphics.blit(RenderPipelines.GUI_TEXTURED, LABEL, leftPos, topPos, 0, 0, this.imageWidth, 70, 256, 256, 0xFF000000 | primaryLabelColor);

        graphics.blit(RenderPipelines.GUI_TEXTURED, LABEL, leftPos, topPos, 0, 70, this.imageWidth, 70, 256, 256, 0xFF000000 | secondaryLabelColor);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void saveChanges() {
        int slot = this.hand == InteractionHand.MAIN_HAND ? this.player.getInventory().getSelectedSlot() : 40;
        String author = this.author.getValue().trim();
        String title = this.title.getValue().trim();
        PacketDistributor.sendToServer(new ServerboundEditMusicLabelPacket(slot, author, title));
    }
}

package gg.moonflower.etched.common.network.play;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.core.Etched;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * @param record The record to play
 * @param pos    The position the music disk is playing at
 * @author Ocelot
 */
@ApiStatus.Internal
public record ClientboundPlayBlockMusicPacket(ItemStack record, BlockPos pos, @Nullable UUID storageId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundPlayBlockMusicPacket> TYPE = new CustomPacketPayload.Type<>(Etched.etchedPath("play_block_music"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayBlockMusicPacket> CODEC = StreamCodec.of((buf, packet) -> {
        ItemStack.STREAM_CODEC.encode(buf, packet.record);
        buf.writeBlockPos(packet.pos);
        writeStorageId(buf, packet.storageId);
    }, buf -> {
        ItemStack record = ItemStack.STREAM_CODEC.decode(buf);
        BlockPos pos = buf.readBlockPos();
        UUID storageId = readStorageId(buf);
        return new ClientboundPlayBlockMusicPacket(record, pos, storageId);
    });

    static void writeStorageId(FriendlyByteBuf buf, @Nullable UUID storageId) {
        buf.writeBoolean(storageId != null);
        if (storageId != null) {
            buf.writeUUID(storageId);
        }
    }

    @Nullable
    static UUID readStorageId(FriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readUUID() : null;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * @param registries The registry instance to get data from
     * @return The tracks to play in sequence
     */
    public List<TrackData> tracks(HolderLookup.Provider registries) {
        return PlayableRecord.getTracks(registries, this.record);
    }
}

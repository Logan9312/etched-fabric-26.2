package gg.moonflower.etched.common.network.play;

import gg.moonflower.etched.core.Etched;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundConfigPacket(boolean useBoomboxMenu, boolean useAlbumCoverMenu) implements CustomPacketPayload {
    public static final Type<ClientboundConfigPacket> TYPE = new Type<>(Etched.etchedPath("server_config"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundConfigPacket> CODEC = StreamCodec.of(
            (buffer, packet) -> {
                buffer.writeBoolean(packet.useBoomboxMenu);
                buffer.writeBoolean(packet.useAlbumCoverMenu);
            },
            buffer -> new ClientboundConfigPacket(buffer.readBoolean(), buffer.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

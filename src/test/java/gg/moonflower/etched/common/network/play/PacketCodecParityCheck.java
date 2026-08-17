package gg.moonflower.etched.common.network.play;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;

import java.util.Objects;
import java.util.UUID;

import gg.moonflower.etched.test.ResourceParityCheck;
import gg.moonflower.etched.test.ComponentCodecParityCheck;

public final class PacketCodecParityCheck {
    private PacketCodecParityCheck() {
    }

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.START, null);
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.START, UUID.fromString("8c36c44d-b63b-4c92-8984-185e90d0e962"));
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.RESTART, null);
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.RESTART, UUID.fromString("a8268084-c239-43ba-8619-59d2d9771551"));
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.STOP, null);
        roundTripEntity(ClientboundPlayEntityMusicPacket.Action.STOP, UUID.fromString("0e88f7eb-3fe3-47f5-9d3e-45d1055f00c6"));

        roundTripBlock(null);
        roundTripBlock(UUID.fromString("ca759e7d-2169-4292-8702-10cbd49b354c"));
        roundTripConfig(false, false);
        roundTripConfig(true, false);
        roundTripConfig(false, true);
        roundTripConfig(true, true);
        ComponentCodecParityCheck.run();
        ResourceParityCheck.run();
    }

    private static void roundTripEntity(ClientboundPlayEntityMusicPacket.Action action, UUID storageId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ClientboundPlayEntityMusicPacket.writeStorageId(buffer, action, storageId);
            UUID output = ClientboundPlayEntityMusicPacket.readStorageId(buffer, action);
            UUID expected = action == ClientboundPlayEntityMusicPacket.Action.STOP ? null : storageId;
            require(Objects.equals(output, expected), "Entity storage id did not round-trip for " + action);
            require(buffer.readableBytes() == 0, "Entity storage id left unread bytes for " + action);
        } finally {
            buffer.release();
        }
    }

    private static void roundTripBlock(UUID storageId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ClientboundPlayBlockMusicPacket.writeStorageId(buffer, storageId);
            UUID output = ClientboundPlayBlockMusicPacket.readStorageId(buffer);
            require(Objects.equals(output, storageId), "Block storage id did not round-trip");
            require(buffer.readableBytes() == 0, "Block storage id left unread bytes");
        } finally {
            buffer.release();
        }
    }

    private static void roundTripConfig(boolean boomboxMenu, boolean albumCoverMenu) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ClientboundConfigPacket input = new ClientboundConfigPacket(boomboxMenu, albumCoverMenu);
            ClientboundConfigPacket.CODEC.encode(buffer, input);
            ClientboundConfigPacket output = ClientboundConfigPacket.CODEC.decode(buffer);
            require(output.equals(input), "Server config did not round-trip");
            require(buffer.readableBytes() == 0, "Server config left unread bytes");
        } finally {
            buffer.release();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

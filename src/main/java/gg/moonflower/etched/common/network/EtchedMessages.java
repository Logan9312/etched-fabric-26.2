package gg.moonflower.etched.common.network;

import gg.moonflower.etched.common.network.play.ClientboundInvalidEtchUrlPacket;
import gg.moonflower.etched.common.network.play.ClientboundPlayBlockMusicPacket;
import gg.moonflower.etched.common.network.play.ClientboundPlayEntityMusicPacket;
import gg.moonflower.etched.common.network.play.ServerboundEditMusicLabelPacket;
import gg.moonflower.etched.common.network.play.SetAlbumJukeboxTrackPacket;
import gg.moonflower.etched.common.network.play.SetUrlPacket;
import gg.moonflower.etched.common.network.play.handler.EtchedClientPlayPacketHandler;
import gg.moonflower.etched.common.network.play.handler.EtchedServerPlayPacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class EtchedMessages {
    private static boolean commonInitialized;
    private static boolean clientInitialized;

    private EtchedMessages() {
    }

    public static synchronized void initCommon() {
        if (commonInitialized) {
            return;
        }
        commonInitialized = true;

        PayloadTypeRegistry.clientboundPlay().register(ClientboundInvalidEtchUrlPacket.TYPE, ClientboundInvalidEtchUrlPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundPlayBlockMusicPacket.TYPE, ClientboundPlayBlockMusicPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundPlayEntityMusicPacket.TYPE, ClientboundPlayEntityMusicPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SetAlbumJukeboxTrackPacket.TYPE, SetAlbumJukeboxTrackPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SetUrlPacket.TYPE, SetUrlPacket.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(ServerboundEditMusicLabelPacket.TYPE, ServerboundEditMusicLabelPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetAlbumJukeboxTrackPacket.TYPE, SetAlbumJukeboxTrackPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetUrlPacket.TYPE, SetUrlPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundEditMusicLabelPacket.TYPE,
                (packet, context) -> EtchedServerPlayPacketHandler.handleEditMusicLabel(packet, context::player));
        ServerPlayNetworking.registerGlobalReceiver(SetAlbumJukeboxTrackPacket.TYPE,
                (packet, context) -> EtchedServerPlayPacketHandler.handleSetAlbumJukeboxTrack(packet, context::player));
        ServerPlayNetworking.registerGlobalReceiver(SetUrlPacket.TYPE,
                (packet, context) -> EtchedServerPlayPacketHandler.handleSetUrl(packet, context::player));
    }

    public static synchronized void initClient() {
        if (clientInitialized) {
            return;
        }
        clientInitialized = true;

        ClientPlayNetworking.registerGlobalReceiver(ClientboundInvalidEtchUrlPacket.TYPE,
                (packet, context) -> EtchedClientPlayPacketHandler.handleSetInvalidEtch(packet, context::player));
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPlayBlockMusicPacket.TYPE,
                (packet, context) -> EtchedClientPlayPacketHandler.handlePlayBlockMusicPacket(packet, context::player));
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPlayEntityMusicPacket.TYPE,
                (packet, context) -> EtchedClientPlayPacketHandler.handlePlayEntityMusicPacket(packet, context::player));
        ClientPlayNetworking.registerGlobalReceiver(SetAlbumJukeboxTrackPacket.TYPE,
                (packet, context) -> EtchedClientPlayPacketHandler.handleSetAlbumJukeboxTrack(packet, context::player));
        ClientPlayNetworking.registerGlobalReceiver(SetUrlPacket.TYPE,
                (packet, context) -> EtchedClientPlayPacketHandler.handleSetUrl(packet, context::player));
    }
}

package gg.moonflower.etched.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.component.MusicTrackComponent;
import gg.moonflower.etched.common.component.PausedComponent;
import gg.moonflower.etched.core.EtchedConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public final class ComponentCodecParityCheck {
    private ComponentCodecParityCheck() {
    }

    public static void run() {
        TrackData track = new TrackData("https://example.invalid/song.ogg", "Artist", Component.literal("Title"));
        roundTripJson(TrackData.CODEC, track, "track data");
        roundTripJson(MusicTrackComponent.CODEC, new MusicTrackComponent(List.of(track)), "music track component");

        MusicLabelComponent label = new MusicLabelComponent("Artist", "Title", 0x123456, 0xABCDEF);
        roundTripJson(MusicLabelComponent.CODEC, label, "music label component");
        roundTripNetwork(MusicLabelComponent.STREAM_CODEC, label, "music label component");

        for (DiscAppearanceComponent.LabelPattern pattern : DiscAppearanceComponent.LabelPattern.values()) {
            int primary = pattern.isColorable() ? 0x123456 : 0xFFFFFF;
            int secondary = pattern.isColorable() ? 0xABCDEF : 0xFFFFFF;
            DiscAppearanceComponent appearance = new DiscAppearanceComponent(pattern, 0x654321, primary, secondary);
            roundTripJson(DiscAppearanceComponent.CODEC, appearance, "disc appearance " + pattern);
            roundTripNetwork(DiscAppearanceComponent.STREAM_CODEC, appearance, "disc appearance " + pattern);
        }

        roundTripJson(PausedComponent.CODEC, PausedComponent.INSTANCE, "paused component");
        require(PausedComponent.CODEC.parse(JsonOps.INSTANCE, new JsonObject()).getOrThrow() == PausedComponent.INSTANCE,
                "Paused component does not decode the upstream unit form");
        require(PausedComponent.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(true)).getOrThrow() == PausedComponent.INSTANCE,
                "Paused component does not decode the alpha.1-alpha.3 boolean form");
        require(PausedComponent.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(false)).getOrThrow() == PausedComponent.INSTANCE,
                "Paused component does not decode the alpha.1-alpha.3 false boolean form");
        ByteBuf buffer = Unpooled.buffer();
        try {
            PausedComponent.STREAM_CODEC.encode(buffer, PausedComponent.INSTANCE);
            require(PausedComponent.STREAM_CODEC.decode(buffer) == PausedComponent.INSTANCE, "Paused component did not round-trip");
            require(buffer.readableBytes() == 0, "Paused component left unread bytes");
        } finally {
            buffer.release();
        }

        EtchedConfig.Server authoritative = new EtchedConfig.Server();
        authoritative.useBoomboxMenu.set(true);
        EtchedConfig.Server clientMirror = new EtchedConfig.Server();
        clientMirror.copyFrom(authoritative);
        clientMirror.useBoomboxMenu.set(false);
        require(authoritative.useBoomboxMenu.get(), "Client config mirror mutated the authoritative server config");
    }

    private static <T> void roundTripJson(Codec<T> codec, T input, String name) {
        JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, input).getOrThrow();
        T decoded = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow();
        require(input.equals(decoded), "Persistent codec did not round-trip " + name);
    }

    private static <T> void roundTripNetwork(StreamCodec<FriendlyByteBuf, T> codec, T input, String name) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            codec.encode(buffer, input);
            T decoded = codec.decode(buffer);
            require(input.equals(decoded), "Network codec did not round-trip " + name);
            require(buffer.readableBytes() == 0, "Network codec left unread bytes for " + name);
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

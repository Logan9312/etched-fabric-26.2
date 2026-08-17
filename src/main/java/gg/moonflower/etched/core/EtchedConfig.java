package gg.moonflower.etched.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Simple file-backed settings shared by clients and dedicated servers. */
public final class EtchedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private EtchedConfig() {
    }

    public static void load(Client client, Server server) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("etched.json");
        try {
            if (Files.exists(path)) {
                JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
                readBoolean(root, "client", "forceStereo", client.forceStereo);
                readBoolean(root, "client", "smoothParrotAnimation", client.smoothParrotAnimation);
                readBoolean(root, "server", "useBoomboxMenu", server.useBoomboxMenu);
                readBoolean(root, "server", "useAlbumCoverMenu", server.useAlbumCoverMenu);
            } else {
                Files.createDirectories(path.getParent());
                Files.writeString(path, GSON.toJson(write(client, server)) + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (Exception exception) {
            Etched.LOGGER.error("Failed to load Etched configuration from {}. Using defaults.", path, exception);
        }
    }

    private static void readBoolean(JsonObject root, String sectionName, String optionName, BooleanOption option) {
        JsonElement sectionElement = root.get(sectionName);
        if (sectionElement == null || !sectionElement.isJsonObject()) {
            return;
        }
        JsonElement value = sectionElement.getAsJsonObject().get(optionName);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            option.set(value.getAsBoolean());
        }
    }

    private static JsonObject write(Client client, Server server) {
        JsonObject root = new JsonObject();
        JsonObject clientSection = new JsonObject();
        clientSection.addProperty("forceStereo", client.forceStereo.get());
        clientSection.addProperty("smoothParrotAnimation", client.smoothParrotAnimation.get());
        root.add("client", clientSection);

        JsonObject serverSection = new JsonObject();
        serverSection.addProperty("useBoomboxMenu", server.useBoomboxMenu.get());
        serverSection.addProperty("useAlbumCoverMenu", server.useAlbumCoverMenu.get());
        root.add("server", serverSection);
        return root;
    }

    public static final class BooleanOption {
        private boolean value;

        public BooleanOption(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value) {
            this.value = value;
        }
    }

    public static final class Client {
        public final BooleanOption forceStereo = new BooleanOption(false);
        public final BooleanOption smoothParrotAnimation = new BooleanOption(true);
    }

    public static final class Server {
        public final BooleanOption useBoomboxMenu = new BooleanOption(false);
        public final BooleanOption useAlbumCoverMenu = new BooleanOption(false);

        public void copyFrom(Server source) {
            this.useBoomboxMenu.set(source.useBoomboxMenu.get());
            this.useAlbumCoverMenu.set(source.useAlbumCoverMenu.get());
        }
    }
}

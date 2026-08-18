package gg.moonflower.etched.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.Recipe;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public final class ResourceParityCheck {
    private static final Path RESOURCES = Path.of(System.getProperty("etched.projectDir", "."), "src/main/resources");
    private static final Map<Integer, Integer> BARD_TRADES_PER_LEVEL = Map.of(
            1, 6,
            2, 2,
            3, 7,
            4, 2,
            5, 14);

    private ResourceParityCheck() {
    }

    public static void run() {
        validateJsonSyntax();
        validateRecipeIngredientShapes();
        validateBardTrades();
        validateComponentDrivenItemModels();
        require(Files.isRegularFile(RESOURCES.resolve("data/c/tags/item/music_discs.json")),
                "The common music-discs tag is missing");
    }

    private static void validateRecipeIngredientShapes() {
        Path recipeDirectory = RESOURCES.resolve("data/etched/recipe");
        try (var paths = Files.list(recipeDirectory)) {
            paths.filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                JsonObject recipe = readJson(path).getAsJsonObject();
                String type = recipe.get("type").getAsString();
                if (type.equals("minecraft:crafting_shaped")) {
                    recipe.getAsJsonObject("key").entrySet().forEach(entry -> require(
                            entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString(),
                            path.getFileName() + " uses the pre-26.2 shaped ingredient format for key " + entry.getKey()));
                } else if (type.equals("minecraft:crafting_shapeless")) {
                    recipe.getAsJsonArray("ingredients").forEach(ingredient -> require(
                            ingredient.isJsonPrimitive() && ingredient.getAsJsonPrimitive().isString(),
                            path.getFileName() + " uses the pre-26.2 shapeless ingredient format"));
                } else if (type.equals("minecraft:smelting")) {
                    JsonElement ingredient = recipe.get("ingredient");
                    require(ingredient.isJsonPrimitive() && ingredient.getAsJsonPrimitive().isString(),
                            path.getFileName() + " uses the pre-26.2 cooking ingredient format");
                }
                if (type.startsWith("minecraft:")) {
                    validateVanillaRecipeCodec(path, recipe);
                }
            });
        } catch (IOException exception) {
            throw new AssertionError("Unable to enumerate recipes", exception);
        }
    }

    private static void validateVanillaRecipeCodec(Path path, JsonObject recipe) {
        JsonObject normalized = recipe.deepCopy();
        normalized.getAsJsonObject("result").addProperty("id", "minecraft:stone");
        if (normalized.has("key")) {
            normalized.getAsJsonObject("key").entrySet().forEach(entry -> {
                String ingredient = entry.getValue().getAsString();
                if (ingredient.startsWith("#") || ingredient.startsWith("etched:")) {
                    entry.setValue(JsonParser.parseString("\"minecraft:stone\""));
                }
            });
        }
        if (normalized.has("ingredients")) {
            var ingredients = normalized.getAsJsonArray("ingredients");
            for (int i = 0; i < ingredients.size(); i++) {
                String ingredient = ingredients.get(i).getAsString();
                if (ingredient.startsWith("#") || ingredient.startsWith("etched:")) {
                    ingredients.set(i, JsonParser.parseString("\"minecraft:stone\""));
                }
            }
        }
        if (normalized.has("ingredient")) {
            String ingredient = normalized.get("ingredient").getAsString();
            if (ingredient.startsWith("#") || ingredient.startsWith("etched:")) {
                normalized.addProperty("ingredient", "minecraft:stone");
            }
        }
        var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        try {
            Recipe.CODEC.parse(ops, normalized).getOrThrow();
        } catch (RuntimeException exception) {
            throw new AssertionError("Minecraft 26.2 recipe codec rejected " + path.getFileName(), exception);
        }
    }

    private static void validateComponentDrivenItemModels() {
        JsonObject label = itemModel("music_label");
        var labelTints = label.getAsJsonArray("tints");
        require(labelTints.size() == 2, "Music label must provide two component-driven tint layers");
        require(labelTints.asList().stream().allMatch(tint -> tint.getAsJsonObject().get("type").getAsString().equals("etched:music_label")),
                "Music label uses a non-Etched tint source");

        JsonObject disc = itemModel("etched_music_disc");
        require(disc.get("type").getAsString().equals("minecraft:select"), "Etched disc is not a select model");
        require(disc.get("property").getAsString().equals("etched:disc_pattern"), "Etched disc uses the wrong pattern property");
        Set<String> patterns = Set.of("flat", "cross", "eye", "parallel", "star", "gold");
        Set<String> actualPatterns = disc.getAsJsonArray("cases").asList().stream()
                .map(entry -> entry.getAsJsonObject().get("when").getAsString())
                .collect(java.util.stream.Collectors.toSet());
        require(actualPatterns.equals(patterns), "Etched disc pattern cases do not match all label patterns");

        JsonObject cover = itemModel("album_cover");
        require(cover.get("type").getAsString().equals("minecraft:special"), "Album cover does not use the special renderer");
        require(cover.getAsJsonObject("model").get("type").getAsString().equals("etched:album_cover"),
                "Album cover special renderer id is incorrect");
    }

    private static JsonObject itemModel(String name) {
        return readJson(RESOURCES.resolve("assets/etched/items/" + name + ".json"))
                .getAsJsonObject().getAsJsonObject("model");
    }

    private static void validateJsonSyntax() {
        try (var paths = Files.walk(RESOURCES)) {
            paths.filter(path -> path.toString().endsWith(".json")).forEach(path -> readJson(path));
        } catch (IOException exception) {
            throw new AssertionError("Unable to enumerate resources", exception);
        }
    }

    private static void validateBardTrades() {
        BARD_TRADES_PER_LEVEL.forEach((level, expectedCount) -> {
            Path tradeSetPath = RESOURCES.resolve("data/etched/trade_set/bard/level_" + level + ".json");
            JsonObject tradeSet = readJson(tradeSetPath).getAsJsonObject();
            require(tradeSet.get("trades").getAsString().equals("#etched:bard/level_" + level),
                    "Bard trade set " + level + " points at the wrong tag");

            Path tagPath = RESOURCES.resolve("data/etched/tags/villager_trade/bard/level_" + level + ".json");
            var values = readJson(tagPath).getAsJsonObject().getAsJsonArray("values");
            require(values.size() == expectedCount,
                    "Bard level " + level + " expected " + expectedCount + " trades but found " + values.size());

            values.forEach(value -> {
                String id = value.getAsString();
                require(id.startsWith("etched:bard/" + level + "/"), "Unexpected bard trade id: " + id);
                Path tradePath = RESOURCES.resolve("data/etched/villager_trade/" + id.substring("etched:".length()) + ".json");
                require(Files.isRegularFile(tradePath), "Missing bard trade resource: " + tradePath);
            });
        });

        JsonObject noteBlockTrade = readJson(RESOURCES.resolve(
                "data/etched/villager_trade/bard/1/emerald_note_block.json")).getAsJsonObject();
        require(noteBlockTrade.getAsJsonObject("wants").get("id").getAsString().equals("minecraft:note_block"),
                "Bard note-block trade must buy note blocks from the player");
        require(noteBlockTrade.getAsJsonObject("wants").get("count").getAsInt() == 2,
                "Bard note-block trade must buy two note blocks");
        require(noteBlockTrade.getAsJsonObject("gives").get("id").getAsString().equals("minecraft:emerald"),
                "Bard note-block trade must pay an emerald");
    }

    private static JsonElement readJson(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader);
        } catch (Exception exception) {
            throw new AssertionError("Invalid JSON resource: " + path, exception);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

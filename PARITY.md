# Etched 5.1.0 parity matrix

Audit baseline: upstream tag `mc1.21.1-5.1.0-etched`, commit
`9cecd797bdb81615111b8fc46fdea6b1092a459d`.

Status values:

- **automated verified**: a repeatable check exercises the relevant invariant.
- **launch verified**: the code and resources load in the applicable Minecraft launch path.
- **implemented**: source comparison shows the behavior is present, but runtime proof is still pending.
- **manual test needed**: meaningful proof requires an interactive client or multiple clients.
- **known gap**: confirmed missing or behaviorally different from upstream.
- **deferred**: intentionally not implemented, with the dependency or validation requirement stated.

## Feature coverage

| Subsystem | Status | Evidence / remaining work |
| --- | --- | --- |
| Fabric 26.2 build and metadata | automated verified | Java 25, Fabric Loader 0.19.3 and Fabric API 0.156.0+26.2. `clean build` runs `parityTest` and packages successfully. |
| Registries and initialization | automated verified | Registry IDs and counts match upstream: 3 blocks, 2 block entities, 10 items, 1 entity, 5 menus, 7 components, 3 recipe serializers, 1 sound, 1 POI and 1 profession. Server GameTests place all three blocks, verify both block entities, and spawn/tick the jukebox minecart. |
| Runtime resources | automated verified | All JSON parses; all bard trade tags resolve to checked-in trade files; vanilla recipe ingredients use Minecraft 26.2 string/tag syntax. Upstream assets/data are otherwise byte-equivalent after accounting for required 26.2 item definitions and GUI-sprite relocation. A fresh in-world reload remains a manual release check. |
| Data components and codecs | automated verified | Persistent and direct-buffer network round trips cover track data, music tracks, labels, every disc pattern and paused state. Registry-aware ItemStack components still require world/save fixtures. The paused codec retains upstream unit-form compatibility. |
| Custom discs and labels | launch verified | 26.2 custom tint sources and a select-model property restore all six patterns plus disc/primary/secondary component colors. The definitions decode during a real client resource reload; craft each combination for final visual comparison. |
| Etching table | manual test needed | Block, menu, screen, URL lookup and recipe flow are present. Client launch proves its screen resources load; complete etching and invalid-URL flows need gameplay tests. |
| Album covers and album jukebox | implemented | Storage, menu, playback and track selection are present. A 26.2 special-item renderer now requests, caches, uploads and releases processed remote cover textures, with extruded edge geometry, foil overlay and static vanilla/default fallbacks. Per-job overlay copies avoid reload races. Its model/resources load; downloaded artwork still needs an in-world smoke test. |
| Addon static cover models | deferred | Upstream allowed resource packs/addons to return an arbitrary baked model for a record. The private pack's built-in vanilla covers are preserved, but arbitrary third-party cover models are not submitted by the 26.2 special renderer. Revisit if this private server adopts an addon using that extension point. |
| Album-cover grindstone removal | implemented | `GrindstoneMenuMixin` restores upstream removal of the nested cover stack. Required mixin applies during client launch; result-slot and take-result behavior need a gameplay test. |
| Boombox | implemented | Menu, playback state and player synchronization are present. Entity-music packet storage tails round-trip automatically, and the playing arm pose is restored after attack animation. Two-client playback and a visual pose check remain. |
| Radio and portal radio | manual test needed | Streaming, menus, Nether item conversion and dimension behavior are present. Test URL validation, save/reload, redstone and portal conversion. |
| Jukebox minecart | manual test needed | Entity, dispenser/item behavior, persistence, renderer and playback are present. Late-tracking playback/state synchronization is not proven. |
| Bard profession and trades | automated verified | Profession name key is corrected. Levels 1–5 reference 26.2 data-driven trade sets reproducing upstream prices, quantities, direction, uses and XP; resource linkage and the unusual note-block purchase direction are checked. A server GameTest also constructs the Bard work package through the live mixin path. Actual offer generation still needs an integrated-world test. |
| Bard village houses | implemented | Server-start injection restores five normal and zombie village-pool additions with upstream weights and structures. Desert/savanna/snowy intentionally use `minecraft:empty` because upstream referenced a nonexistent `minecraft:bard_house` processor and silently skipped those normal houses. Integrated generation is not yet verified. |
| Vanilla jukebox integration | manual test needed | Playable components, hopper behavior and stop/start hooks are present. Late join, chunk reload and save/restart behavior require two-client testing. |
| Audio decoding and streaming | launch verified | OGG/WAV/MP3 decoder chain and streaming utilities match upstream; JLayer is bundled and OpenAL starts. Add deterministic local-HTTP fixture tests and natural-track-end tests. |
| SoundCloud and Bandcamp | implemented | Resolver/request logic is retained. SoundCloud now forwards per-track authorization and falls through failed transcoding candidates. DRM/encrypted-only tracks remain unsupported; use an upload exposing a standard MP3/HLS stream. Add fixture tests and opt-in live smoke tests because provider markup and client IDs can change. |
| Sound and artwork caches | implemented | Fabric tick/disconnect/reload lifecycle replaces NeoForge events. Add hit, expiry, no-store, stale-error and size-cap tests. |
| Networking | automated verified | Payload registrations and directions are present. Block/entity optional storage UUID tails now use an unconditional wire schema; START/RESTART optional tails and STOP canonicalization are checked by `parityTest`. Full payload and two-client coverage remains pending. Use the same release on clients and servers because the post-alpha.3 wire schema intentionally changed. |
| Music-disc cloning | implemented | Recipe now checks `c:music_discs`, matching the upstream common tag and the checked-in Etched disc tag. Add a crafting GameTest proving component preservation and remaining items. |
| Configuration | implemented | All four upstream options load from `config/etched.json`; a launch creates the documented defaults when absent. The two server-authoritative menu settings synchronize to clients on join and their four value combinations round-trip automatically. A real dedicated-client mismatch test remains; no in-game config screen is provided. |
| Smooth parrot animation | launch verified | Minecraft 26.2 already drives party animation with fractional `ageInTicks`; Etched preserves the upstream option by quantizing that value only when smoothing is disabled. The required mixin applies during client launch; visual comparison remains. |
| Download/status HUD hue | launch verified | The animated overlay hue adjustment targets the 26.2 `Hud.extractOverlayMessage` path; the required mixin applies during client launch. Visual comparison remains. |
| Sophisticated Core compatibility | deferred | Upstream integration classes require NeoForge-only APIs and are excluded. Packet layout no longer depends on whether a mod named `sophisticatedcore` is installed. Revisit only if a compatible Fabric 26.2 API exists. |
| Fabric-native datagen | known gap | Runtime data is checked in and validated syntactically, but the retained NeoForge providers are excluded and cannot regenerate it. |
| Dedicated-server startup | automated verified | The server GameTest runner accepts the EULA, loads recipes/advancements, reaches world readiness, executes live ticks and shuts down cleanly. Save/restart and real player join remain external/manual validation requirements. |
| Automated regression suite | automated verified | `build` runs packet/component/resource parity checks plus server GameTests that audit every server mixin, spawn and tick a villager, exercise the Bard work-package mixin, place every Etched block/block entity and tick the custom minecart. A production client GameTest audits client mixins, completes resource reload and world join, renders frames, and captures a screenshot on the self-hosted `etched-ci` runners. Registry-aware ItemStack/payload fixtures and multi-client scenarios remain. |

## Audit findings fixed in alpha.4 and alpha.5

- Fixed asymmetric entity-music decoding that read a presence byte the encoder omitted when Sophisticated Core was absent.
- Made block/entity optional storage UUID wire layouts unconditional and loader-independent.
- Corrected music-disc cloning from `minecraft:music_discs` to `c:music_discs`.
- Restored album-cover grindstone removal.
- Restored bard translation, level 1–5 trades, and normal/zombie village-house pool injection.
- Restored component-driven disc patterns/colors and music-label colors using the 26.2 item-model API.
- Restored dynamic remote album artwork through a 26.2 special-item renderer with lifecycle cleanup.
- Restored persistent client/server settings in `config/etched.json`.
- Restored server-authoritative menu configuration synchronization for multiplayer clients.
- Restored boombox attack-arm suppression and configurable parrot animation smoothing.
- Restored the animated download/status HUD hue on Minecraft 26.2's extracted HUD path.
- Corrected two-color music-label persistence so the complete representation is encoded while legacy one-color data still decodes.
- Added repeatable packet and resource regression checks to the normal Gradle `check`/`build` lifecycle.
- Alpha.5 migrates all eight rejected vanilla recipes to Minecraft 26.2 ingredient syntax and prevents legacy ingredient objects from returning.
- Alpha.5 forwards SoundCloud track authorization and continues past a dead progressive/HLS candidate.

## Required manual/runtime scenarios

1. Create and reload a world; confirm all Etched datapack registries decode without warnings.
2. Complete every etching-table path: direct URL, SoundCloud, Bandcamp, invalid URL, label pattern and colors.
3. Verify album-cover grindstone removal and all album/boombox menu-vs-direct-interaction configuration modes.
4. Spawn and level a bard through levels 1–5; inspect offers and locate each village house type.
5. Exercise OGG, WAV, MP3, playlist, loop and natural-end advancement using deterministic local fixtures.
6. Save and restart a dedicated world, then join with two real clients.
7. Test late tracking, chunk unload/reload and reconnect for jukeboxes, minecarts, boomboxes, radios and album jukeboxes.
8. Visually verify all disc patterns/colors, label colors, downloaded album covers, boombox poses and both parrot smoothing modes.

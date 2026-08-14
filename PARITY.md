# Etched 5.1.0 parity matrix

Status values: **implemented**, **launch verified**, **manual test needed**, or **known gap**.

| Subsystem | Status | Evidence / remaining work |
| --- | --- | --- |
| Fabric 26.2 build and metadata | launch verified | Java 25, Fabric Loader 0.19.3 and Fabric API 0.156.0+26.2. |
| Registries and initialization | launch verified | All Etched registries initialize on both Fabric launch paths. |
| Data components and codecs | implemented | Persistent/network codecs ported; save round-trip needs gameplay testing. |
| Custom discs and labels | manual test needed | Items, components, tooltips, dyeing and cauldron cleaning ported. Custom component-driven item tints are a known visual gap. |
| Etching table | manual test needed | Block, block entity, menu, screen and recipe flow ported. |
| Album covers and album jukebox | manual test needed | Storage, menu, playback and track selection ported. Downloaded cover images currently use the default item model. |
| Boombox | manual test needed | Playback, menu, player synchronization and held pose ported. |
| Radio and portal radio | manual test needed | Streaming, menus, portal transform and dimension behavior ported. |
| Jukebox minecart | manual test needed | Entity, dispenser/item behavior, renderer and playback ported. |
| Bard villagers and structures | known gap | POI/profession register, but 26.2's data-driven trades and village pools are not populated yet. |
| Vanilla jukebox integration | manual test needed | Playable components, hopper behavior and stop/start hooks ported. |
| Audio decoding and streaming | launch verified | Audio engine loads; JLayer is bundled. Format/playback testing remains. |
| SoundCloud and Bandcamp | manual test needed | Both resolvers initialize and retain upstream request logic. |
| Sound and artwork caches | implemented | Fabric tick/disconnect/reload lifecycle replaces NeoForge events. |
| Client rendering and models | launch verified | Resources parse, boombox models and minecart renderer load. Dynamic artwork and custom component tints remain. |
| Networking | implemented | Typed Fabric payload registration and client/server send paths ported. Needs two-client testing. |
| Configuration | known gap | Upstream defaults are active; persistent user-editable config UI/file is not implemented. |
| Datagen and resources | launch verified | Generated recipes, loot and tags are checked in; 26.2 item definitions added. |
| Dedicated-server startup | launch verified | Entrypoint and registries load without client-class leakage; full world start was stopped at Mojang's EULA gate. |
| Two-client multiplayer | manual test needed | Packet and tracking paths compile; real two-client synchronization has not been exercised. |
| Automated regression suite | known gap | Build and launch smoke checks exist; no GameTest suite yet. |

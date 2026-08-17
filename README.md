# Etched Fabric 26.2

Private Fabric 26.2 port of [Etched 5.1.0](https://github.com/jacksonhardaway/etched).

This repository targets behavioral parity with the upstream NeoForge release. The historical 1.19.2 Fabric implementation is used only as a loader-integration reference; Pollen is not carried forward.

## Status

`alpha.2` is a private integration-test build. It compiles, packages, passes Fabric's
dedicated-server bootstrap, and reaches the Minecraft client main menu with its resource
pack and audio engine loaded. Core blocks, items, menus, components, recipes, networking,
streaming sources, caches, jukebox integration, boombox, radio, album jukebox, and minecart
code are present.

This is not yet a claim of perfect 1:1 parity. Dynamic downloaded album artwork, custom
disc/label tint rendering, bard trades and village-pool injection need further 26.2 work,
and multiplayer gameplay still needs hands-on testing. See [PARITY.md](PARITY.md).

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Fabric API 0.156.0+26.2
- Java 25

## Build

```bash
./gradlew build
```

The distributable (not the `-sources` JAR) is written to `build/libs/`.

## Testing

Install the JAR on both client and server alongside Fabric API. Back up the test world;
this is an alpha port and has not been tested against production saves.

## Licensing

The upstream repository's mixed licensing is retained. This private port is intended only for internal testing. Upstream authors and contributors remain credited in the mod metadata and source history.

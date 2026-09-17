# Etched Fabric 26.3

Private Fabric 26.3 port of [Etched 5.1.0](https://github.com/jacksonhardaway/etched).

This repository targets behavioral parity with the upstream NeoForge release. The historical 1.19.2 Fabric implementation is used only as a loader-integration reference; Pollen is not carried forward.

## Status

`alpha.7` targets Minecraft 26.3. It passes the codec/resource checks, server GameTests,
and client startup/mixin audit. Multiplayer playback still needs hands-on testing.

This is not yet a claim of perfect 1:1 parity. Dynamic downloaded album artwork, custom
disc/label tint rendering, bard trades and village-pool injection need further parity work,
and multiplayer gameplay still needs hands-on testing. See [PARITY.md](PARITY.md).

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5+
- Fabric API 0.160.7+26.3
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

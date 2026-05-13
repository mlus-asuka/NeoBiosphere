# NeoBiosphere

A Biosphere world generation mod for NeoForge 1.21.1.

NeoBiosphere generates large grid-aligned spheres in the Overworld (with an optional upper-hemisphere mode) while preserving vanilla biome sampling behavior. This allows different climate zones and terrain styles to appear naturally across different spheres. You can fine-tune sphere size, spacing, center height, bridge behavior, and more through configuration.

## Highlights

- Sphere-based Overworld generation with a clean grid layout.
- Multi-biome support based on vanilla noise biome sampling.
- Highly configurable parameters: radius, spacing, center Y, sphere block, bridge options, and more.
- Per-sphere variety: each sphere gets a random size and block type from configurable lists, while remaining deterministic across world reloads.
- Optional upper-hemisphere mode for sky-island or challenge-style gameplay.
- Automatic sphere-to-sphere bridges for smoother exploration.
- Compatibility-oriented implementation via Biome Modifier and noise routing integration.

## Requirements

- Minecraft: 1.21.1
- NeoForge: 21.1.216+
- Java: 21

## Installation

1. Install NeoForge for Minecraft 1.21.1.
2. Put the mod jar into your `mods` folder.
3. Launch the game and create a new world.

Using a fresh world is strongly recommended to avoid broken borders with previously generated chunks.

## Configuration

Config file path in development environment: `run/config/NeoBiosphere.toml`

Typical path in normal client/server installations: `<game_dir>/config/NeoBiosphere.toml`

### Main Settings

```toml
[Settings]
radius = 128
min_radius = 128
spacing = 500
center_y = 62
sphere_block = ["minecraft:glass"]
only_upper_hemisphere = false
generate_bridge = true
bridge_block = "minecraft:oak_planks"
bridge_radius = 1
```

- `radius`: Maximum sphere radius.
- `min_radius`: Minimum sphere radius. Set equal to `radius` for fixed-size spheres; set lower to enable per-sphere random sizing.
- `spacing`: Distance between sphere center points on the grid.
- `center_y`: Y level of sphere centers.
- `sphere_block`: List of block IDs used for sphere shells. Each sphere randomly picks one type from the list, deterministically based on its grid position. For example `["minecraft:glass", "minecraft:stone", "minecraft:obsidian"]`.
- `only_upper_hemisphere`: Generate only upper hemispheres when enabled.
- `generate_bridge`: Enable or disable bridges between adjacent spheres.
- `bridge_block`: Block used for bridges.
- `bridge_radius`: Bridge radius (larger values make thicker bridges).

### Tuning Notes

- Overlap behavior depends on `spacing` versus `2 * radius`.
- If `spacing > 2 * radius`, spheres have gaps between them.
- If `spacing = 2 * radius`, spheres are tangent.
- If `spacing < 2 * radius`, spheres overlap.

Suggested presets:

- Classic style: `radius=128, min_radius=128, spacing=500, center_y=62`
- Compact exploration: `radius=160, min_radius=160, spacing=320, center_y=70`
- Challenge mode: `only_upper_hemisphere=true, generate_bridge=true`
- Varied spheres: `radius=180, min_radius=80, sphere_block=["minecraft:glass", "minecraft:stone", "minecraft:obsidian"]`

## Compatibility

NeoBiosphere is designed to support most terrain/ecology mod setups, but world generation is a high-conflict area. Please note:

- Usually compatible with:
  - Mods that mainly add mobs, vegetation, structures, or surface features.
  - Mods that append content through Biome Modifiers.

- Potential conflicts with:
  - Mods or datapacks that directly replace noise settings for `minecraft:overworld`, `minecraft:large_biomes`, or `minecraft:amplified`.
  - Mods that deeply alter Carver, Aquifer, or ChunkGenerator pipelines.

- Recommended approach:
  - Treat NeoBiosphere as the primary world-shape mod.
  - Test in a fresh world before finalizing load order and datapack priority in modpacks.

## Covered Overworld Noise Presets

Current version includes support for these Overworld presets:

- `minecraft:overworld`
- `minecraft:large_biomes`
- `minecraft:amplified`

## FAQ

### 1. Why does my old world look incomplete?

Changing world generation settings does not regenerate already created chunks. A new world is recommended.

### 2. Why are bridges not generating?

Check the following:

- `generate_bridge=true`
- `bridge_radius >= 1`
- `bridge_block` is a valid block ID

### 3. Why are spheres different sizes or using different blocks?

This is expected when `min_radius < radius` or when `sphere_block` contains multiple entries. Each sphere deterministically picks its own size and block type based on its grid coordinates, so reloading the world always produces the same sphere layout.

### 4. Why is the sphere shell block not applied?

Make sure each block ID in the list is valid, for example `minecraft:glass`. Invalid IDs can cause config load failure or fallback behavior. If using a single block type, the list must still be formatted as `["minecraft:glass"]`.

### 5. What should I do if another terrain mod conflicts?

First check whether both mods modify Overworld noise settings or Carver flow. In heavy conflicts, keep only one primary world-shape mod.

## Development and Build

Run in project root:

```bash
# Windows
.\gradlew.bat build

# Linux / macOS
./gradlew build
```

Run development client:

```bash
# Windows
.\gradlew.bat runClient

# Linux / macOS
./gradlew runClient
```

## License

GNU General Public License v3.0

## Feedback

Issue tracker:

- https://github.com/mlus-asuka/NeoBiosphere/issues

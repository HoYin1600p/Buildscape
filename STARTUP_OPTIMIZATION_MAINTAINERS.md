# Buildscape Startup Optimization Notes

This document describes the startup optimizations maintained on the
`main` branch and what contributors must check when adding content.
It is a repository document and is not packaged into the mod jar.

Maintainer: `hoyin1600p`

## Automatically Covered Content

The following behavior is registry- or namespace-driven and normally requires
no optimization-specific changes:

- Bundled JSON models under `assets/buildscape/models` are discovered and
  parsed through the Buildscape model pipeline.
- Buildscape top-level models are included in parallel model baking.
- Blocks registered through `ModBlocks.BLOCKS` are included in eager block-state
  cache initialization.
- Items registered through `ModItems.ITEMS` are included in scoped item setup.
- Vertical slab items ending in `_vertical_slab` receive the optimized vanilla
  creative-tab ordering behavior.
- Model and blockstate JSON files are minified in Gradle's copied build output.
  Source JSON files remain readable and unchanged.
- Startup worker count is derived from
  `Runtime.getRuntime().availableProcessors()`. Do not replace this with a
  machine-specific fixed thread count.

These optimizations only apply while the content remains in the `buildscape`
namespace and uses the established Buildscape registries.

## Render-Layer Exception

Non-solid render layers are assigned by
`client/BuildscapeRenderLayers.java`. Existing naming conventions cover common
glass, leaf, wallpaper, chain, vine, ornament, string-light, and decorative
families.

When adding a translucent, cutout, or cutout-mipped block:

1. Check whether its registry path matches an existing rule in
   `BuildscapeRenderLayers.getRenderType`.
2. If it does not, extend the classifier for the new family or add an exact
   path case.
3. Verify that the rule does not classify unrelated solid blocks.
4. Inspect the block in-world, in the Buildscape creative tab, and in JEI.

A non-solid block that does not match the classifier will use Minecraft's
default solid render layer and may appear opaque, invisible, or visually
broken.

## Custom-Model Exception

Models containing a Forge `"loader"` declaration are deliberately excluded
from parallel JSON parsing. If Buildscape custom geometry is detected during a
resource reload, Buildscape model baking remains sequential for that reload.
This compatibility fallback is intentional.

When adding custom geometry:

1. Keep the Forge loader declaration in the model JSON.
2. Confirm the startup log reports the expected sequential fallback.
3. Test the model's dynamic behavior in-world.
4. Do not force the model through the parallel path without proving that its
   loader and bake operations are thread-safe.

Resource packs that introduce entirely new Buildscape models not present in the
bundled mod archive remain supported by Minecraft's normal sequential loader.

## Compatibility

Buildscape checks for LaunchFaster before enabling overlapping optimizations.
Do not remove this ownership check. Buildscape-scoped work may remain active,
while overlapping global block-state or material-cache work is left to
LaunchFaster when it is installed and enabled.

The eager block-state cache is intentional. Replacing it with lazy gameplay
initialization can reduce the launch metric while introducing first-use
stutters in-world.

## Required Verification

After changing startup code or adding a new non-solid/custom-model block:

1. Run `gradlew build`.
2. Confirm packaged model and blockstate JSON files parse successfully.
3. Launch once with LaunchFaster and JEI disabled and inspect `latest.log`.
4. Check for mixin failures, model fallbacks, missing textures, and block-state
   cache retries.
5. Inspect affected blocks in-world and in the relevant creative tabs.
6. Test once with JEI enabled.
7. If LaunchFaster compatibility changed, test with LaunchFaster enabled as a
   separate run.

Launch timing is noisy. Compare at least six runs per build and use both mean
and median rather than judging a single launch.

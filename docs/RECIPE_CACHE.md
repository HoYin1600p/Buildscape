# Bundled Recipe Cache (`recipes.bscb`)

`src/main/resources/data/buildscape/recipes_pack/recipes.bscb` is a generated
file that is committed on purpose. This document explains why, and what to do
when you change a recipe.

## What it is

Most BuildScape recipes live in the compact pack files next to it
(`crafting.json`, `stonecutting.json`, and so on). Parsing and compiling them
takes noticeable time, so `BuildScapeRecipeLoader` first tries to read the
recipes from `recipes.bscb`, a binary cache of the same recipes
(`BinaryRecipeCache` reads and writes the format).

The cache stores a SHA-256 hash of the pack sources. On every reload the
loader hashes the pack files the resource manager provides and uses the cache
only when the hash matches. On any mismatch or
read problem it falls back to the local cache in
`<game dir>/buildscape/cache`, and then to compiling the JSON sources. A stale
bundled cache therefore never gives wrong recipes, it only loses the speed-up.
Categories with conditional recipes are always compiled at load time, because
their conditions depend on the loaded mods.

## Why it is committed

- **It ships inside the jar.** It is part of the mod's resources, so a player's
  first world load already has a valid cache and does not compile the pack.
- **It is written into the source tree.** The Gradle task
  `syncBundledRecipeCache` runs `RecipeCacheGenerator` and writes the file to
  `src/main/resources/...` (and a copy to `run/buildscape/cache`).
  `processResources` depends on that task, so every build regenerates it. A
  build output that lives in `src/` is easier to review when it is tracked: a
  recipe change shows up as a changed cache in the same commit.
- **It is checked, not trusted.** `./gradlew check` runs
  `verifyBundledRecipeCache`, which regenerates the cache from the sources and
  fails unless the committed file is byte-for-byte identical. It also runs
  `checkRecipePackFormat`. The generator is deterministic, so the committed
  file always matches a clean build.
- **Tests read it.** `RecipeIdMigrationTest` opens the bundled file from the
  classpath.

## Changing recipes

1. Edit the pack JSON. `./gradlew compactizeRecipes` (alias `formatRecipes`)
   rewrites the files in the canonical compact format, and then regenerates
   the cache.
2. Otherwise run `./gradlew syncBundledRecipeCache`, or any build.
3. Commit the changed `recipes.bscb` together with the JSON change. `check`
   fails if you forget.

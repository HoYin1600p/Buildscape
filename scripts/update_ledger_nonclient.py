import csv

updates = {
    'src/main/java/com/kingodogo/buildscape/recipe/ClearShulkerFiltersRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/ClearShulkerFiltersRecipe.java',
        '118x',
        'Ghost item filter clearing recipe ported to common using platform registry services',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/ConfettiConfigureRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/ConfettiConfigureRecipe.java',
        '118x',
        'Confetti burst configuration recipe ported to common using vanilla crafting APIs',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/CustomFireworkStarRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/CustomFireworkStarRecipe.java',
        '118x',
        'Custom firework star crafting recipe ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/InfinitePhoenixFireworkStarRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/InfinitePhoenixFireworkStarRecipe.java',
        '118x',
        'Phoenix firework star recipe ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/ShapedDurabilityRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/ShapedDurabilityRecipe.java',
        '118x',
        'Durability preserving shaped crafting recipe ported without Forge CraftingHelper dependency',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/ShapelessDurabilityRecipe.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/ShapelessDurabilityRecipe.java',
        '118x',
        'Durability preserving shapeless crafting recipe ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/ModRecipeSerializers.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/ModRecipeSerializers.java;common/src/main/java/com/kingodogo/buildscape/registry/IRegistryAdapter.java',
        '118x',
        'Recipe serializers registered via IRegistryAdapter cross-loader service',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java',
        '118x',
        'Custom recipe framework loader ported to common as PreparableReloadListener',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java',
        '118x',
        'Fast binary recipe cache ported to common with safe reflection fallbacks',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java',
        '118x',
        'Dynamic recipe compiler ported to common using platform services',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java',
        '118x',
        'Recipe injection helper into RecipeManager ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/CherryTreeGrower.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/CherryTreeGrower.java',
        '118x',
        'Cherry tree grower ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/CreakingHeartTreeDecorator.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/CreakingHeartTreeDecorator.java',
        '118x',
        'Creaking heart tree decorator ported to common with version-safe unit codec',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveLeafSupport.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveLeafSupport.java',
        '118x',
        'Mangrove leaf support utility ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveLeaveVineDecorator.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveLeaveVineDecorator.java',
        '118x',
        'Mangrove vine tree decorator ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveMossCarpetDecorator.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveMossCarpetDecorator.java',
        '118x',
        'Mangrove moss carpet tree decorator ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangrovePropaguleDecorator.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangrovePropaguleDecorator.java',
        '118x',
        'Mangrove propagule tree decorator ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveRandomSpreadFoliagePlacer.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveRandomSpreadFoliagePlacer.java',
        '118x',
        'Mangrove random spread foliage placer ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveRootDecorator.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveRootDecorator.java',
        '118x',
        'Mangrove root decorator ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MangroveUpwardsBranchingTrunkPlacer.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MangroveUpwardsBranchingTrunkPlacer.java',
        '118x',
        'Mangrove branching trunk placer ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModBiomeModifications.java': (
        'consolidated',
        'common/src/main/resources/data/buildscape/worldgen;common/src/main/java/com/kingodogo/buildscape/worldgen/ModConfiguredFeatures.java',
        '118x',
        'Forge 1.18.2 BiomeLoadingEvent consolidated into data-driven placed features and ModConfiguredFeatures',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModBlockStateProviderTypes.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModBlockStateProviderTypes.java',
        '118x',
        'BlockStateProviderType registered with vanilla registry',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModConfiguredFeatures.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModConfiguredFeatures.java',
        '118x',
        'Configured features ported to common with lazy FeatureHolder and vanilla BuiltinRegistries',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModFoliagePlacerTypes.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModFoliagePlacerTypes.java',
        '118x',
        'FoliagePlacerType registered with vanilla registry',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModPlacementModifiers.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModPlacementModifiers.java',
        '118x',
        'PlacementModifierType registered with vanilla registry',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModTreeDecoratorTypes.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModTreeDecoratorTypes.java',
        '118x',
        'TreeDecoratorType registered with vanilla registry',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/ModTrunkPlacerTypes.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/ModTrunkPlacerTypes.java',
        '118x',
        'TrunkPlacerType registered with vanilla registry',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/MossBlockCeilingPlacement.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/MossBlockCeilingPlacement.java',
        '118x',
        'Moss block ceiling placement modifier ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/PaleOakTreeGrower.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/PaleOakTreeGrower.java',
        '118x',
        'Pale oak tree grower ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/PoplarTreeGrower.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/PoplarTreeGrower.java',
        '118x',
        'Poplar tree grower ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/worldgen/RandomStateProvider.java': (
        'common',
        'common/src/main/java/com/kingodogo/buildscape/worldgen/RandomStateProvider.java',
        '118x',
        'Random state provider ported to common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/data/ModBlockTagsProvider.java': (
        'consolidated',
        'common/src/main/resources/data/buildscape/tags/blocks;common/src/main/resources/data/minecraft/tags/blocks',
        '118x;121x;26x',
        'Dev-time Forge block tag generator outputs consolidated into data-driven JSON tags',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/data/ModDataGen.java': (
        'consolidated',
        'common/src/main/resources/data',
        '118x;121x;26x',
        'Dev-time GatherDataEvent harness consolidated into data-driven JSON assets',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/java/com/kingodogo/buildscape/data/ModRecipeProvider.java': (
        'consolidated',
        'common/src/main/resources/data/buildscape/recipes;common/src/main/resources/data/minecraft/recipes',
        '118x;121x;26x',
        'Dev-time recipe generator outputs consolidated into data-driven JSON recipe files',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/resources/data/buildscape/worldgen/placed_feature/mangrove_checked.json': (
        'common',
        'common/src/main/resources/data/buildscape/worldgen/placed_feature/mangrove_checked.json',
        '118x',
        'Worldgen placed feature JSON present in common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    ),
    'src/main/resources/data/buildscape/worldgen/placed_feature/tall_mangrove_checked.json': (
        'common',
        'common/src/main/resources/data/buildscape/worldgen/placed_feature/tall_mangrove_checked.json',
        '118x',
        'Worldgen placed feature JSON present in common',
        'unrestricted eleven-loader compile matrix BUILD SUCCESSFUL'
    )
}

rows = []
updated = 0
with open('migration/ledger.csv', 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    for row in reader:
        if len(row) >= 2 and row[1] in updates:
            u = updates[row[1]]
            new_row = [row[0], row[1], u[0], u[1], u[2], u[3], u[4]]
            rows.append(new_row)
            updated += 1
        else:
            rows.append(row)

with open('migration/ledger.csv', 'w', encoding='utf-8', newline='') as f:
    writer = csv.writer(f)
    writer.writerows(rows)

print(f'Successfully updated {updated} entries in migration/ledger.csv!')

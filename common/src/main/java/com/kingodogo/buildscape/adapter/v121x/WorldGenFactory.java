package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.List;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import java.util.stream.Stream;

public final class WorldGenFactory {
    private static BlockStateProviderType<RandomStateProvider> randomStateType;
    private static PlacementModifierType<MossBlockCeilingPlacement> mossCeilingType;
    private static TreeDecoratorType<CreakingHeartTreeDecorator> creakingHeartType;
    private static TreeDecoratorType<MangroveLeaveVineDecorator> mangroveLeaveVineType;
    private static TreeDecoratorType<MangroveMossCarpetDecorator> mangroveMossCarpetType;
    private static TreeDecoratorType<MangrovePropaguleDecorator> mangrovePropaguleType;
    private static TreeDecoratorType<MangroveRootDecorator> mangroveRootType;
    private static FoliagePlacerType<MangroveRandomSpreadFoliagePlacer> mangroveRandomSpreadFoliageType;
    private static TrunkPlacerType<MangroveUpwardsBranchingTrunkPlacer> mangroveUpwardsBranchingTrunkType;

    private WorldGenFactory() {}

    public static void register() {
        if (randomStateType != null) return;
        randomStateType = PlatformAdapterBase.safeRegister(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "random_state"),
                new BlockStateProviderType<>(RandomStateProvider.CODEC));
        mossCeilingType = PlatformAdapterBase.safeRegister(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "moss_block_ceiling_placement"),
                () -> MossBlockCeilingPlacement.CODEC);
        creakingHeartType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TREE_DECORATOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "creaking_heart"),
                new TreeDecoratorType<>(CreakingHeartTreeDecorator.CODEC));
        mangroveLeaveVineType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TREE_DECORATOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_leave_vine"),
                new TreeDecoratorType<>(MangroveLeaveVineDecorator.CODEC));
        mangroveMossCarpetType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TREE_DECORATOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_moss_carpet"),
                new TreeDecoratorType<>(MangroveMossCarpetDecorator.CODEC));
        mangrovePropaguleType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TREE_DECORATOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_propagule"),
                new TreeDecoratorType<>(MangrovePropaguleDecorator.CODEC));
        mangroveRootType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TREE_DECORATOR_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_root"),
                new TreeDecoratorType<>(MangroveRootDecorator.CODEC));
        mangroveRandomSpreadFoliageType = PlatformAdapterBase.safeRegister(BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_random_spread"),
                new FoliagePlacerType<>(MangroveRandomSpreadFoliagePlacer.CODEC));
        mangroveUpwardsBranchingTrunkType = PlatformAdapterBase.safeRegister(BuiltInRegistries.TRUNK_PLACER_TYPE,
                ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "mangrove_upwards_branching"),
                new TrunkPlacerType<>(MangroveUpwardsBranchingTrunkPlacer.CODEC));
    }

    public static final class CreakingHeartTreeDecorator extends TreeDecorator {
        public static final CreakingHeartTreeDecorator INSTANCE = new CreakingHeartTreeDecorator();
        public static final MapCodec<CreakingHeartTreeDecorator> CODEC = MapCodec.unit(INSTANCE);
        private CreakingHeartTreeDecorator() {}
        @Override protected TreeDecoratorType<?> type() { return creakingHeartType; }
        @Override public void place(Context context) {
            if (!context.logs().isEmpty()) context.setBlock(context.logs().get(context.logs().size() / 2),
                    com.kingodogo.buildscape.block.ModBlocks.CREAKING_HEART.get().defaultBlockState());
        }
    }

    public static final class MangroveLeaveVineDecorator extends TreeDecorator {
        public static final MapCodec<MangroveLeaveVineDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.floatRange(0, 1).fieldOf("probability").forGetter(value -> value.probability)
        ).apply(instance, MangroveLeaveVineDecorator::new));
        private final float probability;
        public MangroveLeaveVineDecorator(float probability) { this.probability = probability; }
        @Override protected TreeDecoratorType<?> type() { return mangroveLeaveVineType; }
        @Override public void place(Context context) {
            for (BlockPos pos : context.leaves()) if (context.random().nextFloat() < probability) {
                BlockPos below = pos.below();
                if (context.isAir(below)) context.setBlock(below, Blocks.VINE.defaultBlockState());
            }
        }
    }

    public static final class MangroveMossCarpetDecorator extends TreeDecorator {
        public static final MapCodec<MangroveMossCarpetDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.floatRange(0, 1).fieldOf("probability").forGetter(value -> value.probability)
        ).apply(instance, MangroveMossCarpetDecorator::new));
        private final float probability;
        public MangroveMossCarpetDecorator(float probability) { this.probability = probability; }
        @Override protected TreeDecoratorType<?> type() { return mangroveMossCarpetType; }
        @Override public void place(Context context) { placeMoss(context, probability); }
    }

    private static void placeMoss(TreeDecorator.Context context, float probability) {
        if (context.logs().isEmpty()) return;
        BlockPos base = context.logs().stream().min(java.util.Comparator.comparingInt(BlockPos::getY)).orElse(context.logs().get(0));
        java.util.Set<BlockPos> checked = new java.util.HashSet<>();
        for (int x = -10; x <= 10; x++) for (int z = -10; z <= 10; z++) for (int y = -5; y <= 5; y++) {
            BlockPos pos = base.offset(x, y, z);
            if (!checked.add(pos)) continue;
            boolean root = context.level().isStateAtPosition(pos, state -> state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:mangrove_roots").getBlock())
                    || state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:muddy_mangrove_roots").getBlock()));
            BlockPos above = pos.above();
            if (root && context.random().nextFloat() < probability && context.isAir(above))
                context.setBlock(above, com.kingodogo.buildscape.block.ModBlocks.MOSS_OVERLAY.get().defaultBlockState());
        }
    }

    public static final class MangrovePropaguleDecorator extends TreeDecorator {
        public static final MapCodec<MangrovePropaguleDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.floatRange(0, 1).fieldOf("probability").forGetter(value -> value.probability),
                IntProvider.codec(0, 16).fieldOf("exclusion_radius_xz").forGetter(value -> value.exclusionRadiusXZ),
                IntProvider.codec(0, 16).fieldOf("exclusion_radius_y").forGetter(value -> value.exclusionRadiusY),
                com.mojang.serialization.Codec.intRange(0, 16).fieldOf("required_empty_blocks").forGetter(value -> value.requiredEmptyBlocks)
        ).apply(instance, MangrovePropaguleDecorator::new));
        private final float probability;
        private final IntProvider exclusionRadiusXZ, exclusionRadiusY;
        private final int requiredEmptyBlocks;
        public MangrovePropaguleDecorator(float probability, IntProvider exclusionRadiusXZ,
                                          IntProvider exclusionRadiusY, int requiredEmptyBlocks) {
            this.probability = probability;
            this.exclusionRadiusXZ = exclusionRadiusXZ;
            this.exclusionRadiusY = exclusionRadiusY;
            this.requiredEmptyBlocks = requiredEmptyBlocks;
        }
        @Override protected TreeDecoratorType<?> type() { return mangrovePropaguleType; }
        @Override public void place(Context context) {
            com.kingodogo.buildscape.worldgen.MangrovePropaguleLogic.place(context.logs(), context.leaves(), requiredEmptyBlocks,
                    pos -> context.level().isStateAtPosition(pos, state -> state.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock),
                    pos -> context.level().isStateAtPosition(pos, state -> state.isAir() || state.is(Blocks.WATER)),
                    pos -> context.level().isStateAtPosition(pos, state -> state.is(Blocks.WATER)), context::setBlock,
                    Blocks.AIR.defaultBlockState(), context.random()::nextInt,
                    age -> com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:mangrove_propagule").defaultBlockState()
                            .setValue(com.kingodogo.buildscape.block.MangrovePropaguleBlock.AGE, age)
                            .setValue(com.kingodogo.buildscape.block.MangrovePropaguleBlock.HANGING, true));
        }
    }

    public static final class MangroveRootDecorator extends TreeDecorator {
        public static final MapCodec<MangroveRootDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProvider.codec(1, 16).fieldOf("max_root_width").forGetter(value -> value.maxRootWidth),
                IntProvider.codec(1, 16).fieldOf("max_root_length").forGetter(value -> value.maxRootLength),
                com.mojang.serialization.Codec.floatRange(0, 1).fieldOf("random_skew_chance").forGetter(value -> value.randomSkewChance),
                IntProvider.codec(0, 16).optionalFieldOf("trunk_offset_y", ConstantInt.of(0)).forGetter(value -> value.trunkOffsetY)
        ).apply(instance, MangroveRootDecorator::new));
        private final IntProvider maxRootWidth, maxRootLength, trunkOffsetY;
        private final float randomSkewChance;
        public MangroveRootDecorator(IntProvider maxRootWidth, IntProvider maxRootLength, float chance) {
            this(maxRootWidth, maxRootLength, chance, ConstantInt.of(0));
        }
        public MangroveRootDecorator(IntProvider maxRootWidth, IntProvider maxRootLength, float chance, IntProvider trunkOffsetY) {
            this.maxRootWidth = maxRootWidth; this.maxRootLength = maxRootLength;
            this.randomSkewChance = chance; this.trunkOffsetY = trunkOffsetY;
        }
        @Override protected TreeDecoratorType<?> type() { return mangroveRootType; }
        @Override public void place(Context context) { placeRoots(context, randomSkewChance); }
    }

    private static void placeRoots(TreeDecorator.Context context, float chance) {
        com.kingodogo.buildscape.worldgen.MangroveRootLogic.place(context.logs(), chance,
                pos -> context.level().isStateAtPosition(pos, state -> {
                    boolean terrain = state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.STONE)
                            || state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY);
                    boolean tree = state.is(net.minecraft.tags.BlockTags.LOGS)
                            || state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:mangrove_roots").getBlock())
                            || state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:muddy_mangrove_roots").getBlock());
                    return terrain && !tree && state.blocksMotion() && !state.canBeReplaced();
                }),
                pos -> context.level().isStateAtPosition(pos, state -> !state.is(net.minecraft.tags.BlockTags.LOGS)
                        && (state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:mangrove_roots").getBlock())
                        || state.is(com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:muddy_mangrove_roots").getBlock())
                        || state.isAir() || state.is(Blocks.WATER))), context::setBlock,
                () -> com.kingodogo.buildscape.block.BlockDefinition.vanilla("minecraft:mangrove_roots").defaultBlockState()
                        .setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, Direction.Axis.Y),
                context.random()::nextInt, context.random()::nextFloat);
    }

    public static final class RandomStateProvider extends BlockStateProvider {
        public static final MapCodec<RandomStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockState.CODEC.listOf().fieldOf("states").forGetter(provider -> provider.states)
        ).apply(instance, RandomStateProvider::new));
        private final List<BlockState> states;

        public RandomStateProvider(List<BlockState> states) { this.states = states; }

        @Override protected BlockStateProviderType<?> type() { return randomStateType; }

        @Override public BlockState getState(RandomSource random, BlockPos pos) {
            return states.isEmpty() ? Blocks.AIR.defaultBlockState() : states.get(random.nextInt(states.size()));
        }
    }

    public static final class MossBlockCeilingPlacement extends PlacementModifier {
        public static final MossBlockCeilingPlacement INSTANCE = new MossBlockCeilingPlacement();
        public static final MapCodec<MossBlockCeilingPlacement> CODEC = MapCodec.unit(INSTANCE);

        private MossBlockCeilingPlacement() {}

        @Override public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
            WorldGenLevel level = context.getLevel();
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int y = pos.getY(); y < pos.getY() + 64; y++) {
                cursor.set(pos.getX(), y, pos.getZ());
                if (level.getBlockState(cursor).is(Blocks.MOSS_BLOCK)) {
                    BlockPos placePos = cursor.below();
                    if (level.getBlockState(placePos).isAir()
                            && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.DOWN)) {
                        return Stream.of(placePos);
                    }
                }
            }
            return Stream.empty();
        }

        @Override public PlacementModifierType<?> type() { return mossCeilingType; }
    }

    public static final class MangroveRandomSpreadFoliagePlacer extends FoliagePlacer {
        public static final MapCodec<MangroveRandomSpreadFoliagePlacer> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        foliagePlacerParts(instance)
                                .and(IntProvider.codec(0, 16).fieldOf("foliage_height").forGetter(p -> p.foliageHeight))
                                .and(com.mojang.serialization.Codec.intRange(0, 512).fieldOf("leaf_placement_attempts").forGetter(p -> p.leafPlacementAttempts))
                                .apply(instance, MangroveRandomSpreadFoliagePlacer::new));

        private final IntProvider foliageHeight;
        private final int leafPlacementAttempts;

        public MangroveRandomSpreadFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight, int leafPlacementAttempts) {
            super(radius, offset);
            this.foliageHeight = foliageHeight;
            this.leafPlacementAttempts = leafPlacementAttempts;
        }

        @Override protected FoliagePlacerType<?> type() { return mangroveRandomSpreadFoliageType; }

        @Override
        protected void createFoliage(
                net.minecraft.world.level.LevelSimulatedReader level,
                FoliagePlacer.FoliageSetter blockSetter,
                RandomSource random,
                net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration config,
                int maxFreeTreeHeight,
                FoliageAttachment attachment,
                int foliageHeight,
                int foliageRadius,
                int offset
        ) {
            int actualFoliageHeight = this.foliageHeight.sample(random);
            com.kingodogo.buildscape.worldgen.MangrovePlacerLogic.createFoliage(
                    level, blockSetter::set, attachment.pos(), foliageRadius, actualFoliageHeight, leafPlacementAttempts,
                    pos -> config.foliageProvider.getState(random, pos),
                    pos -> config.trunkProvider.getState(random, pos),
                    random::nextDouble, random::nextFloat, list -> java.util.Collections.shuffle(list, new java.util.Random(random.nextLong())));
        }

        @Override
        protected boolean shouldSkipLocation(RandomSource random, int baseHeight, int x, int y, int z, boolean large) {
            return x == 0 && z == 0 && y == 0;
        }

        @Override
        public int foliageHeight(RandomSource random, int height, net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration config) {
            return this.foliageHeight.sample(random);
        }
    }

    public static final class MangroveUpwardsBranchingTrunkPlacer extends TrunkPlacer {
        public static final MapCodec<MangroveUpwardsBranchingTrunkPlacer> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        trunkPlacerParts(instance)
                                .and(IntProvider.codec(0, 24).fieldOf("extra_branch_steps").forGetter(p -> p.extraBranchSteps))
                                .and(com.mojang.serialization.Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(p -> p.placeBranchPerLogProbability))
                                .and(IntProvider.codec(0, 24).fieldOf("extra_branch_length").forGetter(p -> p.extraBranchLength))
                                .apply(instance, MangroveUpwardsBranchingTrunkPlacer::new));

        private final IntProvider extraBranchSteps;
        private final IntProvider extraBranchLength;
        private final float placeBranchPerLogProbability;

        public MangroveUpwardsBranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                                                  IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength) {
            super(baseHeight, heightRandA, heightRandB);
            this.extraBranchSteps = extraBranchSteps;
            this.placeBranchPerLogProbability = placeBranchPerLogProbability;
            this.extraBranchLength = extraBranchLength;
        }

        @Override protected TrunkPlacerType<?> type() { return mangroveUpwardsBranchingTrunkType; }

        @Override
        public java.util.List<FoliagePlacer.FoliageAttachment> placeTrunk(
                net.minecraft.world.level.LevelSimulatedReader level,
                java.util.function.BiConsumer<BlockPos, BlockState> blockSetter,
                RandomSource random,
                int freeTreeHeight,
                BlockPos pos,
                net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration config
        ) {
            java.util.List<FoliagePlacer.FoliageAttachment> attachments = new java.util.ArrayList<>();
            int elevationShift = 2 + random.nextInt(3);
            int extraHeight = random.nextInt(4);
            int branchSteps = this.extraBranchSteps.sample(random);
            int branchLength = this.extraBranchLength.sample(random);
            int branchDir = random.nextInt(4);

            com.kingodogo.buildscape.worldgen.MangrovePlacerLogic.placeTrunk(
                    level, pos, freeTreeHeight, elevationShift, extraHeight,
                    placeBranchPerLogProbability, branchSteps, branchLength, branchDir,
                    random::nextFloat,
                    (lvl, logPos) -> (lvl.isStateAtPosition(logPos, state -> state.isAir() || state.is(Blocks.WATER))
                            && placeLog(lvl, blockSetter, random, logPos, config)),
                    foliagePos -> attachments.add(new FoliagePlacer.FoliageAttachment(foliagePos, 0, false))
            );
            return attachments;
        }
    }

    public static BlockStateProvider createRandomStateProvider(List<BlockState> states) {
        return new RandomStateProvider(states);
    }
    public static TreeDecorator getCreakingHeartTreeDecorator() {
        return CreakingHeartTreeDecorator.INSTANCE;
    }
    public static TreeDecorator createMangroveLeaveVineDecorator(float probability) {
        return new MangroveLeaveVineDecorator(probability);
    }
    public static TreeDecorator createMangroveMossCarpetDecorator(float probability) {
        return new MangroveMossCarpetDecorator(probability);
    }
    public static TreeDecorator createMangrovePropaguleDecorator(float probability, IntProvider p1, IntProvider p2, int requiredEmptyBlocks) {
        return new MangrovePropaguleDecorator(probability, p1, p2, requiredEmptyBlocks);
    }
    public static TreeDecorator createMangroveRootDecorator(IntProvider p1, IntProvider p2, float probability, IntProvider p3) {
        return new MangroveRootDecorator(p1, p2, probability, p3);
    }
    public static TrunkPlacer createMangroveUpwardsBranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength) {
        return new MangroveUpwardsBranchingTrunkPlacer(baseHeight, heightRandA, heightRandB, extraBranchSteps, placeBranchPerLogProbability, extraBranchLength);
    }
    public static FoliagePlacer createMangroveRandomSpreadFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight, int leafPlacementAttempts) {
        return new MangroveRandomSpreadFoliagePlacer(radius, offset, foliageHeight, leafPlacementAttempts);
    }
    public static ConfiguredFeature<?, ?> createPatchFeature(
            BlockStateProvider stateProvider,
            int tries,
            int xzSpread,
            int ySpread
    ) {
        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(stateProvider)
        );
        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesBlocks(
                                        List.of(Blocks.AIR)
                                )
                        )
                )
        );
        return new ConfiguredFeature<>(
                Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(tries, xzSpread, ySpread, Holder.direct(placedFeature))
        );
    }
}

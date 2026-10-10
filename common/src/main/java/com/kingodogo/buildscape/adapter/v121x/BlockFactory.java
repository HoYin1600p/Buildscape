package com.kingodogo.buildscape.adapter.v121x;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import com.kingodogo.buildscape.platform.Services;

import java.util.List;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.kingodogo.buildscape.block.AshenKingPillarBlock;
import com.kingodogo.buildscape.block.BigBookBlock;
import com.kingodogo.buildscape.block.BigCandleBlock;
import com.kingodogo.buildscape.block.BigOrnamentBlock;
import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.BoneDiceBlock;
import com.kingodogo.buildscape.block.BuildersWorkbenchBlock;
import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.ColoredMossBlock;
import com.kingodogo.buildscape.block.ColoredSporeBlossomBlock;
import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import com.kingodogo.buildscape.block.CommonBlockProperties;
import com.kingodogo.buildscape.block.CopperBulbBlock;
import com.kingodogo.buildscape.block.CopperOxidationHandler;
import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.block.DecoratedPotBlock;
import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.block.FallingSandBlock;
import com.kingodogo.buildscape.block.FestiveLampBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FroglightBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.GlassJarBlock;
import com.kingodogo.buildscape.block.GlazedGlassBlock;
import com.kingodogo.buildscape.block.GlowLightsBlock;
import com.kingodogo.buildscape.block.GrassSlabBlock;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.block.HayBaleSlabBlock;
import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.block.ICommonRemoval;
import com.kingodogo.buildscape.block.IcicleBlock;
import com.kingodogo.buildscape.block.IcicleCauldronBlock;
import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.LeafLayersBlock;
import com.kingodogo.buildscape.block.LeafLitterBlock;
import com.kingodogo.buildscape.block.MangroveLeavesBlock;
import com.kingodogo.buildscape.block.MangrovePropaguleBlock;
import com.kingodogo.buildscape.block.MangroveRootsBlock;
import com.kingodogo.buildscape.block.PackedIcicleBlock;
import com.kingodogo.buildscape.block.PetalBlock;
import com.kingodogo.buildscape.block.PointedIcicleBlock;
import com.kingodogo.buildscape.block.ResinClumpBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyGlassBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.block.ModBushBlock;
import com.kingodogo.buildscape.block.ModIronBarsBlock;
import com.kingodogo.buildscape.block.ModLadderBlock;
import com.kingodogo.buildscape.block.ModLayerBlock;
import com.kingodogo.buildscape.block.MossLayersBlock;
import com.kingodogo.buildscape.block.ColoredMossLayersBlock;
import com.kingodogo.buildscape.block.LogSlabBlock;
import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModWallBlock;
import com.kingodogo.buildscape.block.MonetFlowerBlock;
import com.kingodogo.buildscape.block.MossOverlayBlock;
import com.kingodogo.buildscape.block.MudBlock;
import com.kingodogo.buildscape.block.MudSlabBlock;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.block.MushroomShelvesBlock;
import com.kingodogo.buildscape.block.MulticolorGlowLightsBlock;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.block.PipeBlock;
import com.kingodogo.buildscape.block.RoseVinesBlock;
import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.SnowOverlayBlock;
import com.kingodogo.buildscape.block.SnowyBushBlock;
import com.kingodogo.buildscape.block.SnowyFernBlock;
import com.kingodogo.buildscape.block.SnowyGrassBlock;
import com.kingodogo.buildscape.block.SnowyLargeFernBlock;
import com.kingodogo.buildscape.block.SnowyLeavesBlock;
import com.kingodogo.buildscape.block.SnowyShortGrassBlock;
import com.kingodogo.buildscape.block.SnowyTallGrassBlock;
import com.kingodogo.buildscape.block.SoftFabricBlock;
import com.kingodogo.buildscape.block.SpoolBlock;
import com.kingodogo.buildscape.block.StarBlock;
import com.kingodogo.buildscape.block.SteelBoltBlock;
import com.kingodogo.buildscape.block.StrawBedBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import com.kingodogo.buildscape.block.TrappedDecoratedPotBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WallpaperFlatBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.block.WeatheringBarsBlock;
import com.kingodogo.buildscape.block.WeatheringBoltBlock;
import com.kingodogo.buildscape.block.WeatheringLargeChainBlock;
import com.kingodogo.buildscape.block.WeatheringBlockLogic;
import com.kingodogo.buildscape.block.WildflowersBlock;
import com.kingodogo.buildscape.block.WoolLayersBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.function.Supplier;
public class BlockFactory implements IBlockFactory {

    private static ItemInteractionResult toItemInteractionResult(InteractionResult result) {
        if (result == InteractionResult.SUCCESS) return ItemInteractionResult.SUCCESS;
        if (result == InteractionResult.CONSUME) return ItemInteractionResult.CONSUME;
        if (result == InteractionResult.FAIL) return ItemInteractionResult.FAIL;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Block createBlock(BlockDefinition def) {
        BlockBehaviour.Properties props = buildProperties(def);

        if (def.isLargeChain()) {
            return new com.kingodogo.buildscape.block.LargeChainBlock(props) {
                @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isClimbableChain()) {
            return new com.kingodogo.buildscape.block.ClimbableChainBlock(props) {
                @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isCopperBulb()) {
            return new CopperBulbBlock(props, def.copperBulbLightLevel()) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onRandomTick(state, level, pos); }
                @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
            };
        } else if (def.isWaterloggableGrate()) {
            return new WaterloggableGrateBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
            };
        } else if (def.isWeatheringBars()) {
            return new WeatheringBarsBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBolt()) {
            return new WeatheringBoltBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringLargeChain()) {
            return new WeatheringLargeChainBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBlock()) {
            return new Block(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringButton()) {
            return new ButtonBlock(BlockSetType.STONE, 20, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringPressurePlate()) {
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(WeatheringBlockLogic.PRESSURE_PLATE_MAX_WEIGHT, BlockSetType.OAK, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringSlab()) {
            return new net.minecraft.world.level.block.SlabBlock(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringStair()) {
            return new ModStairBlock(getBaseState(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isStair()) {
            return new ModStairBlock(getBaseState(def), props) {
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && super.propagatesSkylightDown(state, level, pos); }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props);
        } else if (def.isGrassSlab()) {
            props.randomTicks();
            Supplier<Block> dirtSlabSupplier = resolveBlockSupplier("dirt_slab");
            class V121xGrassSlabBlock extends GrassSlabBlock implements BonemealableBlock {
                public V121xGrassSlabBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction == Direction.UP) {
                        state = state.setValue(SNOWY, isSnowySetting(level, pos));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }

                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    if (!canBeGrass(state, level, pos)) {
                        Block dirtSlab = dirtSlabSupplier.get();
                        if (dirtSlab != null) {
                            level.setBlockAndUpdate(pos, dirtSlab.defaultBlockState()
                                    .setValue(TYPE, state.getValue(TYPE))
                                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
                        }
                        return;
                    }
                    if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                        for (int i = 0; i < 4; ++i) {
                            BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                            BlockState targetState = level.getBlockState(targetPos);
                            Block dirtSlab = dirtSlabSupplier.get();
                            if (dirtSlab != null && targetState.is(dirtSlab) && canPropagate(state, level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, this.defaultBlockState()
                                        .setValue(TYPE, targetState.getValue(TYPE))
                                        .setValue(WATERLOGGED, targetState.getValue(WATERLOGGED))
                                        .setValue(SNOWY, isSnowySetting(level, targetPos)));
                            } else if (targetState.is(Blocks.DIRT) && canPropagate(Blocks.GRASS_BLOCK.defaultBlockState(), level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            }
                        }
                    }
                }

                private boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    BlockState aboveState = level.getBlockState(abovePos);
                    if (aboveState.is(Blocks.SNOW) && aboveState.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS) == 1) {
                        return true;
                    }
                    if (aboveState.getFluidState().getAmount() == 8) {
                        return false;
                    }
                    int lightLevel = net.minecraft.world.level.lighting.LightEngine.getLightBlockInto(
                            level,
                            state,
                            pos,
                            aboveState,
                            abovePos,
                            Direction.UP,
                            aboveState.getLightBlock(level, abovePos)
                    );
                    return lightLevel < level.getMaxLightLevel();
                }

                private boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    if (stack.getItem() instanceof net.minecraft.world.item.BoneMealItem) {
                        if (isValidBonemealTarget(level, pos, state)) {
                            if (level instanceof ServerLevel serverLevel) {
                                if (isBonemealSuccess(level, serverLevel.getRandom(), pos, state)) {
                                    performBonemeal(serverLevel, serverLevel.getRandom(), pos, state);
                                    if (!player.getAbilities().instabuild) {
                                        stack.shrink(1);
                                    }
                                    level.levelEvent(2005, pos, 0);
                                }
                            }
                            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.BONE_MEAL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                            return ItemInteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return level.getBlockState(pos.above()).isAir();
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    BlockPos abovePos = pos.above();
                    Supplier<Block> snowyShortGrassSupplier = resolveBlockSupplier("snowy_short_grass");
                    BlockState foliage = Blocks.SHORT_GRASS.defaultBlockState();
                    if (random.nextInt(8) == 0 && snowyShortGrassSupplier != null && snowyShortGrassSupplier.get() != null) {
                        foliage = snowyShortGrassSupplier.get().defaultBlockState();
                    }
                    if (foliage.canSurvive(level, abovePos)) {
                        level.setBlock(abovePos, foliage, 3);
                    }
                }
            }
            return new V121xGrassSlabBlock(props);
        } else if (def.isHayBaleSlab()) {
            return new HayBaleSlabBlock(props) {
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    entity.causeFallDamage(fallDistance, 0.2F, level.damageSources().fall());
                }
            };
        } else if (def.isLogSlab()) {
            return new LogSlabBlock(getBaseBlock(def), props) {
                @Override public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isSlab()) {
            return new ModSlabBlock(getBaseBlock(def), props) {
                @Override public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isMudSlab()) {
            return new MudSlabBlock(props);
        } else if (def.isMud()) {
            return new MudBlock(props);
        } else if (def.isMossOverlay()) {
            return new MossOverlayBlock(props) {
                @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isSnowOverlay()) {
            return new SnowOverlayBlock(props) {
                @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isWall()) {
            return new ModWallBlock(props);
        } else if (def.isFalling()) {
            return new FallingSandBlock(props) {
                @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onScheduledTick(state, level, pos); }
            };
        } else if (def.isCopperDoor()) {
            return new DoorBlock(BlockSetType.COPPER, props) {};
        } else if (def.isDoor()) {
            return new DoorBlock(BlockSetType.OAK, props) {};
        } else if (def.isCopperTrapdoor()) {
            return new TrapDoorBlock(BlockSetType.COPPER, props) {};
        } else if (def.isTrapdoor()) {
            return new TrapDoorBlock(BlockSetType.OAK, props) {};
        } else if (def.isButton()) {
            return new ButtonBlock(BlockSetType.OAK, 30, props) {};
        } else if (def.isPressurePlate()) {
            return new PressurePlateBlock(BlockSetType.OAK, props) {};
        } else if (def.isMushroomShelves()) {
            return new MushroomShelvesBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(MushroomShelvesBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
            };
        } else if (def.isShelf()) {
            return new ShelfBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(ShelfBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                        this.updateNeighborsAfterPoweringDown(level, pos, state);
                    }
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isWorkbench()) {
            return new BuildersWorkbenchBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isGlassJar()) {
            return new GlassJarBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isSmokeVent()) {
            return new SmokeVentBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public boolean hasAnalogOutputSignal(BlockState state) {
                    return true;
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isMuff()) {
            return new MuffBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isFenceGate()) {
            return new FenceGateBlock(WoodType.OAK, props);
        } else if (def.isFence()) {
            return new FenceBlock(props);
        } else if (def.isIronBars()) {
            return new ModIronBarsBlock(props);
        } else if (def.isLadder()) {
            return new ModLadderBlock(props);
        } else if (def.isLantern()) {
            return new LanternBlock(props);
        } else if (def.isLeafHedge()) {
            return new LeafHedgeBlock(props) {
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(state, level, pos, entity);
                    super.entityInside(state, level, pos, entity);
                }
            };
        } else if (def.isMangroveLeaves()) {
            class V121xMangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
                public V121xMangroveLeavesBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return MangroveLeavesBlock.canGrowPropagule(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    MangroveLeavesBlock.growPropagule(level, pos);
                }
            }
            return new V121xMangroveLeavesBlock(props);
        } else if (def.isMangrovePropagule()) {
            class V121xMangrovePropaguleBlock extends MangrovePropaguleBlock implements BonemealableBlock {
                public V121xMangrovePropaguleBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return canBonemeal(level, pos, state);
                }
                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealEffect(level, pos, state);
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xMangrovePropaguleBlock(props);
        } else if (def.isMangroveRoots()) {
            return new MangroveRootsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
            };
        } else if (def.isLeaves()) {
            return new LeavesBlock(props);
        } else if (def.isSnowyLeaves()) {
            return new LeavesBlock(props) {
                @Override
                public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
                    return Shapes.empty();
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
                    return 1.0F;
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isColoredMossLayers()) {
            Supplier<Block> fullBlock = resolveBlockSupplier(def.getId().replace("_layers", "_block"));
            class V121xColoredMossLayersBlock extends ColoredMossLayersBlock {
                public V121xColoredMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties, fullBlock);
                }

                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    return super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V121xColoredMossLayersBlock(props);
        } else if (def.isMossLayers()) {
            class V121xMossLayersBlock extends MossLayersBlock {
                public V121xMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    return super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V121xMossLayersBlock(props);
        } else if (def.isWoolLayers()) {
            String color = def.getId().replace("_carpet_layers", "").replace("_layered_wool_layers", "");
            Supplier<Item> carpetSupplier = resolveItemSupplier(color + "_carpet_layers");
            return new WoolLayersBlock(props, color, carpetSupplier) {
                @Override
                protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(state);
                }
                @Override
                public MapCodec<net.minecraft.world.level.block.SnowLayerBlock> codec() {
                    return null;
                }
            };
        } else if (def.isLeafLayers()) {
            return new LeafLayersBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isLeafLitter()) {
            class V121xLeafLitterBlock extends LeafLitterBlock implements BonemealableBlock {
                public V121xLeafLitterBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }

                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealEffect(level, pos, state);
                }

                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xLeafLitterBlock(props);
        } else if (def.isLayer()) {
            return new ModLayerBlock(props);
        } else if (def.isAshenKingPillar()) {
            return new AshenKingPillarBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
                                if (id != null) {
                                    tag.putString("ITEM", id.toString());
                                }
                            }
                            String pattern = pillarBE.getParticlePattern();
                            if (pattern != null && !pattern.isEmpty()) {
                                tag.putString("PATTERN", pattern);
                            }
                        });
                    }
                    return stack;
                }
            };
        } else if (def.isPillar()) {
            return new PillarBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(PillarBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
                                if (id != null) {
                                    tag.putString("ITEM", id.toString());
                                }
                            }
                            String pattern = pillarBE.getParticlePattern();
                            if (pattern != null && !pattern.isEmpty()) {
                                tag.putString("PATTERN", pattern);
                            }
                        });
                    }
                    return stack;
                }
            };
        } else if (def.isHollowLog()) {
            return new HollowLogBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerDestroy(level, pos, state, player);
                    return super.playerWillDestroy(level, pos, state, player);
                }
            };
        } else if (def.isHollowPipe()) {
            return new HollowPipeBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerDestroy(level, pos, state, player);
                    return super.playerWillDestroy(level, pos, state, player);
                }
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(level, pos, state);
                }
            };
        } else if (def.isPipe()) {
            return new PipeBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
            };
        } else if (def.isRoseVines()) {
            return new RoseVinesBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return state;
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onRandomTick(state, level, pos);
                }
                public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
                    return canClimb(level, pos);
                }
            };
        } else if (def.isFroglight()) {
            return new FroglightBlock(props);
        } else if (def.isRotatedPillar() || def.isLog() || def.isWood() || def.isCautionBlock() || def.isBambooBlock()) {
            return new RotatedPillarBlock(props);
        } else if (def.isTrappedDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new TrappedDecoratedPotBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new DecoratedPotBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isFestiveStocking()) {
            return new FestiveStockingBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return null;
                }
            };
        } else if (def.isCascadeNoMist()) {
            return new CascadeBlockNoMist(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult result = onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                    return result != InteractionResult.PASS ? result : super.useWithoutItem(state, level, pos, player, hit);
                }
            };
        } else if (def.isCascade()) {
            return new CascadeBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isGlazedGlass()) {
            return new GlazedGlassBlock(props) {
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return getBeaconColor(state, level, pos, beaconPos);
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                protected boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                protected MapCodec<? extends HalfTransparentBlock> codec() {
                    return null;
                }
            };
        } else if (def.isIcicleCauldron()) {
            return new IcicleCauldronBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isIcicleBlock()) {
            props.friction(0.989F);
            return new IcicleBlock(props) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isPackedIcicleBlock()) {
            props.friction(0.989F);
            return new PackedIcicleBlock(props) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isBoneDice()) {
            return new BoneDiceBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isStar()) {
            return new StarBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                    super.entityInside(state, level, pos, entity);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops();
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.useWithoutItem(state, level, pos, player, hit);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            };
        } else if (def.isCushion()) {
            return new CushionBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    if (entity.isSuppressingBounce()) super.fallOn(level, state, pos, entity, fallDistance);
                    else entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
                }
                @Override
                public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
                    if (entity.isSuppressingBounce()) super.updateEntityAfterFallOn(level, entity);
                    else bounceUp(entity);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.useWithoutItem(state, level, pos, player, hit);
                }
            };
        } else if (def.isBigBook()) {
            return new BigBookBlock(props) {
                @Override
                protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
                    return null;
                }
            };
        } else if (def.isBigCandle()) {
            props.lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? BigCandleBlock.LIGHT_LEVEL : 0);
            return new BigCandleBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
                @Override
                protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
                    return 1.0F;
                }
                @Override
                protected boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected boolean isRandomlyTicking(BlockState state) {
                    return state.getValue(LIT);
                }
                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onRandomTick(state, level, pos, random::nextInt, random::nextFloat);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextFloat, random::nextInt);
                }
            };
        } else if (def.isSoftFabric()) {
            return new SoftFabricBlock(props);
        } else if (def.isSpool()) {
            return new SpoolBlock(props);
        } else if (def.isSteelBolt()) {
            return new SteelBoltBlock(props) {
                @Override
                protected MapCodec<? extends Block> codec() {
                    return null;
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    return onUpdateShape(super.updateShape(state, direction, adjacentState, level, pos, adjacentPos), direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(state);
                }
            };
        } else if (def.isFestiveLamp()) {
            return new FestiveLampBlock(props);
        } else if (def.isBigOrnament()) {
            boolean tinted = def.isBigTintedOrnament();
            props.lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? (tinted ? 4 : 15) : 0);
            if (tinted) props.dynamicShape();
            return new BigOrnamentBlock(props, tinted) {
                @Override
                protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return isTinted() ? 0 : super.getLightBlock(state, level, pos);
                }
                @Override
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return getBeaconColor(state, level, pos, beaconPos);
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isOrnament()) {
            boolean tinted = def.isTintedOrnament();
            props.lightLevel(state -> state.getValue(OrnamentBlock.LIT) ? (tinted ? 4 : 15) : 0);
            if (tinted) props.dynamicShape();
            return new OrnamentBlock(props, tinted) {
                @Override protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTinted() ? 0 : super.getLightBlock(state, level, pos); }
                @Override public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return getBeaconColor(state, level, pos, beaconPos); }
                @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); if (!canSurvive(state, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit)); }
                @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit); }
            };
        } else if (def.isStringLight()) {
            return new StringLightBlock(props) {
                @Override
                protected MapCodec<? extends Block> codec() {
                    return null;
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isExperienceCauldron()) {
            return new LayeredCauldronBlock(Biome.Precipitation.NONE, CauldronInteraction.EMPTY, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    int levelVal = state.getValue(LEVEL);
                    if (levelVal > 0 && random.nextInt(25 - levelVal * 5) == 0) {
                        double fillHeight = 0.4375D + (double) levelVal * 0.1875D;
                        double x = (double) pos.getX() + 0.2D + random.nextDouble() * 0.6D;
                        double y = (double) pos.getY() + fillHeight;
                        double z = (double) pos.getZ() + 0.2D + random.nextDouble() * 0.6D;
                        if (ModParticles.XP_PARTICLE.isPresent()) {
                            level.addParticle(ModParticles.XP_PARTICLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
                        } else {
                            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isMulticolorGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new MulticolorGlowLightsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                @SuppressWarnings("unchecked")
                public MapCodec<VineBlock> codec() {
                    return null;
                }
            };
        } else if (def.isGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new GlowLightsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                @SuppressWarnings("unchecked")
                public MapCodec<VineBlock> codec() {
                    return null;
                }
            };
        } else if (def.isFrostRose()) {
            return new FrostRoseBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (!canSurvive(adjusted, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            };
        } else if (def.isStrawBed()) {
            return new StrawBedBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.useWithoutItem(state, level, pos, player, hit);
                }
            };
        } else if (def.isHangingMoss()) {
            props.randomTicks();
            class V121xHangingMossBlock extends HangingMossBlock implements BonemealableBlock {
                public V121xHangingMossBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    if (direction == Direction.UP && !state.canSurvive(level, pos)) {
                        return Blocks.AIR.defaultBlockState();
                    }
                    if (direction == Direction.DOWN) {
                        return state.setValue(TIP, !adjacentState.is(this));
                    }
                    return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public boolean isRandomlyTicking(BlockState state) {
                    return state.getValue(TIP) && !state.getValue(SHEARED);
                }

                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    if (state.getValue(TIP) && !state.getValue(SHEARED)) {
                        if (random.nextInt(10) == 0) {
                            BlockPos growPos = pos.below();
                            BlockState growState = level.getBlockState(growPos);
                            if (growState.isAir() || growState.canBeReplaced()) {
                                level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                                level.setBlock(pos, state.setValue(TIP, false), 3);
                            }
                        }
                    }
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    if (stack.getItem() instanceof net.minecraft.world.item.ShearsItem) {
                        BlockPos tipPos = findTip(level, pos, this);
                        BlockState tipState = level.getBlockState(tipPos);
                        if (tipState.hasProperty(SHEARED) && !tipState.getValue(SHEARED)) {
                            if (!level.isClientSide) {
                                level.setBlockAndUpdate(tipPos, tipState.setValue(SHEARED, true));
                                level.playSound(null, tipPos, net.minecraft.sounds.SoundEvents.GROWING_PLANT_CROP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.SHEAR, tipPos);
                                stack.hurtAndBreak(1, player, net.minecraft.world.entity.LivingEntity.getSlotForHand(hand));
                            }
                            return ItemInteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return false;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    return growState.isAir() || growState.canBeReplaced();
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    return !(tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED));
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    if (growState.isAir() || growState.canBeReplaced()) {
                        level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                        level.setBlock(tipPos, tipState.setValue(TIP, false), 3);
                    }
                }
            }
            return new V121xHangingMossBlock(props);
        } else if (def.isClover()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V121xCloverBlock extends CloverBlock implements BonemealableBlock {
                public V121xCloverBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? toItemInteractionResult(result) : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    growClover(level, pos, state);
                }

                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xCloverBlock(props);
        } else if (def.isPetal()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V121xPetalBlock extends PetalBlock implements BonemealableBlock {
                public V121xPetalBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? toItemInteractionResult(result) : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    growPetal(level, pos, state);
                }

                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xPetalBlock(props);
        } else if (def.isWildflowers()) {
            props.sound(SoundType.GRASS).noCollission();
            class V121xWildflowersBlock extends WildflowersBlock implements BonemealableBlock {
                public V121xWildflowersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return toItemInteractionResult(onInteract(level, pos, state, player, hand, hit));
                }

                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    growFlowers(level, pos, state);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(state);
                }

                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xWildflowersBlock(props);
        } else if (def.isColoredMoss()) {
            Supplier<Block> carpet = resolveBlockSupplier(def.getId().replace("_block", "_carpet"));
            Supplier<Block> overlay = resolveBlockSupplier(def.getId().replace("_block", "_overlay"));
            Supplier<Block> layers = resolveBlockSupplier(def.getId().replace("_block", "_layers"));
            Supplier<Block> sapling = resolveBlockSupplier(def.getParentBlockId());
            class V121xColoredMossBlock extends ColoredMossBlock implements BonemealableBlock {
                public V121xColoredMossBlock(BlockBehaviour.Properties properties) {
                    super(properties, carpet, overlay, layers, sapling, null);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? toItemInteractionResult(result) : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    growMoss(level, pos, state, random::nextInt);
                }

                @Override
                protected MapCodec<? extends Block> codec() {
                    return null;
                }
            }
            return new V121xColoredMossBlock(props);
        } else if (def.isColoredSporeBlossom()) {
            int colorRGB = getSporeBlossomColor(def.getId());
            class V121xColoredSporeBlossomBlock extends ColoredSporeBlossomBlock {
                public V121xColoredSporeBlossomBlock(BlockBehaviour.Properties properties) {
                    super(properties, colorRGB);
                }

                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }

                @Override
                public MapCodec<SporeBlossomBlock> codec() {
                    return null;
                }
            }
            return new V121xColoredSporeBlossomBlock(props);
        } else if (def.isCreakingHeart()) {
            Supplier<Block> resinClumpSupplier = resolveBlockSupplier("resin_clump");
            class V121xCreakingHeartBlock extends CreakingHeartBlock {
                public V121xCreakingHeartBlock(BlockBehaviour.Properties properties) {
                    super(properties, resinClumpSupplier);
                }

                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, InteractionHand.MAIN_HAND);
                    return result != InteractionResult.PASS ? result : super.useWithoutItem(state, level, pos, player, hit);
                }

                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? toItemInteractionResult(result) : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onScheduledTick(state, level, pos);
                }

                @Override
                public MapCodec<RotatedPillarBlock> codec() {
                    return null;
                }
            }
            return new V121xCreakingHeartBlock(props);
        } else if (def.isMonetFlower()) {
            class V121xMonetFlowerBlock extends MonetFlowerBlock implements BonemealableBlock {
                public V121xMonetFlowerBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealAction(level, pos, this);
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public com.mojang.serialization.MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xMonetFlowerBlock(props);
        } else if (def.isSnowyBush()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyBushBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            };
        } else if (def.isSnowyFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V121xSnowyFernBlock extends SnowyFernBlock implements BonemealableBlock {
                public V121xSnowyFernBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyLargeFern);
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xSnowyFernBlock(props);
        } else if (def.isSnowyShortGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            class V121xSnowyShortGrassBlock extends SnowyShortGrassBlock implements BonemealableBlock {
                public V121xSnowyShortGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyTallGrass);
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            }
            return new V121xSnowyShortGrassBlock(props);
        } else if (def.isSnowyLargeFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyLargeFernBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                public MapCodec<? extends net.minecraft.world.level.block.DoublePlantBlock> codec() {
                    return null;
                }
            };
        } else if (def.isSnowyTallGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyTallGrassBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
                @Override
                public MapCodec<? extends net.minecraft.world.level.block.DoublePlantBlock> codec() {
                    return null;
                }
            };
        } else if (def.isSnowyGrass()) {
            Supplier<Block> dirtSlab = resolveBlockSupplier("dirt_slab");
            Supplier<Block> snowyGrassSlab = resolveBlockSupplier("snowy_grass_block_slab");
            Supplier<Block> snowyShortGrass = resolveBlockSupplier("snowy_short_grass");
            Supplier<Block> snowyBush = resolveBlockSupplier("snowy_bush");
            Supplier<Block> snowyFern = resolveBlockSupplier("snowy_fern");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V121xSnowyGrassBlock extends SnowyGrassBlock implements BonemealableBlock {
                public V121xSnowyGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowyShortGrass, snowyBush, snowyFern, snowyTallGrass, snowyLargeFern);
                }

                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    if (!canBeGrass(state, level, pos)) {
                        level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                        return;
                    }
                    if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                        BlockState snowyGrassState = this.defaultBlockState();
                        for (int i = 0; i < 4; ++i) {
                            BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                            BlockState targetState = level.getBlockState(targetPos);
                            Block dirtSlabBlock = dirtSlab != null ? dirtSlab.get() : null;
                            Block snowyGrassSlabBlock = snowyGrassSlab != null ? snowyGrassSlab.get() : null;
                            if (dirtSlabBlock != null && snowyGrassSlabBlock != null && targetState.is(dirtSlabBlock) && canPropagate(snowyGrassState, level, targetPos)) {
                                BlockState grassSlabState = snowyGrassSlabBlock.defaultBlockState()
                                        .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, targetState.getValue(net.minecraft.world.level.block.SlabBlock.TYPE))
                                        .setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, targetState.getValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED));
                                level.setBlockAndUpdate(targetPos, grassSlabState);
                            } else if (targetState.is(Blocks.DIRT) && canPropagate(snowyGrassState, level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, snowyGrassState);
                            }
                        }
                    }
                }

                private boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    BlockState aboveState = level.getBlockState(abovePos);
                    if (aboveState.is(Blocks.SNOW) && aboveState.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS) == 1) {
                        return true;
                    }
                    if (aboveState.getFluidState().getAmount() == 8) {
                        return false;
                    }
                    int lightLevel = net.minecraft.world.level.lighting.LightEngine.getLightBlockInto(
                            level,
                            state,
                            pos,
                            aboveState,
                            abovePos,
                            Direction.UP,
                            aboveState.getLightBlock(level, abovePos)
                    );
                    return lightLevel < level.getMaxLightLevel();
                }

                private boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
                }

                @Override
                public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
                    return isValidBonemeal(level, pos);
                }

                @Override
                public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
                    performBonemealFoliage(level, pos);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }

                @Override
                protected MapCodec<? extends Block> codec() {
                    return null;
                }
            }
            return new V121xSnowyGrassBlock(props);
        } else if (def.isExperienceFluid()) {
            Supplier<FlowingFluid> fluidSupplier = () -> resolveFlowingFluid(def.getParentBlockId());
            return new ExperienceFluidBlock(fluidSupplier, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    super.animateTick(state, level, pos, random);
                    if (random.nextInt(30) == 0) {
                        spawnXpParticle(level, pos, random.nextDouble(), random.nextDouble());
                    }
                }
            };
        } else if (def.isSilkTouchOnlyGlass()) {
            return new SilkTouchOnlyGlassBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res.consumesAction()) return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return state.getMapColor(level, pos).col;
                }
            };
        } else if (def.isSilkTouchOnlyPane()) {
            return new SilkTouchOnlyPaneBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res.consumesAction()) return ItemInteractionResult.sidedSuccess(level.isClientSide());
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return state.getMapColor(level, pos).col;
                }
            };
        } else if (def.isResinClump()) {
            props.lightLevel(state -> 7);
            return new ResinClumpBlock(props) {
                @Override
                public net.minecraft.world.level.block.MultifaceSpreader getSpreader() {
                    return new net.minecraft.world.level.block.MultifaceSpreader(this);
                }
                @Override
                protected MapCodec<? extends net.minecraft.world.level.block.MultifaceBlock> codec() {
                    return null;
                }
            };
        } else if (def.isWallpaperFlat()) {
            return new WallpaperFlatBlock(props) {
                @Override
                public net.minecraft.world.level.block.MultifaceSpreader getSpreader() {
                    return new net.minecraft.world.level.block.MultifaceSpreader(this);
                }
                @Override
                protected MapCodec<? extends net.minecraft.world.level.block.MultifaceBlock> codec() {
                    return null;
                }
            };
        } else if (def.isPointedIcicle()) {
            return new PointedIcicleBlock(props) {
                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onIcicleTick(state, level, pos);
                }
                @Override
                protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
                    onNeighborUpdate(state, level, pos, neighborBlock, neighborPos, isMoving);
                }
                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onRandomTick(state, level, pos);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos);
                }
                @Override
                protected MapCodec<? extends Block> codec() {
                    return null;
                }
            };
        } else if (def.isPlant()) {
            return new ModBushBlock(props) {
                @Override
                protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? toItemInteractionResult(result) : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }
                @Override
                protected MapCodec<? extends BushBlock> codec() {
                    return null;
                }
            };
        } else {
            return new ModBlock(props);
        }
    }

    private static int getSporeBlossomColor(String id) {
        if (id.contains("red")) return 0xFE4A4E;
        if (id.contains("cyan")) return 0x1FDEE2;
        if (id.contains("blue")) return 0x4A7DF4;
        if (id.contains("purple")) return 0xA146F2;
        if (id.contains("orange")) return 0xF76E32;
        return 0xFFFFFF;
    }

    private Block getBaseBlock(BlockDefinition def) {
        String parentId = def.getParentBlockId();
        if (parentId != null) {
            String cleanId = parentId.toLowerCase().replace("blocks.", "").replace("modblocks.", "").replace(".get(", "");
            ResourceLocation rl = ResourceLocation.tryParse(cleanId.contains(":") ? cleanId : "buildscape:" + cleanId);
            if (rl != null) {
                Block b = BuiltInRegistries.BLOCK.get(rl);
                if (b != null && b != Blocks.AIR) return b;
                ResourceLocation mcRl = ResourceLocation.withDefaultNamespace(cleanId);
                b = BuiltInRegistries.BLOCK.get(mcRl);
                if (b != null && b != Blocks.AIR) return b;
            }
        }
        return Blocks.OAK_PLANKS;
    }

    private Supplier<Block> resolveBlockSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("blocks.", "").replace("modblocks.", "").replace(".get(", "").replace(")", "").trim();
        return () -> {
            ResourceLocation rl = ResourceLocation.tryParse(cleanId.contains(":") ? cleanId : "buildscape:" + cleanId);
            if (rl != null) {
                Block b = BuiltInRegistries.BLOCK.get(rl);
                if (b != null && b != Blocks.AIR) return b;
            }
            ResourceLocation mcRl = ResourceLocation.withDefaultNamespace(cleanId);
            Block b = BuiltInRegistries.BLOCK.get(mcRl);
            if (b != null && b != Blocks.AIR) return b;
            return null;
        };
    }

    private Supplier<Item> resolveItemSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("items.", "").replace("moditems.", "").replace(".get(", "").replace(")", "").trim();
        return () -> {
            ResourceLocation rl = ResourceLocation.tryParse(cleanId.contains(":") ? cleanId : "buildscape:" + cleanId);
            if (rl != null) {
                Item it = BuiltInRegistries.ITEM.get(rl);
                if (it != null && it != Items.AIR) return it;
            }
            ResourceLocation mcRl = ResourceLocation.withDefaultNamespace(cleanId);
            Item it = BuiltInRegistries.ITEM.get(mcRl);
            if (it != null && it != Items.AIR) return it;
            return null;
        };
    }

    private FlowingFluid resolveFlowingFluid(String id) {
        if (id == null) return (FlowingFluid) Fluids.WATER;
        String cleanId = id.toLowerCase().replace("fluids.", "").replace("modfluids.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(namespace, path);
        Fluid f = BuiltInRegistries.FLUID.get(rl);
        if (f instanceof FlowingFluid flowing && f != Fluids.EMPTY) {
            return flowing;
        }
        return (FlowingFluid) Fluids.WATER;
    }

    private BlockState getBaseState(BlockDefinition def) {
        return getBaseBlock(def).defaultBlockState();
    }

    private BlockBehaviour.Properties buildProperties(BlockDefinition def) {
        CommonBlockProperties cp = def.getProperties();
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of();
        props.strength(cp.getHardness(), cp.getResistance());
        if (cp.requiresCorrectTool()) {
            props.requiresCorrectToolForDrops();
        }
        if (cp.isNoCollision()) {
            props.noCollission();
        }
        if (cp.isNoOcclusion()) {
            props.noOcclusion();
        }
        if (cp.getLightLevel() > 0) {
            final int light = cp.getLightLevel();
            props.lightLevel(state -> light);
        }
        props.sound(mapSound(cp.getSound()));
        if (cp.getMapColor() != null) {
            props.mapColor(mapColor(cp.getMapColor()));
        }
        return props;
    }

    private SoundType mapSound(String sound) {
        if (sound == null) return SoundType.STONE;
        return switch (sound.toUpperCase()) {
            case "WOOD" -> SoundType.WOOD;
            case "GRAVEL" -> SoundType.GRAVEL;
            case "GRASS" -> SoundType.GRASS;
            case "METAL" -> SoundType.METAL;
            case "GLASS" -> SoundType.GLASS;
            case "WOOL" -> SoundType.WOOL;
            case "SAND" -> SoundType.SAND;
            case "SNOW" -> SoundType.SNOW;
            case "LADDER" -> SoundType.LADDER;
            case "ANVIL" -> SoundType.ANVIL;
            case "SLIME" -> SoundType.SLIME_BLOCK;
            case "HONEY" -> SoundType.HONEY_BLOCK;
            case "COPPER" -> SoundType.COPPER;
            case "BAMBOO" -> SoundType.BAMBOO;
            case "BONE" -> SoundType.BONE_BLOCK;
            case "CHAIN" -> SoundType.CHAIN;
            case "CANDLE" -> SoundType.CANDLE;
            default -> SoundType.STONE;
        };
    }

    private MapColor mapColor(String color) {
        if (color == null) return MapColor.NONE;
        return switch (color.toUpperCase()) {
            case "COLOR_BLACK", "BLACK" -> MapColor.COLOR_BLACK;
            case "COLOR_BLUE", "BLUE" -> MapColor.COLOR_BLUE;
            case "COLOR_BROWN", "BROWN" -> MapColor.COLOR_BROWN;
            case "COLOR_CYAN", "CYAN" -> MapColor.COLOR_CYAN;
            case "COLOR_GRAY", "GRAY" -> MapColor.COLOR_GRAY;
            case "COLOR_GREEN", "GREEN" -> MapColor.COLOR_GREEN;
            case "COLOR_LIGHT_BLUE", "LIGHT_BLUE" -> MapColor.COLOR_LIGHT_BLUE;
            case "COLOR_LIGHT_GRAY", "LIGHT_GRAY" -> MapColor.COLOR_LIGHT_GRAY;
            case "COLOR_LIGHT_GREEN", "LIME" -> MapColor.COLOR_LIGHT_GREEN;
            case "COLOR_MAGENTA", "MAGENTA" -> MapColor.COLOR_MAGENTA;
            case "COLOR_ORANGE", "ORANGE" -> MapColor.COLOR_ORANGE;
            case "COLOR_PINK", "PINK" -> MapColor.COLOR_PINK;
            case "COLOR_PURPLE", "PURPLE" -> MapColor.COLOR_PURPLE;
            case "COLOR_RED", "RED" -> MapColor.COLOR_RED;
            case "COLOR_YELLOW", "YELLOW" -> MapColor.COLOR_YELLOW;
            case "WOOD", "DIRT" -> MapColor.WOOD;
            case "STONE" -> MapColor.STONE;
            case "METAL" -> MapColor.METAL;
            case "WATER" -> MapColor.WATER;
            case "SAND" -> MapColor.SAND;
            case "SNOW", "WHITE" -> MapColor.SNOW;
            default -> MapColor.NONE;
        };
    }

}

package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import com.kingodogo.buildscape.platform.Services;

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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import com.kingodogo.buildscape.block.CommonBlockProperties;
import com.kingodogo.buildscape.block.CopperChestBlockEntity;
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
import com.kingodogo.buildscape.block.ICopperChestBlock;
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
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
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
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
public class BlockFactory implements IBlockFactory {

    private static class CustomWoodType extends WoodType {
        public CustomWoodType(String name) {
            super(name);
        }
    }
    private static final WoodType BAMBOO_WOOD_TYPE = new CustomWoodType("buildscape:bamboo");
    private static final WoodType MANGROVE_WOOD_TYPE = new CustomWoodType("buildscape:mangrove");

    @Override
    public Block createBlock(BlockDefinition def) {
        BlockBehaviour.Properties props = buildProperties(def);

        if (def.isLargeChain()) {
            return new com.kingodogo.buildscape.block.LargeChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isClimbableChain()) {
            return new com.kingodogo.buildscape.block.ClimbableChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isCopperBulb()) {
            return new CopperBulbBlock(props, def.copperBulbLightLevel()) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
                @Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
            };
        } else if (def.isWaterloggableGrate()) {
            return new WaterloggableGrateBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
            };
        } else if (def.isWeatheringBars()) {
            return new WeatheringBarsBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBolt()) {
            return new WeatheringBoltBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringLargeChain()) {
            return new WeatheringLargeChainBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBlock()) {
            return new Block(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringButton()) {
            return new net.minecraft.world.level.block.StoneButtonBlock(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringPressurePlate()) {
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(WeatheringBlockLogic.PRESSURE_PLATE_MAX_WEIGHT, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringSlab()) {
            return new net.minecraft.world.level.block.SlabBlock(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringStair()) {
            return new ModStairBlock(getBaseState(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isStair()) {
            return new ModStairBlock(getBaseState(def), props) {
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && super.propagatesSkylightDown(state, level, pos); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props);
        } else if (def.isGrassSlab()) {
            props.randomTicks();
            Supplier<Block> dirtSlabSupplier = resolveBlockSupplier("dirt_slab");
            class V118xGrassSlabBlock extends GrassSlabBlock implements BonemealableBlock {
                public V118xGrassSlabBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction == Direction.UP) {
                        state = state.setValue(SNOWY, isSnowySetting(level, pos));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
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
                    int lightLevel = net.minecraft.world.level.lighting.LayerLightEngine.getLightBlockInto(
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
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack held = player.getItemInHand(hand);
                    if (held.getItem() instanceof net.minecraft.world.item.BoneMealItem) {
                        if (isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                            if (level instanceof ServerLevel serverLevel) {
                                if (isBonemealSuccess(level, level.getRandom(), pos, state)) {
                                    performBonemeal(serverLevel, level.getRandom(), pos, state);
                                    if (!player.getAbilities().instabuild) {
                                        held.shrink(1);
                                    }
                                    level.levelEvent(2005, pos, 0);
                                }
                            }
                            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.BONE_MEAL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return level.getBlockState(pos.above()).isAir();
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos abovePos = pos.above();
                    Supplier<Block> snowyShortGrassSupplier = resolveBlockSupplier("snowy_short_grass");
                    BlockState foliage = Blocks.GRASS.defaultBlockState();
                    if (random.nextInt(8) == 0 && snowyShortGrassSupplier != null && snowyShortGrassSupplier.get() != null) {
                        foliage = snowyShortGrassSupplier.get().defaultBlockState();
                    }
                    if (foliage.canSurvive(level, abovePos)) {
                        level.setBlock(abovePos, foliage, 3);
                    }
                }
            }
            return new V118xGrassSlabBlock(props);
        } else if (def.isHayBaleSlab()) {
            return new HayBaleSlabBlock(props) {
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    entity.causeFallDamage(fallDistance, 0.2F, net.minecraft.world.damagesource.DamageSource.FALL);
                }
            };
        } else if (def.isLogSlab()) {
            return new LogSlabBlock(getBaseBlock(def), props) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    return res != InteractionResult.PASS ? res : super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isSlab()) {
            return new ModSlabBlock(getBaseBlock(def), props) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isMudSlab()) {
            return new MudSlabBlock(props);
        } else if (def.isMud()) {
            return new MudBlock(props);
        } else if (def.isMossOverlay()) {
            return new MossOverlayBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isSnowOverlay()) {
            return new SnowOverlayBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isWall()) {
            return new ModWallBlock(props);
        } else if (def.isFalling()) {
            return new FallingSandBlock(props) {
                @Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onScheduledTick(state, level, pos); }
            };
        } else if (def.isCopperDoor()) {
            return new DoorBlock(props) {
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 10);
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_DOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_DOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(player, state.getValue(OPEN) ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            };
        } else if (def.isDoor()) {
            return new DoorBlock(props);
        } else if (def.isCopperTrapdoor()) {
            return new TrapDoorBlock(props) {
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 2);
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            };
        } else if (def.isTrapdoor()) {
            return new TrapDoorBlock(props);
        } else if (def.isButton()) {
            return new WoodButtonBlock(props);
        } else if (def.isPressurePlate()) {
            return new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, props);
        } else if (def.isMushroomShelves()) {
            return new MushroomShelvesBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(MushroomShelvesBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
                    return canClimb(level, pos);
                }
            };
        } else if (def.isShelf()) {
            return new ShelfBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(ShelfBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                        this.updateNeighborsAfterPoweringDown(level, pos, state);
                    }
                }
            };
        } else if (def.isWorkbench()) {
            return new BuildersWorkbenchBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
            };
        } else if (def.isGlassJar()) {
            return new GlassJarBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isSmokeVent()) {
            return new SmokeVentBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public boolean hasAnalogOutputSignal(BlockState state) {
                    return true;
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isMuff()) {
            return new MuffBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
            };
        } else if (def.isChest()) {
            boolean isWaxed = def.getId().contains("waxed");
            return new CopperChest(isWaxed, props);
        } else if (def.isFenceGate()) {
            return new FenceGateBlock(props);
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
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(state, level, pos, entity);
                    super.entityInside(state, level, pos, entity);
                }
            };
        } else if (def.isMangroveLeaves()) {
            class V118xMangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
                public V118xMangroveLeavesBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return MangroveLeavesBlock.canGrowPropagule(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    MangroveLeavesBlock.growPropagule(level, pos);
                }
            }
            return new V118xMangroveLeavesBlock(props);
        } else if (def.isMangrovePropagule()) {
            class V118xMangrovePropaguleBlock extends MangrovePropaguleBlock implements BonemealableBlock {
                public V118xMangrovePropaguleBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return canBonemeal(level, pos, state);
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    performBonemealEffect(level, pos, state);
                }
            }
            return new V118xMangrovePropaguleBlock(props);
        } else if (def.isMangroveRoots()) {
            return new MangroveRootsBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
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
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isColoredMossLayers()) {
            Supplier<Block> fullBlock = resolveBlockSupplier(def.getId().replace("_layers", "_block"));
            class V118xColoredMossLayersBlock extends ColoredMossLayersBlock {
                public V118xColoredMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties, fullBlock);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V118xColoredMossLayersBlock(props);
        } else if (def.isMossLayers()) {
            class V118xMossLayersBlock extends MossLayersBlock {
                public V118xMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V118xMossLayersBlock(props);
        } else if (def.isWoolLayers()) {
            String color = def.getId().replace("_carpet_layers", "").replace("_layered_wool_layers", "");
            Supplier<Item> carpetSupplier = resolveItemSupplier(color + "_carpet_layers");
            return new WoolLayersBlock(props, color, carpetSupplier) {
                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state);
                }
            };
        } else if (def.isLeafLayers()) {
            return new LeafLayersBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
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
            class V118xLeafLitterBlock extends LeafLitterBlock implements BonemealableBlock {
                public V118xLeafLitterBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    return res != InteractionResult.PASS ? res : super.use(state, level, pos, player, hand, hit);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    performBonemealEffect(level, pos, state);
                }
            }
            return new V118xLeafLitterBlock(props);
        } else if (def.isLayer()) {
            return new ModLayerBlock(props);
        } else if (def.isAshenKingPillar()) {
            return new AshenKingPillarBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
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
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(PillarBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
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
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }
            };
        } else if (def.isHollowPipe()) {
            return new HollowPipeBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(level, pos, state);
                }
            };
        } else if (def.isPipe()) {
            return new PipeBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
            };
        } else if (def.isRoseVines()) {
            return new RoseVinesBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return state;
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
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
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new DecoratedPotBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isFestiveStocking()) {
            return new FestiveStockingBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isCascadeNoMist()) {
            return new CascadeBlockNoMist(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return false; }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.level.material.Fluid fluid) { return false; }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(level, pos, state, player, hand, hit);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isCascade()) {
            return new CascadeBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return false; }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.level.material.Fluid fluid) { return false; }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isGlazedGlass()) {
            return new GlazedGlassBlock(props) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = getBeaconColor(state, level, pos, beaconPos);
                    return new float[]{((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F, (color & 255) / 255.0F};
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
            };
        } else if (def.isIcicleCauldron()) {
            return new IcicleCauldronBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
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
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isStar()) {
            return new StarBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                    super.entityInside(state, level, pos, entity);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops();
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isCushion()) {
            return new CushionBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    if (entity.isSuppressingBounce()) super.fallOn(level, state, pos, entity, fallDistance);
                    else entity.causeFallDamage(fallDistance, 0.0F, net.minecraft.world.damagesource.DamageSource.FALL);
                }
                @Override
                public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
                    if (entity.isSuppressingBounce()) super.updateEntityAfterFallOn(level, entity);
                    else bounceUp(entity);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isBigBook()) {
            return new BigBookBlock(props) {};
        } else if (def.isBigCandle()) {
            props.lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? BigCandleBlock.LIGHT_LEVEL : 0);
            return new BigCandleBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
                @Override
                public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
                    return 1.0F;
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public boolean isRandomlyTicking(BlockState state) {
                    return state.getValue(LIT);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    onRandomTick(state, level, pos, random::nextInt, random::nextFloat);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
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
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    return onUpdateShape(super.updateShape(state, direction, adjacentState, level, pos, adjacentPos), direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
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
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return isTinted() ? 0 : super.getLightBlock(state, level, pos);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = getBeaconColor(state, level, pos, beaconPos);
                    return new float[]{((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F};
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isOrnament()) {
            boolean tinted = def.isTintedOrnament();
            props.lightLevel(state -> state.getValue(OrnamentBlock.LIT) ? (tinted ? 4 : 15) : 0);
            if (tinted) props.dynamicShape();
            return new OrnamentBlock(props, tinted) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTinted() ? 0 : super.getLightBlock(state, level, pos); }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { int color = getBeaconColor(state, level, pos, beaconPos); return new float[]{((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F, (color & 255) / 255.0F}; }
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); if (!canSurvive(state, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return onInteract(level, pos, state, player, hand, hit); }
            };
        } else if (def.isStringLight()) {
            return new StringLightBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isExperienceCauldron()) {
            return new LayeredCauldronBlock(props, (precipitation) -> false, CauldronInteraction.EMPTY) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
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
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isMulticolorGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new MulticolorGlowLightsBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new GlowLightsBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isFrostRose()) {
            return new FrostRoseBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (!canSurvive(adjusted, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isStrawBed()) {
            return new StrawBedBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isHangingMoss()) {
            props.randomTicks();
            class V118xHangingMossBlock extends HangingMossBlock implements BonemealableBlock {
                public V118xHangingMossBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
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
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    if (state.getValue(TIP) && !state.getValue(SHEARED)) {
                        if (random.nextInt(10) == 0) {
                            BlockPos growPos = pos.below();
                            BlockState growState = level.getBlockState(growPos);
                            if (growState.isAir() || growState.getMaterial().isReplaceable()) {
                                level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                                level.setBlock(pos, state.setValue(TIP, false), 3);
                            }
                        }
                    }
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack held = player.getItemInHand(hand);
                    if (held.getItem() instanceof net.minecraft.world.item.ShearsItem) {
                        BlockPos tipPos = findTip(level, pos, this);
                        BlockState tipState = level.getBlockState(tipPos);
                        if (tipState.hasProperty(SHEARED) && !tipState.getValue(SHEARED)) {
                            if (!level.isClientSide) {
                                level.setBlockAndUpdate(tipPos, tipState.setValue(SHEARED, true));
                                level.playSound(null, tipPos, net.minecraft.sounds.SoundEvents.GROWING_PLANT_CROP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.SHEAR, tipPos);
                                held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                            }
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return false;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    return growState.isAir() || growState.getMaterial().isReplaceable();
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    return !(tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED));
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    if (growState.isAir() || growState.getMaterial().isReplaceable()) {
                        level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                        level.setBlock(tipPos, tipState.setValue(TIP, false), 3);
                    }
                }
            }
            return new V118xHangingMossBlock(props);
        } else if (def.isClover()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V118xCloverBlock extends CloverBlock implements BonemealableBlock {
                public V118xCloverBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growClover(level, pos, state);
                }
            }
            return new V118xCloverBlock(props);
        } else if (def.isPetal()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V118xPetalBlock extends PetalBlock implements BonemealableBlock {
                public V118xPetalBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growPetal(level, pos, state);
                }
            }
            return new V118xPetalBlock(props);
        } else if (def.isWildflowers()) {
            props.sound(SoundType.GRASS).noCollission();
            class V118xWildflowersBlock extends WildflowersBlock implements BonemealableBlock {
                public V118xWildflowersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growFlowers(level, pos, state);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state);
                }
            }
            return new V118xWildflowersBlock(props);
        } else if (def.isColoredMoss()) {
            Supplier<Block> carpet = resolveBlockSupplier(def.getId().replace("_block", "_carpet"));
            Supplier<Block> overlay = resolveBlockSupplier(def.getId().replace("_block", "_overlay"));
            Supplier<Block> layers = resolveBlockSupplier(def.getId().replace("_block", "_layers"));
            Supplier<Block> sapling = resolveBlockSupplier(def.getParentBlockId());
            class V118xColoredMossBlock extends ColoredMossBlock implements BonemealableBlock {
                public V118xColoredMossBlock(BlockBehaviour.Properties properties) {
                    super(properties, carpet, overlay, layers, sapling, null);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growMoss(level, pos, state, random::nextInt);
                }
            }
            return new V118xColoredMossBlock(props);
        } else if (def.isColoredSporeBlossom()) {
            int colorRGB = getSporeBlossomColor(def.getId());
            class V118xColoredSporeBlossomBlock extends ColoredSporeBlossomBlock {
                public V118xColoredSporeBlossomBlock(BlockBehaviour.Properties properties) {
                    super(properties, colorRGB);
                }

                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
            }
            return new V118xColoredSporeBlossomBlock(props);
        } else if (def.isCreakingHeart()) {
            Supplier<Block> resinClumpSupplier = resolveBlockSupplier("resin_clump");
            class V118xCreakingHeartBlock extends CreakingHeartBlock {
                public V118xCreakingHeartBlock(BlockBehaviour.Properties properties) {
                    super(properties, resinClumpSupplier);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(state, level, pos, player, hand);
                }

                @Override
                public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    onScheduledTick(state, level, pos);
                }
            }
            return new V118xCreakingHeartBlock(props);
        } else if (def.isMonetFlower()) {
            class V118xMonetFlowerBlock extends MonetFlowerBlock implements BonemealableBlock {
                public V118xMonetFlowerBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    performBonemealAction(level, pos, this);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
            }
            return new V118xMonetFlowerBlock(props);
        } else if (def.isSnowyBush()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyBushBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isSnowyFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V118xSnowyFernBlock extends SnowyFernBlock implements BonemealableBlock {
                public V118xSnowyFernBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyLargeFern);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyFernBlock(props);
        } else if (def.isSnowyShortGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            class V118xSnowyShortGrassBlock extends SnowyShortGrassBlock implements BonemealableBlock {
                public V118xSnowyShortGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyTallGrass);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyShortGrassBlock(props);
        } else if (def.isSnowyLargeFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyLargeFernBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isSnowyTallGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyTallGrassBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
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
            class V118xSnowyGrassBlock extends SnowyGrassBlock implements BonemealableBlock {
                public V118xSnowyGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowyShortGrass, snowyBush, snowyFern, snowyTallGrass, snowyLargeFern);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
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
                    int lightLevel = net.minecraft.world.level.lighting.LayerLightEngine.getLightBlockInto(
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
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }

                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealFoliage(level, pos);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyGrassBlock(props);
        } else if (def.isExperienceFluid()) {
            Supplier<FlowingFluid> fluidSupplier = () -> resolveFlowingFluid(def.getParentBlockId());
            return new ExperienceFluidBlock(fluidSupplier, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    super.animateTick(state, level, pos, random);
                    if (random.nextInt(30) == 0) {
                        spawnXpParticle(level, pos, random.nextDouble(), random.nextDouble());
                    }
                }
            };
        } else if (def.isSilkTouchOnlyGlass()) {
            return new SilkTouchOnlyGlassBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = state.getMapColor(level, pos).col;
                    return new float[]{((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f};
                }
            };
        } else if (def.isSilkTouchOnlyPane()) {
            return new SilkTouchOnlyPaneBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = state.getMapColor(level, pos).col;
                    return new float[]{((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f};
                }
            };
        } else if (def.isResinClump()) {
            props.lightLevel(state -> 7);
            return new ResinClumpBlock(props) {
                public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
                    return 7;
                }
            };
        } else if (def.isWallpaperFlat()) {
            return new WallpaperFlatBlock(props) {};
        } else if (def.isPointedIcicle()) {
            return new PointedIcicleBlock(props) {
                @Override
                public void tick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onIcicleTick(state, level, pos);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
                    onNeighborUpdate(state, level, pos, neighborBlock, neighborPos, isMoving);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(state, level, pos);
                }
            };
        } else if (def.isPlant()) {
            return new ModBushBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
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
                Block b = Registry.BLOCK.get(rl);
                if (b != null && b != Blocks.AIR) return b;
                ResourceLocation mcRl = new ResourceLocation(cleanId);
                b = Registry.BLOCK.get(mcRl);
                if (b != null && b != Blocks.AIR) return b;
            }
        }
        return Blocks.OAK_PLANKS;
    }

    private Supplier<Block> resolveBlockSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("blocks.", "").replace("modblocks.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        return () -> {
            ResourceLocation rl = new ResourceLocation(namespace, path);
            Block b = Registry.BLOCK.get(rl);
            if (b != null && b != Blocks.AIR) return b;
            b = Registry.BLOCK.get(new ResourceLocation("minecraft", path));
            if (b != null && b != Blocks.AIR) return b;
            return null;
        };
    }

    private Supplier<Item> resolveItemSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("items.", "").replace("moditems.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        return () -> {
            ResourceLocation rl = new ResourceLocation(namespace, path);
            Item it = Registry.ITEM.get(rl);
            if (it != null && it != Items.AIR) return it;
            it = Registry.ITEM.get(new ResourceLocation("minecraft", path));
            if (it != null && it != Items.AIR) return it;
            return null;
        };
    }

    private FlowingFluid resolveFlowingFluid(String id) {
        if (id == null) return (FlowingFluid) Fluids.WATER;
        String cleanId = id.toLowerCase().replace("fluids.", "").replace("modfluids.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        ResourceLocation rl = new ResourceLocation(namespace, path);
        Fluid f = Registry.FLUID.get(rl);
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
        net.minecraft.world.level.material.Material mat = mapMaterial(cp.getMaterial());
        MaterialColor color = mapColor(cp.getMapColor());

        BlockBehaviour.Properties props = color != null ?
                BlockBehaviour.Properties.of(mat, color) :
                BlockBehaviour.Properties.of(mat);

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
        return props;
    }

    private net.minecraft.world.level.material.Material mapMaterial(String mat) {
        if (mat == null) return net.minecraft.world.level.material.Material.STONE;
        return switch (mat.toUpperCase()) {
            case "WOOD" -> net.minecraft.world.level.material.Material.WOOD;
            case "METAL", "IRON", "HEAVY_METAL" -> net.minecraft.world.level.material.Material.METAL;
            case "SAND" -> net.minecraft.world.level.material.Material.SAND;
            case "GLASS" -> net.minecraft.world.level.material.Material.GLASS;
            case "DIRT", "GRAVEL" -> net.minecraft.world.level.material.Material.DIRT;
            case "PLANT" -> net.minecraft.world.level.material.Material.PLANT;
            case "LEAVES" -> net.minecraft.world.level.material.Material.LEAVES;
            case "WOOL", "CLOTH" -> net.minecraft.world.level.material.Material.WOOL;
            case "CLAY" -> net.minecraft.world.level.material.Material.CLAY;
            case "WATER" -> net.minecraft.world.level.material.Material.WATER;
            case "DECORATION" -> net.minecraft.world.level.material.Material.DECORATION;
            case "ICE" -> net.minecraft.world.level.material.Material.ICE;
            case "SNOW" -> net.minecraft.world.level.material.Material.SNOW;
            default -> net.minecraft.world.level.material.Material.STONE;
        };
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

    private MaterialColor mapColor(String color) {
        if (color == null) return MaterialColor.NONE;
        return switch (color.toUpperCase()) {
            case "COLOR_BLACK", "BLACK" -> MaterialColor.COLOR_BLACK;
            case "COLOR_BLUE", "BLUE" -> MaterialColor.COLOR_BLUE;
            case "COLOR_BROWN", "BROWN" -> MaterialColor.COLOR_BROWN;
            case "COLOR_CYAN", "CYAN" -> MaterialColor.COLOR_CYAN;
            case "COLOR_GRAY", "GRAY" -> MaterialColor.COLOR_GRAY;
            case "COLOR_GREEN", "GREEN" -> MaterialColor.COLOR_GREEN;
            case "COLOR_LIGHT_BLUE", "LIGHT_BLUE" -> MaterialColor.COLOR_LIGHT_BLUE;
            case "COLOR_LIGHT_GRAY", "LIGHT_GRAY" -> MaterialColor.COLOR_LIGHT_GRAY;
            case "COLOR_LIGHT_GREEN", "LIME" -> MaterialColor.COLOR_LIGHT_GREEN;
            case "COLOR_MAGENTA", "MAGENTA" -> MaterialColor.COLOR_MAGENTA;
            case "COLOR_ORANGE", "ORANGE" -> MaterialColor.COLOR_ORANGE;
            case "COLOR_PINK", "PINK" -> MaterialColor.COLOR_PINK;
            case "COLOR_PURPLE", "PURPLE" -> MaterialColor.COLOR_PURPLE;
            case "COLOR_RED", "RED" -> MaterialColor.COLOR_RED;
            case "COLOR_YELLOW", "YELLOW" -> MaterialColor.COLOR_YELLOW;
            case "WOOD", "DIRT" -> MaterialColor.WOOD;
            case "STONE" -> MaterialColor.STONE;
            case "METAL" -> MaterialColor.METAL;
            case "WATER" -> MaterialColor.WATER;
            case "SAND" -> MaterialColor.SAND;
            case "SNOW", "WHITE" -> MaterialColor.SNOW;
            default -> MaterialColor.NONE;
        };
    }

    private static class CopperChest extends ChestBlock implements ICopperChestBlock, ICommonRemoval {
        private final boolean isWaxed;

        @SuppressWarnings("unchecked")
        public CopperChest(boolean isWaxed, BlockBehaviour.Properties properties) {
            super(isWaxed ? properties : properties.randomTicks(), () -> (BlockEntityType<? extends ChestBlockEntity>) (Object) ModBlockEntities.COPPER_CHEST_TYPE);
            this.isWaxed = isWaxed;
        }

        @Override
        public boolean isWaxed() {
            return isWaxed;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new CopperChestBlockEntity(pos, state);
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return !isWaxed;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
            if (!isWaxed) {
                CopperOxidationHandler.tryOxidize(level, pos, state);
            }
        }

        @Override
        public void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
            if (level.getBlockState(pos).getBlock() instanceof ICopperChestBlock) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
            if (newState.getBlock() instanceof ICopperChestBlock) {
                level.updateNeighbourForOutputSignal(pos, this);
                return;
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}

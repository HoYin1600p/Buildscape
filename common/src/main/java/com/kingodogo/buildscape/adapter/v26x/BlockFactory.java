package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.block.SulfurSpikeBlock;
import com.kingodogo.buildscape.block.SulfurSpikeLogic;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;

import java.util.List;
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
import com.kingodogo.buildscape.block.CactusFlowerBlock;
import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.ColoredMossBlock;
import com.kingodogo.buildscape.block.ColoredSporeBlossomBlock;
import com.kingodogo.buildscape.block.BambooSignBlockEntity;
import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import com.kingodogo.buildscape.block.MangroveSignBlockEntity;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import com.kingodogo.buildscape.block.CommonBlockProperties;
import com.kingodogo.buildscape.block.CopperChestBlockEntity;
import com.kingodogo.buildscape.block.CopperBulbBlock;
import com.kingodogo.buildscape.block.CopperOxidationHandler;
import com.kingodogo.buildscape.block.CopperTorchBlock;
import com.kingodogo.buildscape.block.CopperWallTorchBlock;
import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.DryGrassBlock;
import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.block.ICopperChestBlock;
import com.kingodogo.buildscape.block.ICommonRemoval;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.block.DecoratedPotBlock;
import com.kingodogo.buildscape.block.EyeblossomBlock;
import com.kingodogo.buildscape.block.FallingSandBlock;
import com.kingodogo.buildscape.block.FestiveLampBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FireflyBushBlock;
import com.kingodogo.buildscape.block.FroglightBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.GlassJarBlock;
import com.kingodogo.buildscape.block.GlazedGlassBlock;
import com.kingodogo.buildscape.block.GlowLightsBlock;
import com.kingodogo.buildscape.block.GoldenDandelionBlock;
import com.kingodogo.buildscape.block.GrassSlabBlock;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.block.HayBaleSlabBlock;
import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.IBlockFactory;
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
import com.kingodogo.buildscape.block.SculkCatalystBlock;
import com.kingodogo.buildscape.block.SculkVeinBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyGlassBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.ModBlock;
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
import com.kingodogo.buildscape.block.PotentSulfurBlock;
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
import com.kingodogo.buildscape.block.TallDryGrassBlock;
import com.kingodogo.buildscape.block.TrappedDecoratedPotBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WallpaperFlatBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.block.WeatheringBarsBlock;
import com.kingodogo.buildscape.block.WeatheringBoltBlock;
import com.kingodogo.buildscape.block.WeatheringClimbableChainBlock;
import com.kingodogo.buildscape.block.WeatheringGrateBlock;
import com.kingodogo.buildscape.block.WeatheringLanternBlock;
import com.kingodogo.buildscape.block.WeatheringLargeChainBlock;
import com.kingodogo.buildscape.block.WeatheringBlockLogic;
import com.kingodogo.buildscape.block.WildflowersBlock;
import com.kingodogo.buildscape.block.WoolLayersBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import java.util.function.Supplier;
import net.minecraft.world.level.block.ButtonBlock;
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
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
public class BlockFactory implements IBlockFactory {

    @Override
    public Block createBlock(BlockDefinition def) {
        BlockBehaviour.Properties props = buildProperties(def);

        if ("SaplingBlock".equals(def.getBlockType())) {
            return new net.minecraft.world.level.block.SaplingBlock(WorldGenFactory.saplingGrower(def.getId()),
                    props.noCollision().instabreak().sound(SoundType.GRASS).randomTicks()) {};
        }

        if (def.isLargeChain()) {
            return new com.kingodogo.buildscape.block.LargeChainBlock(props) {
                @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) { if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random); }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
            };
        } else if (def.isClimbableChain()) {
            return new com.kingodogo.buildscape.block.ClimbableChainBlock(props) {
                @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) { if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random); }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
            };
        } else if (def.isCopperBulb()) {
            return new CopperBulbBlock(props, def.copperBulbLightLevel()) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { onRandomTick(state, level, pos); }
                @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) { onNeighborUpdate(level, pos, state, block, pos, isMoving); }
                @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) { return getAnalogOutput(state, level, pos); }
            };
        } else if (def.isWeatheringGrate()) {
            return new WeatheringGrateBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
                }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWaterloggableGrate()) {
            return new WaterloggableGrateBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
                }
            };
        } else if (def.isWeatheringBars()) {
            return new WeatheringBarsBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringLantern()) {
            return new WeatheringLanternBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBolt()) {
            return new WeatheringBoltBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringLargeChain()) {
            return new WeatheringLargeChainBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringClimbableChain()) {
            return new WeatheringClimbableChainBlock(props) {
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) { onRandomTick(state, level, pos); }
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
        } else if (def.isWeatheringDoor()) {
            return new DoorBlock(BlockSetType.OAK, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
                @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 10);
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_DOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_DOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(player, state.getValue(OPEN) ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
                    return InteractionResult.SUCCESS;
                }
            };
        } else if (def.isWeatheringPressurePlate()) {
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(WeatheringBlockLogic.PRESSURE_PLATE_MAX_WEIGHT, BlockSetType.OAK, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringRod()) {
            return new net.minecraft.world.level.block.LightningRodBlock(props.randomTicks()) {
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
        } else if (def.isWeatheringTrapdoor()) {
            return new TrapDoorBlock(BlockSetType.OAK, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
                @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 2);
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            };
        } else if (def.isWeatheringVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isStair()) {
            return new ModStairBlock(getBaseState(def), props);
        } else if (def.isVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props);
        } else if (def.isGrassSlab()) {
            props.randomTicks();
            Supplier<Block> dirtSlabSupplier = resolveBlockSupplier("dirt_slab");
            class V26xGrassSlabBlock extends GrassSlabBlock implements BonemealableBlock {
                public V26xGrassSlabBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (direction == Direction.UP) {
                        state = state.setValue(SNOWY, isSnowySetting(level, pos));
                    }
                    return super.updateShape(state, level, scheduledTicks, pos, direction, neighborPos, neighborState, random);
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
                    if (aboveState.getFluidState().isFull()) {
                        return false;
                    }
                    int dampening = net.minecraft.world.level.lighting.LightEngine.getLightDampeningInto(
                            state,
                            aboveState,
                            Direction.UP,
                            aboveState.getLightDampening()
                    );
                    return dampening < 15;
                }

                private boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
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
                            return InteractionResult.SUCCESS;
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
            return new V26xGrassSlabBlock(props);
        } else if (def.isHayBaleSlab()) {
            return new HayBaleSlabBlock(props) {
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
                    entity.causeFallDamage(fallDistance, 0.2F, level.damageSources().fall());
                }
            };
        } else if (def.isLogSlab()) {
            return new LogSlabBlock(getBaseBlock(def), props) {
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    return res != InteractionResult.PASS ? res : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isSlab()) {
            return new ModSlabBlock(getBaseBlock(def), props) {
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
            };
        } else if (def.isMudSlab()) {
            return new MudSlabBlock(props);
        } else if (def.isMud()) {
            return new MudBlock(props);
        } else if (def.isMossOverlay()) {
            return new MossOverlayBlock(props) {
                @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isSnowOverlay()) {
            return new SnowOverlayBlock(props) {
                @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isWall()) {
            return new ModWallBlock(props);
        } else if (def.isFalling()) {
            return new FallingSandBlock(props) {
                @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) { onNeighborUpdate(level, pos, state, block, pos, isMoving); }
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
        } else if (def.isWeightedPressurePlate()) {
            // 1.18.2 ModPressurePlateBlock: a weighted plate (max weight 150) with the 'power' property.
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(150, BlockSetType.OAK, props) {};
        } else if (def.isRedstoneLamp()) {
            return new net.minecraft.world.level.block.RedstoneLampBlock(props.lightLevel(
                    state -> state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 15 : 0));
        } else if (def.isLightningRod()) {
            return new net.minecraft.world.level.block.LightningRodBlock(props);
        } else if (def.isPressurePlate()) {
            return new PressurePlateBlock(BlockSetType.OAK, props) {};
        } else if (def.isMushroomShelves()) {
            return new MushroomShelvesBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(MushroomShelvesBlock.WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
                @Override protected int getLightDampening(BlockState state) { return 0; }
            };
        } else if (def.isShelf()) {
            return new ShelfBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(ShelfBlock.WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return codecForDefinition(def, BaseEntityBlock.class);
                }
            };
        } else if (def.isWorkbench()) {
            return new BuildersWorkbenchBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return codecForDefinition(def, BaseEntityBlock.class);
                }
            };
        } else if (def.isGlassJar()) {
            return new GlassJarBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return codecForDefinition(def, BaseEntityBlock.class);
                }
            };
        } else if (def.isSmokeVent()) {
            return new SmokeVentBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    return onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, null, isMoving);
                }
                @Override
                public boolean hasAnalogOutputSignal(BlockState state) {
                    return true;
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return codecForDefinition(def, BaseEntityBlock.class);
                }
            };
        } else if (def.isMuff()) {
            return new MuffBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, null, isMoving);
                }
                @Override
                protected MapCodec<? extends BaseEntityBlock> codec() {
                    return codecForDefinition(def, BaseEntityBlock.class);
                }
            };
        } else if (def.isChest()) {
            boolean isWaxed = def.getId().contains("waxed");
            return new CopperChest(isWaxed, props);
        } else if (def.isFenceGate()) {
            return new FenceGateBlock(WoodType.OAK, props);
        } else if (def.isFence()) {
            return new FenceBlock(props);
        } else if (def.isIronBars()) {
            return new ModIronBarsBlock(props);
        } else if (def.isLadder()) {
            return new ModLadderBlock(props);
        } else if (def.isCopperWallTorch()) {
            return new WallTorchBlock(net.minecraft.core.particles.ParticleTypes.FLAME, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    CopperWallTorchBlock.spawnParticles(state, level, pos);
                }
                @Override
                public MapCodec<WallTorchBlock> codec() {
                    return codecForDefinition(def, WallTorchBlock.class);
                }
            };
        } else if (def.isCopperTorch()) {
            return new TorchBlock(net.minecraft.core.particles.ParticleTypes.FLAME, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    CopperTorchBlock.spawnParticles(state, level, pos);
                }
                @Override
                public MapCodec<? extends TorchBlock> codec() {
                    return codecForDefinition(def, TorchBlock.class);
                }
            };
        } else if (def.isLantern()) {
            return new LanternBlock(props);
        } else if (def.isLeafHedge()) {
            return new LeafHedgeBlock(props) {
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
                    onEntityInside(state, level, pos, entity);
                    super.entityInside(state, level, pos, entity, applier, isMoving);
                }
            };
        } else if (def.isMangroveLeaves()) {
            class V26xMangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
                public V26xMangroveLeavesBlock(BlockBehaviour.Properties properties) {
                    super(0.01f, properties);
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
                @Override
                public MapCodec<? extends LeavesBlock> codec() {
                    return codecForDefinition(def, LeavesBlock.class);
                }
                @Override
                protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
                    // The 1.18.2 reference leaves do not emit falling-leaf particles.
                }
            }
            return new V26xMangroveLeavesBlock(props);
        } else if (def.isMangrovePropagule()) {
            class V26xMangrovePropaguleBlock extends MangrovePropaguleBlock implements BonemealableBlock {
                public V26xMangrovePropaguleBlock(BlockBehaviour.Properties properties) {
                    super(properties.noCollision().instabreak().sound(SoundType.GRASS).randomTicks());
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
                    if (state.getValue(HANGING)) performBonemealEffect(level, pos, state);
                    else WorldGenFactory.advanceMangrove(level, pos, state, random, true);
                }
                @Override
                protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    if (!state.getValue(HANGING)
                            && !level.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(pos).inflate(32)).isEmpty()
                            && random.nextInt(7) == 0) {
                        WorldGenFactory.advanceMangrove(level, pos, state, random, false);
                    }
                }
                @Override
                @SuppressWarnings("unchecked")
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xMangrovePropaguleBlock(props);
        } else if (def.isMangroveRoots()) {
            return new MangroveRootsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
            };
        } else if (def.isBambooStandingSign()) {
            class V26xBambooStandingSignBlock extends StandingSignBlock {
                public V26xBambooStandingSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
                    super(woodType, properties);
                }
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new BambooSignBlockEntity(pos, state);
                }
                @Override
                public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
                    return createTickerHelper(type, ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY_TYPE, net.minecraft.world.level.block.entity.SignBlockEntity::tick);
                }
            }
            return new V26xBambooStandingSignBlock(WoodType.BAMBOO, props);
        } else if (def.isBambooWallSign()) {
            class V26xBambooWallSignBlock extends WallSignBlock {
                public V26xBambooWallSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
                    super(woodType, properties);
                }
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new BambooSignBlockEntity(pos, state);
                }
                @Override
                public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
                    return createTickerHelper(type, ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY_TYPE, net.minecraft.world.level.block.entity.SignBlockEntity::tick);
                }
            }
            return new V26xBambooWallSignBlock(WoodType.BAMBOO, props);
        } else if (def.isMangroveStandingSign()) {
            class V26xMangroveStandingSignBlock extends StandingSignBlock {
                public V26xMangroveStandingSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
                    super(woodType, properties);
                }
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new MangroveSignBlockEntity(pos, state);
                }
                @Override
                public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
                    return createTickerHelper(type, ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY_TYPE, net.minecraft.world.level.block.entity.SignBlockEntity::tick);
                }
            }
            return new V26xMangroveStandingSignBlock(WoodType.MANGROVE, props);
        } else if (def.isMangroveWallSign()) {
            class V26xMangroveWallSignBlock extends WallSignBlock {
                public V26xMangroveWallSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
                    super(woodType, properties);
                }
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new MangroveSignBlockEntity(pos, state);
                }
                @Override
                public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
                    return createTickerHelper(type, ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY_TYPE, net.minecraft.world.level.block.entity.SignBlockEntity::tick);
                }
            }
            return new V26xMangroveWallSignBlock(WoodType.MANGROVE, props);
        } else if (def.isLeaves()) {
            return new LeavesBlock(0.01f, props) {
                @Override
                public MapCodec<? extends LeavesBlock> codec() {
                    return codecForDefinition(def, LeavesBlock.class);
                }
                @Override
                protected void spawnFallingLeavesParticle(Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
                    // The 1.18.2 reference leaves do not emit falling-leaf particles.
                }
            };
        } else if (def.isSnowyLeaves()) {
            return new LeavesBlock(0.01f, props) {
                @Override
                public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
                    return net.minecraft.world.phys.shapes.Shapes.empty();
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
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
                @Override
                public MapCodec<? extends LeavesBlock> codec() {
                    return codecForDefinition(def, LeavesBlock.class);
                }
                @Override
                protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
                    // The 1.18.2 reference leaves do not emit falling-leaf particles.
                }
            };
        } else if (def.isColoredMossLayers()) {
            Supplier<Block> fullBlock = resolveBlockSupplier(def.getId().replace("_layers", "_block"));
            class V26xColoredMossLayersBlock extends ColoredMossLayersBlock {
                public V26xColoredMossLayersBlock(BlockBehaviour.Properties properties) {
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
            return new V26xColoredMossLayersBlock(props);
        } else if (def.isMossLayers()) {
            class V26xMossLayersBlock extends MossLayersBlock {
                public V26xMossLayersBlock(BlockBehaviour.Properties properties) {
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
            return new V26xMossLayersBlock(props);
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
                    return codecForDefinition(def, net.minecraft.world.level.block.SnowLayerBlock.class);
                }
            };
        } else if (def.isLeafLayers()) {
            return new LeafLayersBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
            };
        } else if (def.isLeafLitter()) {
            class V26xLeafLitterBlock extends LeafLitterBlock implements BonemealableBlock {
                public V26xLeafLitterBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    return res != InteractionResult.PASS ? res : super.useItemOn(stack, state, level, pos, player, hand, hit);
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
                @SuppressWarnings("unchecked")
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xLeafLitterBlock(props);
        } else if (def.isLayer()) {
            return new ModLayerBlock(props);
        } else if (def.isAshenKingPillar()) {
            return new AshenKingPillarBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);
                    if (includeData && level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
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
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(PillarBlock.WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, neighborBlock, null, isMoving);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                protected int getLightDampening(BlockState state) {
                    return 0;
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);
                    if (includeData && level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
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
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                public boolean canPlaceLiquid(@Nullable LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
                    return canPlaceLiquid(level, pos, state, fluid);
                }
                @Override
                public ItemStack pickupBlock(@Nullable LivingEntity entity, LevelAccessor level, BlockPos pos, BlockState state) {
                    return pickupBlock(level, pos, state);
                }
            };
        } else if (def.isHollowPipe()) {
            return new HollowPipeBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                public boolean canPlaceLiquid(@Nullable LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
                    return canPlaceLiquid(level, pos, state, fluid);
                }
                @Override
                public ItemStack pickupBlock(@Nullable LivingEntity entity, LevelAccessor level, BlockPos pos, BlockState state) {
                    return pickupBlock(level, pos, state);
                }
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
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
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    return onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, null, isMoving);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
            };
        } else if (def.isRoseVines()) {
            return new RoseVinesBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    return state;
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    dropStoredContents(level, pos);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool != null && tool.count() > 0);
                }
            };
        } else if (def.isDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new DecoratedPotBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    dropStoredContents(level, pos);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool != null && tool.count() > 0);
                }
            };
        } else if (def.isFestiveStocking()) {
            return new FestiveStockingBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isCascadeNoMist()) {
            return new CascadeBlockNoMist(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
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
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
                @Override
                protected boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                public MapCodec<? extends HalfTransparentBlock> codec() {
                    return codecForDefinition(def, HalfTransparentBlock.class);
                }
            };
        } else if (def.isIcicleCauldron()) {
            return new IcicleCauldronBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isIcicleBlock()) {
            props.friction(0.989F);
            return new IcicleBlock(props) {
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
            };
        } else if (def.isPackedIcicleBlock()) {
            props.friction(0.989F);
            return new PackedIcicleBlock(props) {
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
                    return true;
                }
            };
        } else if (def.isPotentSulfur()) {
            return new PotentSulfurBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    return onUpdateShape(state, level, pos);
                }

                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, pos, isMoving);
                }

                @Override
                protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
                    super.onPlace(state, level, pos, oldState, movedByPiston);
                    onPlaced(state, level, pos);
                }

                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextFloat);
                }

                @Override
                protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
                    return onTriggerEvent(level, pos);
                }
            };
        } else if (def.isSulfurSpike()) {
            return new V26xSulfurSpikeBlock(props);
        } else if (def.isBoneDice()) {
            return new BoneDiceBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isStar()) {
            return new StarBlock(props) {
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    if (direction == Direction.DOWN) {
                        state = state.setValue(ON_FARMLAND, state.getValue(VERTICAL_DIRECTION) == Direction.UP && shouldSink(level, pos));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
                    onEntityInside(level, pos, state, entity);
                    super.entityInside(state, level, pos, entity, applier, isMoving);
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
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isCushion()) {
            return new CushionBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
                    if (entity.isSuppressingBounce()) {
                        super.fallOn(level, state, pos, entity, (float) fallDistance);
                    } else {
                        entity.causeFallDamage((float) fallDistance, 0.0F, level.damageSources().fall());
                    }
                }
                @Override
                public float getBounceRestitution() {
                    return 0.6F;
                }
            };
        } else if (def.isBigBook()) {
            return new BigBookBlock(props) {
                @Override
                public MapCodec<? extends HorizontalDirectionalBlock> codec() {
                    return codecForDefinition(def, HorizontalDirectionalBlock.class);
                }
            };
        } else if (def.isBigCandle()) {
            props.lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? BigCandleBlock.LIGHT_LEVEL : 0);
            return new BigCandleBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected int getLightDampening(BlockState state) {
                    return 0;
                }
                @Override
                protected boolean propagatesSkylightDown(BlockState state) {
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
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                public MapCodec<? extends Block> codec() {
                    return codecForDefinition(def, Block.class);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
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
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return getBeaconColor(state, level, pos, beaconPos);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return getBeaconColor(state, level, pos, beaconPos); }
                @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) { if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); if (!canSurvive(state, level, pos)) tickAccess.scheduleTick(pos, this, 1); return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random); }
                @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return onInteract(level, pos, state, player, hand, hit); }
                @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit); }
            };
        } else if (def.isStringLight()) {
            return new StringLightBlock(props) {
                @Override
                public MapCodec<? extends Block> codec() {
                    return codecForDefinition(def, Block.class);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        tickAccess.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isExperienceCauldron()) {
            return new LayeredCauldronBlock(Biome.Precipitation.NONE, CauldronInteractions.EMPTY, props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                        Player player, InteractionHand hand, BlockHitResult hit) {
                    return com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceCauldronInteractions.use(
                            stack, state, level, pos, player, hand);
                }
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
                public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isMulticolorGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new MulticolorGlowLightsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        scheduledTicks.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new GlowLightsBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (state.getValue(WATERLOGGED)) {
                        scheduledTicks.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
            };
        } else if (def.isEyeblossom()) {
            boolean isOpen = def.getId().contains("open");
            class V26xEyeblossomBlock extends EyeblossomBlock implements BonemealableBlock {
                public V26xEyeblossomBlock(BlockBehaviour.Properties properties) {
                    super(isOpen, properties);
                }
                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onBlockTick(state, level, pos);
                }
                @Override
                protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
                    onBlockRemoved(level, pos, state);
                    super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
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
                    performBonemeal(level, pos);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xEyeblossomBlock(props);
        } else if (def.isFrostRose()) {
            return new FrostRoseBlock(props) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (!canSurvive(adjusted, level, pos)) tickAccess.scheduleTick(pos, this, 1);
                    return super.updateShape(adjusted, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isStrawBed()) {
            return new StrawBedBlock(props) {
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                    return res != InteractionResult.PASS ? res : super.useWithoutItem(state, level, pos, player, hit);
                }
            };
        } else if (def.isCactusFlower()) {
            return new CactusFlowerBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isGoldenDandelion()) {
            return new GoldenDandelionBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isHangingMoss()) {
            props.randomTicks();
            class V26xHangingMossBlock extends HangingMossBlock implements BonemealableBlock {
                public V26xHangingMossBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    if (direction == Direction.UP && !state.canSurvive(level, pos)) {
                        return Blocks.AIR.defaultBlockState();
                    }
                    if (direction == Direction.DOWN) {
                        return state.setValue(TIP, !adjacentState.is(this));
                    }
                    return super.updateShape(state, level, scheduledTicks, pos, direction, adjacentPos, adjacentState, random);
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
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    if (stack.getItem() instanceof net.minecraft.world.item.ShearsItem) {
                        BlockPos tipPos = findTip(level, pos, this);
                        BlockState tipState = level.getBlockState(tipPos);
                        if (tipState.hasProperty(SHEARED) && !tipState.getValue(SHEARED)) {
                            if (!level.isClientSide()) {
                                level.setBlockAndUpdate(tipPos, tipState.setValue(SHEARED, true));
                                level.playSound(null, tipPos, net.minecraft.sounds.SoundEvents.GROWING_PLANT_CROP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                                net.minecraft.world.entity.EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                                        ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                                        : net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                                stack.hurtAndBreak(1, player, slot);
                            }
                            return InteractionResult.SUCCESS;
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
            return new V26xHangingMossBlock(props);
        } else if (def.isClover()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V26xCloverBlock extends CloverBlock implements BonemealableBlock {
                public V26xCloverBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }

                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
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
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xCloverBlock(props);
        } else if (def.isPetal()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V26xPetalBlock extends PetalBlock implements BonemealableBlock {
                public V26xPetalBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }

                @Override
                protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean isMoving) {
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
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xPetalBlock(props);
        } else if (def.isWildflowers()) {
            props.sound(SoundType.GRASS).noCollision();
            class V26xWildflowersBlock extends WildflowersBlock implements BonemealableBlock {
                public V26xWildflowersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
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
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xWildflowersBlock(props);
        } else if (def.isColoredMoss()) {
            Supplier<Block> carpet = resolveBlockSupplier(def.getId().replace("_block", "_carpet"));
            Supplier<Block> overlay = resolveBlockSupplier(def.getId().replace("_block", "_overlay"));
            Supplier<Block> layers = resolveBlockSupplier(def.getId().replace("_block", "_layers"));
            Supplier<Block> sapling = resolveBlockSupplier(def.getParentBlockId());
            class V26xColoredMossBlock extends ColoredMossBlock implements BonemealableBlock {
                public V26xColoredMossBlock(BlockBehaviour.Properties properties) {
                    super(properties, carpet, overlay, layers, sapling, null);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
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
                public MapCodec<Block> codec() {
                    return codecForDefinition(def, Block.class);
                }
            }
            return new V26xColoredMossBlock(props);
        } else if (def.isColoredSporeBlossom()) {
            int colorRGB = getSporeBlossomColor(def.getId());
            class V26xColoredSporeBlossomBlock extends ColoredSporeBlossomBlock {
                public V26xColoredSporeBlossomBlock(BlockBehaviour.Properties properties) {
                    super(properties, colorRGB);
                }

                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }

                @Override
                public MapCodec<SporeBlossomBlock> codec() {
                    return codecForDefinition(def, SporeBlossomBlock.class);
                }
            }
            return new V26xColoredSporeBlossomBlock(props);
        } else if (def.isCreakingHeart()) {
            Supplier<Block> resinClumpSupplier = resolveBlockSupplier("resin_clump");
            class V26xCreakingHeartBlock extends CreakingHeartBlock {
                public V26xCreakingHeartBlock(BlockBehaviour.Properties properties) {
                    super(properties, resinClumpSupplier);
                }

                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, InteractionHand.MAIN_HAND);
                    return result != InteractionResult.PASS ? result : super.useWithoutItem(state, level, pos, player, hit);
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onScheduledTick(state, level, pos);
                }

                @Override
                public MapCodec<RotatedPillarBlock> codec() {
                    return codecForDefinition(def, RotatedPillarBlock.class);
                }
            }
            return new V26xCreakingHeartBlock(props);
        } else if (def.isDryGrass()) {
            Supplier<Block> tallDryGrass = resolveBlockSupplier("tall_dry_grass");
            class V26xDryGrassBlock extends DryGrassBlock implements BonemealableBlock {
                public V26xDryGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, tallDryGrass);
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
                    growTall(level, pos);
                }

                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xDryGrassBlock(props);
        } else if (def.isTallDryGrass()) {
            return new TallDryGrassBlock(props) {
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isMonetFlower()) {
            class V26xMonetFlowerBlock extends MonetFlowerBlock implements BonemealableBlock {
                public V26xMonetFlowerBlock(BlockBehaviour.Properties properties) {
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
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    return super.updateShape(adjusted, level, tickAccess, pos, direction, adjacentPos, adjacentState, random);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xMonetFlowerBlock(props);
        } else if (def.isSnowyBush()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyBushBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isSnowyFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V26xSnowyFernBlock extends SnowyFernBlock implements BonemealableBlock {
                public V26xSnowyFernBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyLargeFern);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
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
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xSnowyFernBlock(props);
        } else if (def.isSnowyShortGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            class V26xSnowyShortGrassBlock extends SnowyShortGrassBlock implements BonemealableBlock {
                public V26xSnowyShortGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyTallGrass);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
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
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            }
            return new V26xSnowyShortGrassBlock(props);
        } else if (def.isSnowyLargeFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyLargeFernBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    BlockState updated = super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(state, tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }
                @Override
                public MapCodec<? extends net.minecraft.world.level.block.DoublePlantBlock> codec() {
                    return codecForDefinition(def, net.minecraft.world.level.block.DoublePlantBlock.class);
                }
            };
        } else if (def.isSnowyTallGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyTallGrassBlock(props, snowBricks) {
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
                    BlockState updated = super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(state, tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }
                @Override
                public MapCodec<? extends net.minecraft.world.level.block.DoublePlantBlock> codec() {
                    return codecForDefinition(def, net.minecraft.world.level.block.DoublePlantBlock.class);
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
            class V26xSnowyGrassBlock extends SnowyGrassBlock implements BonemealableBlock {
                public V26xSnowyGrassBlock(BlockBehaviour.Properties properties) {
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
                    if (aboveState.getFluidState().isFull()) {
                        return false;
                    }
                    int dampening = net.minecraft.world.level.lighting.LightEngine.getLightDampeningInto(
                            state,
                            aboveState,
                            Direction.UP,
                            aboveState.getLightDampening()
                    );
                    return dampening < 15;
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
                    net.minecraft.world.item.ItemInstance tool = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
                    return getReferenceDrops(tool instanceof ItemStack is ? is : ItemStack.EMPTY);
                }

                @Override
                public MapCodec<? extends Block> codec() {
                    return codecForDefinition(def, Block.class);
                }
            }
            return new V26xSnowyGrassBlock(props);
        } else if (def.isExperienceFluid()) {
            return new ExperienceFluidBlock(com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids::still,
                    props.noLootTable().replaceable().noOcclusion()) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    super.animateTick(state, level, pos, random);
                    if (random.nextInt(30) == 0) {
                        spawnXpParticle(level, pos, random.nextDouble(), random.nextDouble());
                    }
                }
            };
        } else if (def.isFireflyBush()) {
            return new FireflyBushBlock(props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
                    boolean isNight = Services.PLATFORM.isNight(level);
                    boolean noSkylight = level.getBrightness(LightLayer.SKY, pos) == 0;
                    if (!(isNight || noSkylight) || random.nextFloat() >= 0.70F) {
                        return;
                    }
                    for (int i = 0; i < 10; i++) {
                        BlockPos targetPos = pos.offset(random.nextInt(11) - 5, random.nextInt(6), random.nextInt(11) - 5);
                        if (level.getBlockState(targetPos).isAir()) {
                            level.addParticle(
                                    ModParticles.FIREFLY.get(),
                                    targetPos.getX() + random.nextDouble(),
                                    targetPos.getY() + random.nextDouble(),
                                    targetPos.getZ() + random.nextDouble(),
                                    (random.nextFloat() - 0.5F) * 0.01D,
                                    (random.nextFloat() - 0.5F) * 0.005D,
                                    (random.nextFloat() - 0.5F) * 0.01D);
                            return;
                        }
                    }
                }

                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }

                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
                }
            };
        } else if (def.isSilkTouchOnlyGlass()) {
            return new SilkTouchOnlyGlassBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return state.getMapColor(level, pos).col;
                }
            };
        } else if (def.isSilkTouchOnlyPane()) {
            return new SilkTouchOnlyPaneBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, InteractionHand.MAIN_HAND, hit);
                }
                public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    return state.getMapColor(level, pos).col;
                }
            };
        } else if (def.isResinClump()) {
            props.lightLevel(state -> 7);
            return new ResinClumpBlock(props) {
                @Override
                protected MapCodec<? extends net.minecraft.world.level.block.MultifaceBlock> codec() {
                    return codecForDefinition(def, net.minecraft.world.level.block.MultifaceBlock.class);
                }
            };
        } else if (def.isSculkVein()) {
            return new SculkVeinBlock(props) {
                @Override
                protected MapCodec<? extends net.minecraft.world.level.block.MultifaceBlock> codec() {
                    return codecForDefinition(def, net.minecraft.world.level.block.MultifaceBlock.class);
                }
            };
        } else if (def.isWallpaperFlat()) {
            return new WallpaperFlatBlock(props) {
                @Override
                protected MapCodec<? extends net.minecraft.world.level.block.MultifaceBlock> codec() {
                    return codecForDefinition(def, net.minecraft.world.level.block.MultifaceBlock.class);
                }
            };
        } else if (def.isSculkCatalyst()) {
            props.lightLevel(state -> state.getValue(SculkCatalystBlock.BLOOM) ? 6 : 0);
            return new SculkCatalystBlock(props) {
                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onCatalystTick(state, level, pos);
                }
            };
        } else if (def.isPointedIcicle()) {
            return new PointedIcicleBlock(props) {
                @Override
                protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    onIcicleTick(state, level, pos);
                }
                @Override
                protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos adjacentPos, BlockState adjacentState, RandomSource random) {
                    return onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
                    onNeighborUpdate(state, level, pos, neighborBlock, pos, isMoving);
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
                    return codecForDefinition(def, Block.class);
                }
            };
        } else if (def.isPlant()) {
            return new ModBushBlock(props) {
                @Override
                protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
                }
                @Override
                public MapCodec<BushBlock> codec() {
                    return codecForDefinition(def, BushBlock.class);
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
            cleanId = cleanId.replace(")", "").trim();
            if (cleanId.contains(":")) {
                cleanId = cleanId.substring(cleanId.indexOf(":") + 1);
            }
            Identifier rl = Identifier.parse("minecraft:" + cleanId);
            Block b = BuiltInRegistries.BLOCK.getValue(rl);
            if (b != null && b != Blocks.AIR) {
                return b;
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
            Identifier rl = Identifier.fromNamespaceAndPath(namespace, path);
            Block b = BuiltInRegistries.BLOCK.getValue(rl);
            if (b != null && b != Blocks.AIR) return b;
            Identifier mcRl = Identifier.fromNamespaceAndPath("minecraft", path);
            b = BuiltInRegistries.BLOCK.getValue(mcRl);
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
            Identifier rl = Identifier.fromNamespaceAndPath(namespace, path);
            Item it = BuiltInRegistries.ITEM.getValue(rl);
            if (it != null && it != Items.AIR) return it;
            Identifier mcRl = Identifier.fromNamespaceAndPath("minecraft", path);
            it = BuiltInRegistries.ITEM.getValue(mcRl);
            if (it != null && it != Items.AIR) return it;
            return null;
        };
    }

    private FlowingFluid resolveFlowingFluid(String id) {
        if (id == null) return (FlowingFluid) Fluids.WATER;
        String cleanId = id.toLowerCase().replace("fluids.", "").replace("modfluids.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        Identifier rl = Identifier.fromNamespaceAndPath(namespace, path);
        Fluid f = BuiltInRegistries.FLUID.getValue(rl);
        if (f instanceof FlowingFluid flowing && f != Fluids.EMPTY) {
            return flowing;
        }
        return (FlowingFluid) Fluids.WATER;
    }
    private BlockState getBaseState(BlockDefinition def) {
        return getBaseBlock(def).defaultBlockState();
    }
    /**
     * Vanilla 26.2 properties codecs carry no settings (Properties.CODEC is a unit codec).
     * Rebuild from the definition so the registered ID, constructor settings and custom
     * overrides survive decoding instead of producing a plain vanilla superclass.
     */
    private static <B extends Block> MapCodec<B> codecForDefinition(BlockDefinition definition, Class<B> type) {
        return Block.simpleCodec(ignoredProperties -> {
            Block[] decoded = new Block[1];
            Services.PLATFORM.wrapRegistryAction(() -> decoded[0] = new BlockFactory().createBlock(definition));
            return type.cast(decoded[0]);
        });
    }
    private BlockBehaviour.Properties buildProperties(BlockDefinition def) {
        CommonBlockProperties cp = def.getProperties();
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of().setId(
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(com.kingodogo.buildscape.BuildscapeCommon.MOD_ID, def.getId())));
        props.strength(cp.getHardness(), cp.getResistance());
        if (cp.requiresCorrectTool()) {
            props.requiresCorrectToolForDrops();
        }
        if (cp.isNoCollision()) {
            props.noCollision();
        }
        if (cp.isNoOcclusion()) {
            props.noOcclusion();
        }
        if (cp.getLightLevel() > 0) {
            final int light = cp.getLightLevel();
            props.lightLevel(state -> light);
        }
        props.sound(resolveSoundType(cp.getSound()));
        if (cp.getMapColor() != null) {
            props.mapColor(resolveMapColor(cp.getMapColor()));
        }
        return props;
    }
    private SoundType resolveSoundType(String sound) {
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
    private MapColor resolveMapColor(String color) {
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

    private static class CopperChest extends ChestBlock implements ICopperChestBlock, ICommonRemoval {
        private final boolean isWaxed;

        public CopperChest(boolean isWaxed, BlockBehaviour.Properties properties) {
            super(() -> (BlockEntityType<? extends ChestBlockEntity>) (Object) ModBlockEntities.COPPER_CHEST_TYPE, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, isWaxed ? properties : properties.randomTicks());
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
        protected boolean isRandomlyTicking(BlockState state) {
            return !isWaxed;
        }

        @Override
        protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
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
        protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
            if (level.getBlockState(pos).getBlock() instanceof ICopperChestBlock) {
                level.updateNeighbourForOutputSignal(pos, this);
                return;
            }
            super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
        }
    }
    public final class V26xSulfurSpikeBlock extends PointedDripstoneBlock implements SulfurSpikeBlock {
    public V26xSulfurSpikeBlock(BlockBehaviour.Properties properties) {
        super(Blocks.DRIPSTONE_BLOCK.defaultBlockState(), properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(BlockStateProperties.VERTICAL_DIRECTION);
        BlockPos supportPos = direction == Direction.DOWN ? pos.above() : pos.below();
        BlockState support = level.getBlockState(supportPos);
        boolean pointed = support.getBlock() instanceof PointedDripstoneBlock;
        boolean sameDirection = pointed
                && support.getValue(BlockStateProperties.VERTICAL_DIRECTION) == direction;
        return SulfurSpikeLogic.canSurvive(
                support.getBlock() instanceof SlabBlock,
                support.isFaceSturdy(level, supportPos,
                        direction == Direction.DOWN ? Direction.DOWN : Direction.UP),
                pointed,
                sameDirection);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockPos pos = clickedPos.relative(clickedFace);
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        SulfurSpikeLogic.VerticalDirection chosen = SulfurSpikeLogic.placementDirection(
                sulfurDirection(level.getBlockState(clickedPos)), clickFace(clickedFace),
                sulfurDirection(below), sulfurDirection(above),
                above.isFaceSturdy(level, pos.above(), Direction.DOWN),
                below.isFaceSturdy(level, pos.below(), Direction.UP));
        Direction nativeDirection = chosen == SulfurSpikeLogic.VerticalDirection.DOWN
                ? Direction.DOWN : Direction.UP;
        return applyThickness(level, pos, defaultBlockState().setValue(
                BlockStateProperties.VERTICAL_DIRECTION, nativeDirection));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) SulfurSpikeLogic.onPlaced(new Access(level), pos, state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, net.minecraft.util.RandomSource random) {
        if (direction != Direction.UP && direction != Direction.DOWN) return state;
        if (level instanceof Level world) {
            return SulfurSpikeLogic.updateVerticalShape(new Access(world), pos, state);
        }
        return applyThickness(level, pos, state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction direction = state.getValue(BlockStateProperties.VERTICAL_DIRECTION);
        BlockPos supportPos = direction == Direction.DOWN ? pos.above() : pos.below();
        BlockState support = level.getBlockState(supportPos);
        SulfurSpikeLogic.scheduledSurvivalTick(new Access(level), pos, state,
                canSurvive(state, level, pos),
                SulfurSpikeLogic.supportGone(
                        support.isFaceSturdy(level, supportPos,
                                direction == Direction.DOWN ? Direction.DOWN : Direction.UP),
                        support.getBlock() instanceof PointedDripstoneBlock,
                        support.getBlock() instanceof SlabBlock),
                level.getBlockState(pos.below()).canBeReplaced());
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos,
                                               boolean isMoving) {
        if (!isMoving) SulfurSpikeLogic.removed(new Access(level), pos, state);
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   net.minecraft.world.level.block.Block neighborBlock,
                                   net.minecraft.world.level.redstone.Orientation orientation,
                                   boolean isMoving) {
        if (!level.isClientSide()) {
            SulfurSpikeLogic.neighborChanged(new Access(level), pos, state, true);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        SulfurSpikeLogic.randomGrowthTick(new Access(level), pos, state, random.nextFloat());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) return;
        SulfurSpikeLogic.FluidKind fluid = SulfurSpikeLogic.dripParticleFluid(
                new Access(level), pos, state, random.nextInt(3));
        if (fluid != SulfurSpikeLogic.FluidKind.EMPTY) {
            level.addParticle(fluid == SulfurSpikeLogic.FluidKind.WATER
                            ? ParticleTypes.DRIPPING_WATER : ParticleTypes.DRIPPING_LAVA,
                    pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, 0D, 0D, 0D);
        }
    }

    @Override protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos,
                                                   CollisionContext context) { return Shapes.empty(); }
    @Override protected boolean useShapeForLightOcclusion(BlockState state) { return false; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) { return 1.0F; }
    @Override protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return adjacent.is(this) || super.skipRendering(state, adjacent, side);
    }

    private BlockState applyThickness(LevelReader level, BlockPos pos, BlockState state) {
        return SulfurSpikeLogic.applyThickness(new SulfurSpikeLogic.NativeStateAccess() {
            public BlockState state(BlockPos target) { return level.getBlockState(target); }
            public boolean isSulfur(BlockState candidate) { return candidate.is(V26xSulfurSpikeBlock.this); }
            public boolean isPointed(BlockState candidate) {
                return candidate.getBlock() instanceof PointedDripstoneBlock;
            }
            public SulfurSpikeLogic.VerticalDirection direction(BlockState candidate) {
                return toShared(candidate.getValue(BlockStateProperties.VERTICAL_DIRECTION));
            }
            public SulfurSpikeLogic.Thickness thickness(BlockState candidate) {
                return SulfurSpikeLogic.Thickness.valueOf(
                        candidate.getValue(BlockStateProperties.SPELEOTHEM_THICKNESS).name());
            }
            public BlockState withThickness(BlockState candidate, SulfurSpikeLogic.Thickness thickness) {
                return candidate.setValue(BlockStateProperties.SPELEOTHEM_THICKNESS,
                        SpeleothemThickness.valueOf(thickness.name()));
            }
        }, pos, state);
    }

    private SulfurSpikeLogic.VerticalDirection sulfurDirection(BlockState state) {
        return state.is(this) ? toShared(state.getValue(BlockStateProperties.VERTICAL_DIRECTION)) : null;
    }

    private static SulfurSpikeLogic.ClickFace clickFace(Direction direction) {
        if (direction == Direction.UP) return SulfurSpikeLogic.ClickFace.UP;
        if (direction == Direction.DOWN) return SulfurSpikeLogic.ClickFace.DOWN;
        return SulfurSpikeLogic.ClickFace.SIDE;
    }

    private static SulfurSpikeLogic.VerticalDirection toShared(Direction direction) {
        return direction == Direction.DOWN ? SulfurSpikeLogic.VerticalDirection.DOWN
                : SulfurSpikeLogic.VerticalDirection.UP;
    }

    private final class Access implements SulfurSpikeLogic.NativeMutationAccess {
        private final Level level;

        private Access(Level level) { this.level = level; }
        public BlockState state(BlockPos pos) { return level.getBlockState(pos); }
        public boolean isSulfur(BlockState state) { return state.is(V26xSulfurSpikeBlock.this); }
        public boolean isPointed(BlockState state) { return state.getBlock() instanceof PointedDripstoneBlock; }
        public SulfurSpikeLogic.VerticalDirection direction(BlockState state) {
            return toShared(state.getValue(BlockStateProperties.VERTICAL_DIRECTION));
        }
        public SulfurSpikeLogic.Thickness thickness(BlockState state) {
            return SulfurSpikeLogic.Thickness.valueOf(
                    state.getValue(BlockStateProperties.SPELEOTHEM_THICKNESS).name());
        }
        public BlockState withThickness(BlockState state, SulfurSpikeLogic.Thickness thickness) {
            return state.setValue(BlockStateProperties.SPELEOTHEM_THICKNESS,
                    SpeleothemThickness.valueOf(thickness.name()));
        }
        public void setState(BlockPos pos, BlockState state, int flags) { level.setBlock(pos, state, flags); }
        public void scheduleTick(BlockPos pos, int delay) {
            if (level instanceof ScheduledTickAccess ticks) {
                ticks.scheduleTick(pos, V26xSulfurSpikeBlock.this, delay);
            }
        }
        public void destroy(BlockPos pos, boolean drop) { level.destroyBlock(pos, drop); }
        public boolean isAir(BlockState state) { return state.isAir(); }
        public boolean isSulfurSource(BlockState state) {
            com.kingodogo.buildscape.util.CommonId id = Services.PLATFORM.getBlockId(state.getBlock());
            if (id == null || !"buildscape".equals(id.getNamespace())) return false;
            String path = id.getPath();
            return path.equals("sulfur") || path.equals("polished_sulfur")
                    || path.equals("chiseled_sulfur") || path.equals("sulfur_bricks");
        }
        public SulfurSpikeLogic.FluidKind fluidAt(BlockPos pos) {
            net.minecraft.world.level.material.FluidState fluid = level.getFluidState(pos);
            if (fluid.is(FluidTags.WATER)) return SulfurSpikeLogic.FluidKind.WATER;
            if (fluid.is(FluidTags.LAVA)) return SulfurSpikeLogic.FluidKind.LAVA;
            return SulfurSpikeLogic.FluidKind.EMPTY;
        }
        public BlockState newState(SulfurSpikeLogic.VerticalDirection direction,
                                   SulfurSpikeLogic.Thickness thickness) {
            return defaultBlockState()
                    .setValue(BlockStateProperties.VERTICAL_DIRECTION,
                            direction == SulfurSpikeLogic.VerticalDirection.DOWN ? Direction.DOWN : Direction.UP)
                    .setValue(BlockStateProperties.SPELEOTHEM_THICKNESS,
                            SpeleothemThickness.valueOf(thickness.name()));
        }
        public void notifyVanillaPointedNeighbor(BlockPos target, BlockPos source) {
            BlockState state = level.getBlockState(target);
            if (state.getBlock() instanceof PointedDripstoneBlock && !state.is(V26xSulfurSpikeBlock.this)) {
                level.neighborChanged(state, target, V26xSulfurSpikeBlock.this, null, false);
            }
        }
    }
}
}

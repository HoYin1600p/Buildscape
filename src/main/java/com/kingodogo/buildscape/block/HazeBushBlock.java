package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HazeBushBlock extends ModBushBlock {

    public static final BooleanProperty HAS_HAZE = BooleanProperty.create("has_haze");
    private final DyeColor color;

    public HazeBushBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_HAZE, true));
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static float[] getPastelColor(@Nullable DyeColor color) {
        if (color == null) {
            return new float[]{0.94F, 0.94F, 0.94F};
        }
        return switch (color) {
            case WHITE -> new float[]{0.96F, 0.96F, 0.98F};
            case ORANGE -> new float[]{1.00F, 0.80F, 0.62F};
            case MAGENTA -> new float[]{0.92F, 0.68F, 0.88F};
            case LIGHT_BLUE -> new float[]{0.72F, 0.86F, 0.98F};
            case YELLOW -> new float[]{1.00F, 0.94F, 0.65F};
            case LIME -> new float[]{0.75F, 0.95F, 0.70F};
            case PINK -> new float[]{1.00F, 0.78F, 0.85F};
            case GRAY -> new float[]{0.62F, 0.65F, 0.70F};
            case LIGHT_GRAY -> new float[]{0.82F, 0.84F, 0.87F};
            case CYAN -> new float[]{0.68F, 0.92F, 0.92F};
            case PURPLE -> new float[]{0.80F, 0.70F, 0.92F};
            case BLUE -> new float[]{0.65F, 0.76F, 0.95F};
            case BROWN -> new float[]{0.80F, 0.70F, 0.62F};
            case GREEN -> new float[]{0.65F, 0.85F, 0.68F};
            case RED -> new float[]{0.98F, 0.68F, 0.70F};
            case BLACK -> new float[]{0.35F, 0.35F, 0.38F};
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_HAZE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        boolean hasHaze = true;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                if (tag.contains("BlockStateTag") && tag.getCompound("BlockStateTag").contains("has_haze")) {
                    hasHaze = !"false".equalsIgnoreCase(tag.getCompound("BlockStateTag").getString("has_haze"));
                } else if (tag.contains("HasHaze")) {
                    hasHaze = tag.getBoolean("HasHaze");
                }
            }
        }
        return this.defaultBlockState().setValue(HAS_HAZE, hasHaze);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        super.animateTick(state, level, pos, random);

        if (!state.getValue(HAS_HAZE)) {
            return;
        }

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.options.particles == net.minecraft.client.ParticleStatus.MINIMAL) {
            return;
        }

        com.kingodogo.buildscape.client.HazeBushParticleHandler.track(pos);

        if (mc.options.particles == net.minecraft.client.ParticleStatus.DECREASED && random.nextFloat() > 0.5F) {
            return;
        }

        float[] rgb = getPastelColor(this.color);

        int count = (mc.options.particles == net.minecraft.client.ParticleStatus.DECREASED) ? 1 : (1 + random.nextInt(2));
        for (int i = 0; i < count; i++) {
            net.minecraft.world.phys.Vec3 particlePos = com.kingodogo.buildscape.client.HazeBushParticleHandler.findHazeParticlePos(level, pos, random);
            if (particlePos != null) {
                level.addParticle(ModParticles.HAZE.get(), particlePos.x, particlePos.y, particlePos.z, rgb[0], rgb[1], rgb[2]);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide) {
            com.kingodogo.buildscape.client.HazeBushParticleHandler.track(pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.isClientSide) {
                com.kingodogo.buildscape.client.HazeBushParticleHandler.untrack(pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(Items.GLASS_BOTTLE) && state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, false), 3);

                level.playSound(null, pos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 1.2F);

                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack bottleOfMist = new ItemStack(ModItems.BOTTLE_OF_MIST.get());
                if (held.isEmpty()) {
                    player.setItemInHand(hand, bottleOfMist);
                } else {
                    if (!player.getInventory().add(bottleOfMist)) {
                        player.drop(bottleOfMist, false);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();
            if (this.color != dyeColor) {
                if (!level.isClientSide) {
                    RegistryObject<Block> targetBlock = ModBlocks.COLORED_HAZE_BUSHES.get(dyeColor);
                    if (targetBlock != null) {
                        boolean hadHaze = state.getValue(HAS_HAZE);
                        level.setBlock(pos, targetBlock.get().defaultBlockState().setValue(HAS_HAZE, hadHaze), 3);
                        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (held.is(ModItems.BOTTLE_OF_MIST.get()) && !state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, true), 3);

                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 0.8F);

                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (held.isEmpty()) {
                    player.setItemInHand(hand, emptyBottle);
                } else {
                    if (!player.getInventory().add(emptyBottle)) {
                        player.drop(emptyBottle, false);
                    }
                }
                level.levelEvent(2005, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.is(Items.BONE_MEAL) && !state.getValue(HAS_HAZE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HAS_HAZE, true), 3);
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(2005, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ItemStack stack = new ItemStack(this);
        if (!state.getValue(HAS_HAZE)) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putString("has_haze", "false");
            stack.addTagElement("BlockStateTag", blockStateTag);
            stack.getOrCreateTag().putBoolean("HasHaze", false);
        }
        return Collections.singletonList(stack);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        if (!state.getValue(HAS_HAZE)) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putString("has_haze", "false");
            stack.addTagElement("BlockStateTag", blockStateTag);
            stack.getOrCreateTag().putBoolean("HasHaze", false);
        }
        return stack;
    }
}

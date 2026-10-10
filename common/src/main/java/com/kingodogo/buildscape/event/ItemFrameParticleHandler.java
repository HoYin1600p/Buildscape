package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.PillarSparkleParticle;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
public final class ItemFrameParticleHandler {

    private static final String PARTICLE_PATTERN_KEY = "BuildScapeParticlePattern";
    private static final String PARTICLE_ENABLED_KEY = "BuildScapeParticleEnabled";
    private static final String PARTICLE_COLORS_KEY = "BuildScapeParticleColors";
    private static final String FRAME_ID_KEY = "BuildScapeFrameId";
    private static final String FRAME_PREFIX = "F-";

    private static final Map<Integer, String> CLIENT_PATTERN_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, List<String>> CLIENT_COLOR_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, String> CLIENT_FRAME_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> COLOR_COUNTERS = new ConcurrentHashMap<>();

    private static final String PATTERN_NOT_SET = "__not_set__";
    private static final String[] PATTERNS = {
            "none",
            "default",
            "beam",
            "spiral",
            "fountain",
            "pulse",
            "ring",
            "burst",
            "snowflake"
    };

    public static final int MAX_DYE_COLORS = 5;

    private ItemFrameParticleHandler() {}

    public static void clearClientCaches() {
        CLIENT_PATTERN_CACHE.clear();
        CLIENT_COLOR_CACHE.clear();
        CLIENT_FRAME_ID_CACHE.clear();
        COLOR_COUNTERS.clear();
    }

    public static void clearCaches() {
        clearClientCaches();
    }

    public static void clearCaches(int entityId) {
        CLIENT_PATTERN_CACHE.remove(entityId);
        CLIENT_COLOR_CACHE.remove(entityId);
        CLIENT_FRAME_ID_CACHE.remove(entityId);
        COLOR_COUNTERS.remove(entityId);
    }

    public static void clearPatternCache(int entityId) {
        CLIENT_PATTERN_CACHE.remove(entityId);
    }

    public static void onEntityJoin(Entity entity, Level level) {
        if (level == null || level.isClientSide()) return;

        if (entity instanceof ItemFrame itemFrame) {
            PillarIdManager.get(level).registerItemFrame(itemFrame);
        } else if (entity instanceof ColoredItemFrameEntity coloredFrame) {
            PillarIdManager.get(level).registerColoredItemFrame(coloredFrame);
        }
    }

    public static void onEntityLeave(Entity entity, Level level) {
        if (level == null || level.isClientSide()) return;

        if (entity instanceof ItemFrame || entity instanceof ColoredItemFrameEntity) {
            if (entity.isRemoved()) {
                CompoundTag data = Services.PLATFORM.getEntityData(entity);
                String frameId = Services.PLATFORM.getTagString(data, FRAME_ID_KEY, null);
                if (frameId != null && !frameId.isEmpty()) {
                    PillarIdManager.get(level).removePillar(frameId);
                    clearCaches(entity.getId());
                }
            }
        }
    }

    public static InteractionResult onEntityInteract(Player player, Level level, InteractionHand hand, Entity target) {
        if (player == null || level == null || target == null) return InteractionResult.PASS;
        if (!(target instanceof ItemFrame) && !(target instanceof ColoredItemFrameEntity)) {
            return InteractionResult.PASS;
        }
        if (!player.mayBuild() || !level.mayInteract(player, target.blockPosition())) {
            return InteractionResult.PASS;
        }

        if (target instanceof ColoredItemFrameEntity coloredFrame) {
            return handleColoredItemFrameInteraction(player, level, hand, coloredFrame);
        } else if (target instanceof ItemFrame itemFrame) {
            return handleItemFrameInteraction(player, level, hand, itemFrame);
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult handleItemFrameInteraction(Player player, Level level, InteractionHand hand, ItemFrame itemFrame) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                List<String> currentColors = getParticleColors(itemFrame);
                if (currentColors.size() >= MAX_DYE_COLORS) {
                    if (!level.isClientSide()) {
                        Services.PLATFORM.sendActionBarMessage(player,
                                Services.PLATFORM.literal("Item frame already has " + MAX_DYE_COLORS + " colors!").withStyle(ChatFormatting.RED));
                    }
                    return Services.PLATFORM.sidedSuccess(level.isClientSide());
                }

                addParticleColor(itemFrame, dyeColor);
                PillarIdManager.get(level).registerItemFrame(itemFrame, true);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                if (!level.isClientSide()) {
                    level.playSound(null, itemFrame.getX(), itemFrame.getY(), itemFrame.getZ(),
                            SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    String frameId = getFrameId(itemFrame);
                    int colorCount = getParticleColors(itemFrame).size();
                    Services.PLATFORM.sendActionBarMessage(player,
                            Services.PLATFORM.literal("[" + frameId + "] + " + dyeName + " (" + colorCount + "/" + MAX_DYE_COLORS + ")"));
                }

                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }
        }

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        String currentPattern = getParticlePattern(itemFrame);
        String nextPattern = cyclePattern(currentPattern);

        setParticlePattern(itemFrame, nextPattern);
        PillarIdManager.get(level).registerItemFrame(itemFrame, true);

        if (!level.isClientSide()) {
            level.playSound(null, itemFrame.getX(), itemFrame.getY(), itemFrame.getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.5f, 1.0f);

            ChatFormatting color = getPatternColor(nextPattern);
            Services.PLATFORM.sendActionBarMessage(player,
                    Services.PLATFORM.literal(nextPattern).withStyle(color));
        }

        return Services.PLATFORM.sidedSuccess(level.isClientSide());
    }

    private static InteractionResult handleColoredItemFrameInteraction(Player player, Level level, InteractionHand hand, ColoredItemFrameEntity coloredFrame) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                List<String> currentColors = getParticleColorsColored(coloredFrame);
                if (currentColors.size() >= MAX_DYE_COLORS) {
                    if (!level.isClientSide()) {
                        Services.PLATFORM.sendActionBarMessage(player,
                                Services.PLATFORM.literal("Item frame already has " + MAX_DYE_COLORS + " colors!").withStyle(ChatFormatting.RED));
                    }
                    return Services.PLATFORM.sidedSuccess(level.isClientSide());
                }

                addParticleColorColored(coloredFrame, dyeColor);
                PillarIdManager.get(level).registerColoredItemFrame(coloredFrame, true);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                if (!level.isClientSide()) {
                    level.playSound(null, coloredFrame.getX(), coloredFrame.getY(), coloredFrame.getZ(),
                            SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    String frameId = getFrameIdColored(coloredFrame);
                    int colorCount = getParticleColorsColored(coloredFrame).size();
                    Services.PLATFORM.sendActionBarMessage(player,
                            Services.PLATFORM.literal("[" + frameId + "] + " + dyeName + " (" + colorCount + "/" + MAX_DYE_COLORS + ")"));
                }

                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            }
        }

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        String currentPattern = getParticlePatternColored(coloredFrame);
        String nextPattern = cyclePattern(currentPattern);

        setParticlePatternColored(coloredFrame, nextPattern);
        PillarIdManager.get(level).registerColoredItemFrame(coloredFrame, true);

        if (!level.isClientSide()) {
            level.playSound(null, coloredFrame.getX(), coloredFrame.getY(), coloredFrame.getZ(),
                    SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.5f, 1.0f);

            ChatFormatting color = getPatternColor(nextPattern);
            Services.PLATFORM.sendActionBarMessage(player,
                    Services.PLATFORM.literal(nextPattern).withStyle(color));
        }

        return Services.PLATFORM.sidedSuccess(level.isClientSide());
    }

    public static void clientTick(Level level, Vec3 playerPos, Iterable<Entity> entities, long gameTime) {
        if (level == null || playerPos == null || gameTime % 5 != 0) return;

        double maxDistance = 32.0;
        double maxDistSq = maxDistance * maxDistance;
        PillarParticleConfig cfg = PillarParticleConfig.get();

        for (Entity entity : entities) {
            if (entity instanceof ItemFrame itemFrame) {
                if (itemFrame.getItem().isEmpty()) continue;
                if (itemFrame.position().distanceToSqr(playerPos) > maxDistSq) continue;
                if (!cfg.matches(itemFrame.getItem())) continue;

                String pattern = getParticlePattern(itemFrame);
                if (pattern.equals(PATTERN_NOT_SET) || pattern.equals("none")) continue;

                spawnItemFrameParticles(level, itemFrame, pattern, cfg, gameTime);
            } else if (entity instanceof ColoredItemFrameEntity coloredFrame) {
                if (coloredFrame.getItem().isEmpty()) continue;
                if (coloredFrame.position().distanceToSqr(playerPos) > maxDistSq) continue;
                if (!cfg.matches(coloredFrame.getItem())) continue;

                String pattern = getParticlePatternColored(coloredFrame);
                if (pattern.equals(PATTERN_NOT_SET) || pattern.equals("none")) continue;

                spawnColoredItemFrameParticles(level, coloredFrame, pattern, cfg, gameTime);
            }
        }
    }

    public static void clientTick(Level level, Player player, long gameTime) {
        if (level == null || player == null || gameTime % 5 != 0) return;
        Vec3 playerPos = player.position();
        AABB box = player.getBoundingBox().inflate(32.0);
        List<Entity> nearby = level.getEntitiesOfClass(Entity.class, box);
        clientTick(level, playerPos, nearby, gameTime);
    }

    private static void spawnItemFrameParticles(Level level, ItemFrame itemFrame, String pattern, PillarParticleConfig cfg, long time) {
        Random rand = new Random();
        Direction facing = itemFrame.getDirection();
        Vec3 normal = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        double centerX = itemFrame.getX() + normal.x * 0.0625;
        double centerY = itemFrame.getY() + normal.y * 0.0625;
        double centerZ = itemFrame.getZ() + normal.z * 0.0625;

        int baseCount = cfg.particle_density;
        int count = Math.max(1, cfg.use_pattern ? (int) Math.round(baseCount * cfg.pattern_intensity) : baseCount);
        double speed = cfg.use_pattern ? cfg.pattern_speed * cfg.pattern_intensity : cfg.particle_speed;
        double spread = cfg.use_pattern ? cfg.pattern_spread : cfg.particle_spread;

        Vec3 upVec, rightVec;
        if (facing == Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == Direction.DOWN) {
            upVec = new Vec3(0, 0, 1);
            rightVec = new Vec3(1, 0, 0);
        } else {
            upVec = new Vec3(0, 1, 0);
            rightVec = normal.cross(upVec).normalize();
        }

        for (int i = 0; i < count; i++) {
            double u, v, w, vu, vv, vw;

            switch (pattern) {
                case "beam":
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 0.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = speed * (0.8 + rand.nextDouble() * 0.4);
                    break;
                case "spiral":
                    double angle = (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    u = Math.cos(angle) * radius;
                    v = Math.sin(angle) * radius;
                    w = 0.0;
                    vu = Math.cos(angle) * speed * 0.3;
                    vv = Math.sin(angle) * speed * 0.3;
                    vw = speed * 0.6;
                    break;
                case "fountain":
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    u = Math.cos(fAngle) * fRadius;
                    v = Math.sin(fAngle) * fRadius;
                    w = rand.nextDouble() * spread * 0.5;
                    vu = Math.cos(fAngle) * speed * 0.5;
                    vv = Math.sin(fAngle) * speed * 0.5;
                    vw = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                    break;
                case "pulse":
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    u = Math.cos(pAngle) * pulseRadius;
                    v = Math.sin(pAngle) * pulseRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.5;
                    vu = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vv = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    vw = speed * 0.2;
                    break;
                case "ring":
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    u = Math.cos(rAngle) * rRadius;
                    v = Math.sin(rAngle) * rRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vu = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vv = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    vw = speed * 0.3;
                    break;
                case "burst":
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.7;
                    u = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.6;
                    v = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.6;
                    w = Math.sin(bElevation) * spread * 0.6;
                    vu = Math.cos(bAngle) * Math.cos(bElevation) * speed * 1.5;
                    vv = Math.sin(bAngle) * Math.cos(bElevation) * speed * 1.5;
                    vw = Math.sin(bElevation) * speed * 1.5;
                    break;
                case "snowflake":
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = -speed * (0.8 + rand.nextDouble() * 0.4);
                    break;
                default:
                    u = (rand.nextDouble() - 0.5) * spread;
                    v = (rand.nextDouble() - 0.5) * spread;
                    w = rand.nextDouble() * spread;
                    vu = (rand.nextDouble() - 0.5) * speed;
                    vv = (rand.nextDouble() - 0.5) * speed;
                    vw = rand.nextDouble() * speed;
                    break;
            }

            double sx = rightVec.x * u + upVec.x * v + normal.x * w;
            double sy = rightVec.y * u + upVec.y * v + normal.y * w;
            double sz = rightVec.z * u + upVec.z * v + normal.z * w;

            double vx = rightVec.x * vu + upVec.x * vv + normal.x * vw;
            double vy = rightVec.y * vu + upVec.y * vv + normal.y * vw;
            double vz = rightVec.z * vu + upVec.z * vv + normal.z * vw;

            double particleX = centerX + sx;
            double particleY = centerY + sy;
            double particleZ = centerZ + sz;

            boolean isSnowflake = "snowflake".equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get()
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            if (!isSnowflake) {
                String colorCode = getNextColor(itemFrame, cfg);
                PillarSparkleParticle.queueColor(particleX, particleY, particleZ, colorCode);
            }

            level.addParticle(particleType, particleX, particleY, particleZ, vx, vy, vz);
        }
    }

    private static void spawnColoredItemFrameParticles(Level level, ColoredItemFrameEntity coloredFrame, String pattern, PillarParticleConfig cfg, long time) {
        Random rand = new Random();
        Direction facing = coloredFrame.getDirection();
        Vec3 normal = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        double centerX = coloredFrame.getX() + normal.x * 0.0625;
        double centerY = coloredFrame.getY() + normal.y * 0.0625;
        double centerZ = coloredFrame.getZ() + normal.z * 0.0625;

        int baseCount = cfg.particle_density;
        int count = Math.max(1, cfg.use_pattern ? (int) Math.round(baseCount * cfg.pattern_intensity) : baseCount);
        double speed = cfg.use_pattern ? cfg.pattern_speed * cfg.pattern_intensity : cfg.particle_speed;
        double spread = cfg.use_pattern ? cfg.pattern_spread : cfg.particle_spread;

        Vec3 upVec, rightVec;
        if (facing == Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == Direction.DOWN) {
            upVec = new Vec3(0, 0, 1);
            rightVec = new Vec3(1, 0, 0);
        } else {
            upVec = new Vec3(0, 1, 0);
            rightVec = normal.cross(upVec).normalize();
        }

        for (int i = 0; i < count; i++) {
            double u, v, w, vu, vv, vw;

            switch (pattern) {
                case "beam":
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 0.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = speed * (0.8 + rand.nextDouble() * 0.4);
                    break;
                case "spiral":
                    double angle = (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
                    double radius = spread * 0.5;
                    u = Math.cos(angle) * radius;
                    v = Math.sin(angle) * radius;
                    w = 0.0;
                    vu = Math.cos(angle) * speed * 0.3;
                    vv = Math.sin(angle) * speed * 0.3;
                    vw = speed * 0.6;
                    break;
                case "fountain":
                    double fAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double fRadius = rand.nextDouble() * spread;
                    u = Math.cos(fAngle) * fRadius;
                    v = Math.sin(fAngle) * fRadius;
                    w = rand.nextDouble() * spread * 0.5;
                    vu = Math.cos(fAngle) * speed * 0.5;
                    vv = Math.sin(fAngle) * speed * 0.5;
                    vw = speed * 0.3 - rand.nextDouble() * speed * 0.2;
                    break;
                case "pulse":
                    double pulsePhase = (time * 0.2) % (2.0 * Math.PI);
                    double pulseRadius = spread * (0.3 + Math.sin(pulsePhase) * 0.7);
                    double pAngle = rand.nextDouble() * 2.0 * Math.PI;
                    u = Math.cos(pAngle) * pulseRadius;
                    v = Math.sin(pAngle) * pulseRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.5;
                    vu = Math.cos(pAngle) * speed * Math.sin(pulsePhase);
                    vv = Math.sin(pAngle) * speed * Math.sin(pulsePhase);
                    vw = speed * 0.2;
                    break;
                case "ring":
                    double rAngle = ((i * 2.0 * Math.PI) / count) + (time * 0.05);
                    double rRadius = spread * 0.8;
                    u = Math.cos(rAngle) * rRadius;
                    v = Math.sin(rAngle) * rRadius;
                    w = (rand.nextDouble() - 0.5) * spread * 0.3;
                    vu = Math.cos(rAngle + Math.PI / 2) * speed * 0.4;
                    vv = Math.sin(rAngle + Math.PI / 2) * speed * 0.4;
                    vw = speed * 0.3;
                    break;
                case "burst":
                    double bAngle = rand.nextDouble() * 2.0 * Math.PI;
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.5;
                    u = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    v = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    w = Math.sin(bElevation) * spread * 0.3;
                    vu = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vv = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    vw = Math.sin(bElevation) * speed;
                    break;
                case "snowflake":
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0;
                    vu = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vv = (rand.nextDouble() - 0.5) * speed * 0.2;
                    vw = -speed * (0.8 + rand.nextDouble() * 0.4);
                    break;
                default:
                    u = (rand.nextDouble() - 0.5) * spread;
                    v = (rand.nextDouble() - 0.5) * spread;
                    w = rand.nextDouble() * spread;
                    vu = (rand.nextDouble() - 0.5) * speed;
                    vv = (rand.nextDouble() - 0.5) * speed;
                    vw = rand.nextDouble() * speed;
                    break;
            }

            double sx = rightVec.x * u + upVec.x * v + normal.x * w;
            double sy = rightVec.y * u + upVec.y * v + normal.y * w;
            double sz = rightVec.z * u + upVec.z * v + normal.z * w;

            double vx = rightVec.x * vu + upVec.x * vv + normal.x * vw;
            double vy = rightVec.y * vu + upVec.y * vv + normal.y * vw;
            double vz = rightVec.z * vu + upVec.z * vv + normal.z * vw;

            double particleX = centerX + sx;
            double particleY = centerY + sy;
            double particleZ = centerZ + sz;

            boolean isSnowflake = "snowflake".equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get()
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            if (!isSnowflake) {
                String colorCode = getNextColorColored(coloredFrame, cfg);
                PillarSparkleParticle.queueColor(particleX, particleY, particleZ, colorCode);
            }

            level.addParticle(particleType, particleX, particleY, particleZ, vx, vy, vz);
        }
    }

    public static List<String> getParticleColors(ItemFrame itemFrame) {
        Level level = Services.PLATFORM.getEntityLevel(itemFrame);
        if (level != null && level.isClientSide()) {
            List<String> cached = CLIENT_COLOR_CACHE.get(itemFrame.getId());
            if (cached != null) return cached;

            CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
            if (data != null && data.contains(PARTICLE_COLORS_KEY)) {
                List<String> colors = Services.PLATFORM.getTagStringList(data, PARTICLE_COLORS_KEY);
                if (colors != null && !colors.isEmpty()) {
                    List<String> sub = colors.subList(0, Math.min(colors.size(), MAX_DYE_COLORS));
                    CLIENT_COLOR_CACHE.put(itemFrame.getId(), sub);
                    return sub;
                }
            }

            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData = PillarIdManager.get(level).getPillarData(frameId);
                if (managerData != null && managerData.dyeColors != null && !managerData.dyeColors.isEmpty()) {
                    List<String> mutableColors = new ArrayList<>(managerData.dyeColors);
                    CLIENT_COLOR_CACHE.put(itemFrame.getId(), mutableColors);
                    return mutableColors;
                }
            }

            return new ArrayList<>();
        }

        CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
        List<String> colors = new ArrayList<>();
        if (data != null && data.contains(PARTICLE_COLORS_KEY)) {
            List<String> list = Services.PLATFORM.getTagStringList(data, PARTICLE_COLORS_KEY);
            if (list != null) {
                for (int i = 0; i < list.size() && i < MAX_DYE_COLORS; i++) {
                    colors.add(list.get(i));
                }
            }
        }
        return colors;
    }

    public static void addParticleColor(ItemFrame itemFrame, String color) {
        List<String> colors = getParticleColors(itemFrame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(Locale.ROOT));
        CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);

        CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
        if (data != null) {
            Services.PLATFORM.putTagStringList(data, PARTICLE_COLORS_KEY, colors);
        }
    }

    private static String getNextColor(ItemFrame itemFrame, PillarParticleConfig cfg) {
        int entityId = itemFrame.getId();
        List<String> customColors = getParticleColors(itemFrame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = counter % customColors.size();
            COLOR_COUNTERS.put(entityId, counter + 1);

            String color = customColors.get(colorIndex);
            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                return color.toUpperCase(Locale.ROOT);
            }
        }

        if (cfg.particle_color == null || cfg.particle_color.isEmpty()) {
            return "#FFFFFF";
        }

        int maxColors = Math.max(1, Math.min(7, Math.min(cfg.max_particle_color, cfg.particle_color.size())));
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = counter % maxColors;
        COLOR_COUNTERS.put(entityId, counter + 1);

        if (colorIndex < 0 || colorIndex >= cfg.particle_color.size()) {
            colorIndex = 0;
        }

        String color = cfg.particle_color.get(colorIndex);
        if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
            return color.toUpperCase(Locale.ROOT);
        }
        return "#FFFFFF";
    }

    public static String getParticlePattern(ItemFrame itemFrame) {
        Level level = Services.PLATFORM.getEntityLevel(itemFrame);
        if (level != null && level.isClientSide()) {
            String cached = CLIENT_PATTERN_CACHE.get(itemFrame.getId());
            if (cached != null) return cached;

            CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
            if (data != null && data.contains(PARTICLE_PATTERN_KEY)) {
                String pattern = Services.PLATFORM.getTagString(data, PARTICLE_PATTERN_KEY, null);
                if (pattern != null && !pattern.isEmpty()) {
                    CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);
                    return pattern;
                }
            }

            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData = PillarIdManager.get(level).getPillarData(frameId);
                if (managerData != null && managerData.pattern != null && !managerData.pattern.isEmpty()) {
                    CLIENT_PATTERN_CACHE.put(itemFrame.getId(), managerData.pattern);
                    return managerData.pattern;
                }
            }

            return "none";
        }

        CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
        if (data != null && data.contains(PARTICLE_PATTERN_KEY)) {
            String p = Services.PLATFORM.getTagString(data, PARTICLE_PATTERN_KEY, "none");
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        return "none";
    }

    public static void setParticlePattern(ItemFrame itemFrame, String pattern) {
        CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);

        Level level = Services.PLATFORM.getEntityLevel(itemFrame);
        if (level != null && !level.isClientSide()) {
            CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
            if (data != null) {
                data.putString(PARTICLE_PATTERN_KEY, pattern);
                if (!data.contains(FRAME_ID_KEY)) {
                    String frameId = generateFrameId(PillarIdManager.get(level));
                    data.putString(FRAME_ID_KEY, frameId);
                    CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
                }
            }
        }
    }

    public static boolean hasCustomColors(ItemFrame itemFrame) {
        return !getParticleColors(itemFrame).isEmpty();
    }

    public static boolean hasCustomColorsColored(ColoredItemFrameEntity frame) {
        return !getParticleColorsColored(frame).isEmpty();
    }

    public static String getFrameId(ItemFrame itemFrame) {
        Level level = Services.PLATFORM.getEntityLevel(itemFrame);
        if (level != null && level.isClientSide()) {
            String cached = CLIENT_FRAME_ID_CACHE.get(itemFrame.getId());
            if (cached != null && !cached.equals("F-????")) return cached;

            String dimension = PillarIdManager.getDimensionKey(level);
            BlockPos pos = itemFrame.blockPosition();
            Direction dir = itemFrame.getDirection();

            String exactKey = PillarIdManager.get(level).positionKey(dimension, pos, dir);
            String idFromPos = PillarIdManager.get(level).getIdForPosition(exactKey);

            if (idFromPos == null) {
                String fuzzyKey = PillarIdManager.get(level).positionKey(dimension, pos, null);
                String potentialId = PillarIdManager.get(level).getIdForPosition(fuzzyKey);
                if (potentialId != null && potentialId.startsWith(FRAME_PREFIX)) {
                    idFromPos = potentialId;
                }
            }

            if (idFromPos != null) {
                CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), idFromPos);
                return idFromPos;
            }

            return "F-????";
        }

        CompoundTag data = Services.PLATFORM.getEntityData(itemFrame);
        if (data != null && data.contains(FRAME_ID_KEY)) {
            String id = Services.PLATFORM.getTagString(data, FRAME_ID_KEY, null);
            if (id != null && !id.isEmpty()) return id;
        }

        String frameId = generateFrameId(PillarIdManager.get(level));
        if (data != null) {
            data.putString(FRAME_ID_KEY, frameId);
        }
        CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
        return frameId;
    }

    public static String getParticlePatternColored(ColoredItemFrameEntity frame) {
        Level level = frame.getEntityLevel();
        if (level != null && level.isClientSide()) {
            String cached = CLIENT_PATTERN_CACHE.get(frame.getId());
            if (cached != null) return cached;

            String pattern = frame.getParticlePattern();
            if (pattern != null && !pattern.isEmpty() && !"none".equals(pattern)) {
                CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
                return pattern;
            }

            String frameId = getFrameIdColored(frame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData = PillarIdManager.get(level).getPillarData(frameId);
                if (managerData != null) {
                    pattern = (managerData.pattern == null || managerData.pattern.isEmpty()) ? "none" : managerData.pattern;
                    CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
                    return pattern;
                }
            }

            return "none";
        }

        CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
        if (data != null && data.contains(PARTICLE_PATTERN_KEY)) {
            String p = Services.PLATFORM.getTagString(data, PARTICLE_PATTERN_KEY, "none");
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        if (data != null && data.contains("PATTERN")) {
            String p = Services.PLATFORM.getTagString(data, "PATTERN", "none");
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        return "none";
    }

    public static void setParticlePatternColored(ColoredItemFrameEntity frame, String pattern) {
        CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
        frame.setParticlePattern(pattern);

        Level level = frame.getEntityLevel();
        if (level != null && !level.isClientSide()) {
            CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
            if (data != null) {
                data.putString(PARTICLE_PATTERN_KEY, pattern);
            }
        }
    }

    public static List<String> getParticleColorsColored(ColoredItemFrameEntity frame) {
        Level level = frame.getEntityLevel();
        if (level != null && level.isClientSide()) {
            List<String> cached = CLIENT_COLOR_CACHE.get(frame.getId());
            if (cached != null) return cached;

            CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
            if (data != null && data.contains(PARTICLE_COLORS_KEY)) {
                List<String> colors = Services.PLATFORM.getTagStringList(data, PARTICLE_COLORS_KEY);
                if (colors != null && !colors.isEmpty()) {
                    List<String> sub = colors.subList(0, Math.min(colors.size(), MAX_DYE_COLORS));
                    CLIENT_COLOR_CACHE.put(frame.getId(), sub);
                    return sub;
                }
            }

            String colorsStr = frame.getParticleColorsRaw();
            if (colorsStr != null && !colorsStr.isEmpty()) {
                List<String> synchronizedColors = new ArrayList<>(Arrays.asList(colorsStr.split(";")));
                if (!synchronizedColors.isEmpty()) {
                    CLIENT_COLOR_CACHE.put(frame.getId(), synchronizedColors);
                    return synchronizedColors;
                }
            }

            return new ArrayList<>();
        }

        CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
        List<String> colors = new ArrayList<>();
        if (data != null && data.contains(PARTICLE_COLORS_KEY)) {
            List<String> list = Services.PLATFORM.getTagStringList(data, PARTICLE_COLORS_KEY);
            if (list != null) {
                for (int i = 0; i < list.size() && i < MAX_DYE_COLORS; i++) {
                    String color = list.get(i);
                    if (color != null && !color.isEmpty()) {
                        colors.add(color);
                    }
                }
            }
        }
        return colors;
    }

    public static void addParticleColorColored(ColoredItemFrameEntity frame, String color) {
        List<String> colors = getParticleColorsColored(frame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(Locale.ROOT));
        CLIENT_COLOR_CACHE.put(frame.getId(), colors);

        Level level = frame.getEntityLevel();
        if (level != null && !level.isClientSide()) {
            CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
            if (data != null) {
                Services.PLATFORM.putTagStringList(data, PARTICLE_COLORS_KEY, colors);
            }
            frame.setParticleColorsRaw(String.join(";", colors));

            if (data != null && !data.contains(FRAME_ID_KEY)) {
                String colorVariant = frame.getColorVariant();
                String frameId = generateColoredFrameId(colorVariant, PillarIdManager.get(level));
                data.putString(FRAME_ID_KEY, frameId);
                CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
            }
        }
    }

    public static String getFrameIdColored(ColoredItemFrameEntity frame) {
        Level level = frame.getEntityLevel();
        if (level != null && level.isClientSide()) {
            String cached = CLIENT_FRAME_ID_CACHE.get(frame.getId());
            if (cached != null && !cached.equals("F-????")) return cached;

            String dimension = PillarIdManager.getDimensionKey(level);
            BlockPos pos = frame.blockPosition();
            Direction dir = frame.getDirection();

            String exactKey = PillarIdManager.get(level).positionKey(dimension, pos, dir);
            String idFromPos = PillarIdManager.get(level).getIdForPosition(exactKey);

            if (idFromPos == null) {
                String fuzzyKey = PillarIdManager.get(level).positionKey(dimension, pos, null);
                String potentialId = PillarIdManager.get(level).getIdForPosition(fuzzyKey);
                if (potentialId != null && potentialId.startsWith(FRAME_PREFIX)) {
                    idFromPos = potentialId;
                }
            }

            if (idFromPos != null) {
                CLIENT_FRAME_ID_CACHE.put(frame.getId(), idFromPos);
                return idFromPos;
            }

            return "F-????";
        }

        CompoundTag data = Services.PLATFORM.getEntityData(frame.asEntity());
        if (data != null && data.contains(FRAME_ID_KEY)) {
            String id = Services.PLATFORM.getTagString(data, FRAME_ID_KEY, null);
            if (id != null && !id.isEmpty()) return id;
        }

        String colorVariant = frame.getColorVariant();
        String frameId = generateColoredFrameId(colorVariant, PillarIdManager.get(level));
        if (data != null) {
            data.putString(FRAME_ID_KEY, frameId);
        }
        CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
        return frameId;
    }

    private static String getNextColorColored(ColoredItemFrameEntity frame, PillarParticleConfig cfg) {
        int entityId = frame.getId();
        List<String> customColors = getParticleColorsColored(frame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = counter % customColors.size();
            COLOR_COUNTERS.put(entityId, counter + 1);

            String color = customColors.get(colorIndex);
            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                return color.toUpperCase(Locale.ROOT);
            }
        }

        if (cfg.particle_color == null || cfg.particle_color.isEmpty()) {
            return "#E8FEFD";
        }

        int maxColors = Math.max(1, Math.min(7, Math.min(cfg.max_particle_color, cfg.particle_color.size())));
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = counter % maxColors;
        COLOR_COUNTERS.put(entityId, counter + 1);

        if (colorIndex < 0 || colorIndex >= cfg.particle_color.size()) {
            colorIndex = 0;
        }

        String color = cfg.particle_color.get(colorIndex);
        if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
            return color.toUpperCase(Locale.ROOT);
        }
        return "#E8FEFD";
    }

    public static String reassignFrameId(Entity frame) {
        if (frame == null) return "F-????";
        CompoundTag data = Services.PLATFORM.getEntityData(frame);
        if (data != null) {
            data.remove(FRAME_ID_KEY);
        }
        CLIENT_FRAME_ID_CACHE.remove(frame.getId());
        if (frame instanceof ColoredItemFrameEntity coloredFrame) {
            return getFrameIdColored(coloredFrame);
        }
        if (frame instanceof ItemFrame itemFrame) {
            return getFrameId(itemFrame);
        }
        return "F-????";
    }

    public static String reassignFrameId(ItemFrame frame) {
        return reassignFrameId((Entity) frame);
    }

    public static String reassignFrameIdColored(ColoredItemFrameEntity frame) {
        return reassignFrameId(frame.asEntity());
    }

    private static String generateFrameId(PillarIdManager manager) {
        return generateUniqueId(FRAME_PREFIX, manager);
    }

    private static String generateColoredFrameId(String colorVariant, PillarIdManager manager) {
        return generateUniqueId("I-F" + getColorCode(colorVariant), manager);
    }

    private static String generateUniqueId(String prefix, PillarIdManager manager) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 64; attempt++) {
            String id = prefix + String.format("%04X", random.nextInt(0x10000));
            if (manager == null || !manager.isIdTaken(id)) {
                return id;
            }
        }
        String id;
        do {
            id = prefix + String.format("%08X", random.nextInt() & 0x7FFFFFFF);
        } while (manager != null && manager.isIdTaken(id));
        return id;
    }

    private static String getColorCode(String colorName) {
        if (colorName == null || colorName.isEmpty()) return "W";
        switch (colorName.toLowerCase(Locale.ROOT)) {
            case "white": return "W";
            case "orange": return "O";
            case "magenta": return "M";
            case "light_blue": return "LB";
            case "yellow": return "Y";
            case "lime": return "L";
            case "pink": return "P";
            case "gray": return "GR";
            case "light_gray": return "LG";
            case "cyan": return "C";
            case "purple": return "PU";
            case "blue": return "B";
            case "brown": return "BR";
            case "green": return "G";
            case "red": return "R";
            case "black": return "BL";
            case "invisible": return "I";
            default: return "W";
        }
    }

    private static String cyclePattern(String current) {
        if (current == null || current.equals(PATTERN_NOT_SET)) {
            return "none";
        }

        int currentIndex = -1;
        for (int i = 0; i < PATTERNS.length; i++) {
            if (PATTERNS[i].equals(current)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            currentIndex = 0;
        }

        return PATTERNS[(currentIndex + 1) % PATTERNS.length];
    }

    private static ChatFormatting getPatternColor(String pattern) {
        if (pattern == null) return ChatFormatting.WHITE;

        switch (pattern) {
            case "none": return ChatFormatting.DARK_GRAY;
            case "default": return ChatFormatting.WHITE;
            case "beam": return ChatFormatting.AQUA;
            case "spiral": return ChatFormatting.LIGHT_PURPLE;
            case "fountain": return ChatFormatting.BLUE;
            case "pulse": return ChatFormatting.RED;
            case "ring": return ChatFormatting.GOLD;
            case "burst": return ChatFormatting.YELLOW;
            case "snowflake": return ChatFormatting.AQUA;
            default: return ChatFormatting.GRAY;
        }
    }

    private static Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        net.minecraft.world.item.DyeColor dye = Services.PLATFORM.getDyeColor(stack);
        if (dye != null) {
            switch (dye) {
                case WHITE: return new AbstractMap.SimpleEntry<>("#E8FEFD", "White");
                case ORANGE: return new AbstractMap.SimpleEntry<>("#FF5C00", "Orange");
                case MAGENTA: return new AbstractMap.SimpleEntry<>("#FF00FF", "Magenta");
                case LIGHT_BLUE: return new AbstractMap.SimpleEntry<>("#3CDFFF", "Light Blue");
                case YELLOW: return new AbstractMap.SimpleEntry<>("#FFFF00", "Yellow");
                case LIME: return new AbstractMap.SimpleEntry<>("#BFFE00", "Lime");
                case PINK: return new AbstractMap.SimpleEntry<>("#F686B7", "Pink");
                case GRAY: return new AbstractMap.SimpleEntry<>("#232526", "Gray");
                case LIGHT_GRAY: return new AbstractMap.SimpleEntry<>("#B1B8C5", "Light Gray");
                case CYAN: return new AbstractMap.SimpleEntry<>("#00FFFF", "Cyan");
                case PURPLE: return new AbstractMap.SimpleEntry<>("#AB87FF", "Purple");
                case BLUE: return new AbstractMap.SimpleEntry<>("#1919EA", "Blue");
                case BROWN: return new AbstractMap.SimpleEntry<>("#411900", "Brown");
                case GREEN: return new AbstractMap.SimpleEntry<>("#39FF14", "Green");
                case RED: return new AbstractMap.SimpleEntry<>("#FF0000", "Red");
                case BLACK: return new AbstractMap.SimpleEntry<>("#07010C", "Black");
            }
        }
        return null;
    }
}


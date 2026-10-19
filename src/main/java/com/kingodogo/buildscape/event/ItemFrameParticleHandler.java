package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.PillarSparkleParticle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ItemFrameParticleHandler {

    private static final String PARTICLE_PATTERN_KEY =
            "BuildScapeParticlePattern";
    private static final String PARTICLE_ENABLED_KEY =
            "BuildScapeParticleEnabled";
    private static final String PARTICLE_COLORS_KEY = "BuildScapeParticleColors";
    private static final String FRAME_ID_KEY = "BuildScapeFrameId";
    private static final String FRAME_PREFIX = "F-";

    private static final Map<Integer, String> CLIENT_PATTERN_CACHE =
            new ConcurrentHashMap<>();

    private static final Map<Integer, List<String>> CLIENT_COLOR_CACHE =
            new ConcurrentHashMap<>();

    private static final Map<Integer, String> CLIENT_FRAME_ID_CACHE =
            new ConcurrentHashMap<>();

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
            "snowflake",
    };

    public static final int MAX_DYE_COLORS = 5;
    private static int registryCleanupTicks;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ++registryCleanupTicks < 100) return;
        registryCleanupTicks = 0;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isRunning() || !BuildScape.isServerFullyInitialized()) return;
        PillarIdManager manager = PillarIdManager.get();
        Set<String> dimensions = new HashSet<>();
        for (PillarIdManager.PillarData data : manager.getAllData()) {
            if (data.pillarType != null && (data.pillarType.endsWith(":item_frame")
                    || data.pillarType.endsWith("_item_frame"))) {
                dimensions.add(data.dimension);
            }
        }
        for (ServerLevel level : server.getAllLevels()) {
            if (dimensions.contains(PillarIdManager.getDimensionKey(level))) {
                manager.cleanupOrphans(level);
            }
        }
    }

    public static void clearClientCaches() {
        CLIENT_PATTERN_CACHE.clear();
        CLIENT_COLOR_CACHE.clear();
        CLIENT_FRAME_ID_CACHE.clear();
        COLOR_COUNTERS.clear();
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide) return;

        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame itemFrame) {
            CompoundTag data = itemFrame.getPersistentData();
            PillarIdManager.get(itemFrame.level).registerItemFrame(itemFrame);
        } else if (entity instanceof ColoredItemFrameEntity coloredFrame) {
            CompoundTag data = coloredFrame.getPersistentData();
            PillarIdManager.get(coloredFrame.level).registerColoredItemFrame(coloredFrame);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveWorldEvent event) {
        if (event.getWorld().isClientSide()) return;

        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof ColoredItemFrameEntity) {
            CompoundTag data = entity.getPersistentData();
            Entity.RemovalReason reason = entity.getRemovalReason();
            if (reason == null || !reason.shouldDestroy()) {
                return;
            }
            String frameId = data.getString("BuildScapeFrameId");
            if (frameId != null && !frameId.isEmpty()) {
                PillarIdManager.get(entity.level).unregisterDestroyedFrame(frameId);
                clearCaches(entity.getId());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(
            PlayerInteractEvent.EntityInteract event
    ) {
        if (!(event.getTarget() instanceof ItemFrame) &&
                !(event.getTarget() instanceof ColoredItemFrameEntity)) {
            return;
        }
        if (!event.getPlayer().mayBuild()
                || !event.getWorld().mayInteract(event.getPlayer(), event.getTarget().blockPosition())) {
            return;
        }

        try {
            handleFrameInteraction(event);
        } finally {
            if (event.isCanceled() && event.getCancellationResult() == InteractionResult.PASS) {
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getWorld().isClientSide));
            }
        }
    }

    private static void handleFrameInteraction(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof ColoredItemFrameEntity) {
            handleColoredItemFrameInteraction(event);
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getTarget();

        Player player = event.getPlayer();
        Level level = event.getWorld();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!itemFrame.getItem().isEmpty() && !heldItem.isEmpty() && !player.isShiftKeyDown()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                List<String> currentColors = getParticleColors(itemFrame);
                if (currentColors.size() >= MAX_DYE_COLORS) {
                    if (!level.isClientSide) {
                        TextComponent message = new TextComponent(
                                "Item frame already has " + MAX_DYE_COLORS + " colors!"
                        );
                        message.withStyle(ChatFormatting.RED);
                        player.displayClientMessage(message, true);
                    }
                    event.setCanceled(true);
                    return;
                }

                addParticleColor(itemFrame, dyeColor);
                PillarIdManager.get(itemFrame.level).registerItemFrame(itemFrame, true);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            itemFrame.getX(),
                            itemFrame.getY(),
                            itemFrame.getZ(),
                            SoundEvents.DYE_USE,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );

                    String frameId = getFrameId(itemFrame);
                    int colorCount = getParticleColors(itemFrame).size();
                    TextComponent message = new TextComponent(
                            "[" +
                                    frameId +
                                    "] + " +
                                    dyeName +
                                    " (" +
                                    colorCount +
                                    "/" +
                                    MAX_DYE_COLORS +
                                    ")"
                    );
                    player.displayClientMessage(message, true);
                }

                event.setCanceled(true);
                return;
            }
        }

        if (!player.isShiftKeyDown()) {
            return;
        }

        event.setCanceled(true);

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        String currentPattern = getParticlePattern(itemFrame);
        String nextPattern = cyclePattern(currentPattern);

        setParticlePattern(itemFrame, nextPattern);
        PillarIdManager.get(itemFrame.level).registerItemFrame(itemFrame, true);

        if (!level.isClientSide) {
            level.playSound(
                    null,
                    itemFrame.getX(),
                    itemFrame.getY(),
                    itemFrame.getZ(),
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.0f
            );

            ChatFormatting color = getPatternColor(nextPattern);
            TextComponent patternText = (TextComponent) new TextComponent(
                    nextPattern
            ).withStyle(color);
            player.displayClientMessage(patternText, true);
        }
    }



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc =
                Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        ClientLevel level = mc.level;
        long gameTime = level.getGameTime();

        if (gameTime % 5 != 0) {
            return;
        }

        Vec3 playerPos = mc.player.position();
        double maxDistance = 32.0;

        PillarParticleConfig cfg = PillarParticleConfig.get();

        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ItemFrame itemFrame) {
                if (itemFrame.getItem().isEmpty()) {
                    continue;
                }

                Vec3 framePos = itemFrame.position();
                if (framePos.distanceToSqr(playerPos) > maxDistance * maxDistance) {
                    continue;
                }

                if (!cfg.matches(itemFrame.getItem())) {
                    continue;
                }

                String pattern = getParticlePattern(itemFrame);

                spawnItemFrameParticles(level, itemFrame, pattern, cfg, gameTime);
            }
            else if (entity instanceof ColoredItemFrameEntity coloredFrame) {
                if (coloredFrame.getItem().isEmpty()) {
                    continue;
                }

                Vec3 framePos = coloredFrame.position();
                if (framePos.distanceToSqr(playerPos) > maxDistance * maxDistance) {
                    continue;
                }

                if (!cfg.matches(coloredFrame.getItem())) {
                    continue;
                }

                String pattern = getParticlePatternColored(coloredFrame);

                spawnColoredItemFrameParticles(level, coloredFrame, pattern, cfg, gameTime);
            }
        }
    }

    private record FrameSettings(String pattern, int count, double speed, double spread, int maxColors) {
    }

    private static FrameSettings resolveFrameSettings(Level level, String frameId, String pattern,
                                                       PillarParticleConfig cfg) {
        PillarIdManager.PillarData data =
                PillarIdManager.get(level).getPillarData(frameId);
        return resolveFrameSettings(data, pattern, cfg);
    }

    private static FrameSettings resolveFrameSettings(
            PillarIdManager.PillarData data,
            String pattern, PillarParticleConfig cfg) {
        if (data != null && data.pattern != null) {
            pattern = data.pattern;
        }
        if (pattern == null || pattern.isEmpty() || "default".equals(pattern)) {
            pattern = cfg.pattern;
        }
        if (pattern == null || pattern.isEmpty() || "default".equals(pattern)) {
            pattern = "none";
        }
        boolean usePattern = data != null && data.use_pattern != null ? data.use_pattern : cfg.use_pattern;
        double intensity = data != null && data.pattern_intensity != null ? data.pattern_intensity : cfg.pattern_intensity;
        double patternSpeed = data != null && data.pattern_speed != null ? data.pattern_speed : cfg.pattern_speed;
        double patternSpread = data != null && data.pattern_spread != null ? data.pattern_spread : cfg.pattern_spread;
        int maxColors = data != null && data.max_particle_color != null ? data.max_particle_color : cfg.max_particle_color;
        int count = cfg.particle_density <= 0 ? 0 : (int) Math.min(64L,
                Math.max(0L, Math.round(cfg.particle_density * (usePattern ? intensity : 1.0))));
        if ("none".equals(pattern) || PATTERN_NOT_SET.equals(pattern)) {
            count = 0;
        }
        return new FrameSettings(usePattern ? pattern : "random", count,
                usePattern ? patternSpeed * intensity : cfg.particle_speed,
                usePattern ? patternSpread : cfg.particle_spread, Math.max(1, Math.min(7, maxColors)));
    }

    private static void spawnItemFrameParticles(
            Level level,
            ItemFrame itemFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time
    ) {
        FrameSettings settings = resolveFrameSettings(level, getFrameId(itemFrame), pattern, cfg);
        if (settings.count() == 0) return;
        pattern = settings.pattern();
        Random rand = new Random();

        Direction facing = itemFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = itemFrame.getX();
        double centerY = itemFrame.getY();
        double centerZ = itemFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int count = settings.count();
        double speed = settings.speed();
        double spread = settings.spread();

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
                    double angle =
                            (time * 0.1 + (i * 2.0 * Math.PI) / count) % (2.0 * Math.PI);
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
                String colorCode = getNextColor(itemFrame, cfg, settings.maxColors());
                PillarSparkleParticle.queueColor(
                        particleX,
                        particleY,
                        particleZ,
                        colorCode
                );
            }

            level.addParticle(
                    particleType,
                    particleX,
                    particleY,
                    particleZ,
                    vx,
                    vy,
                    vz
            );
        }
    }

    private static final Map<Integer, Integer> COLOR_COUNTERS =
            new ConcurrentHashMap<>();

    public static List<String> getParticleColors(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            List<String> cached = CLIENT_COLOR_CACHE.get(itemFrame.getId());
            if (cached != null) return cached;

            CompoundTag data = itemFrame.getPersistentData();
            if (data.contains(PARTICLE_COLORS_KEY)) {
                List<String> colors = new ArrayList<>();
                ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
                for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                    colors.add(colorList.getString(i));
                }
                CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);
                return colors;
            }

            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData =
                        PillarIdManager.get(itemFrame.level).getPillarData(frameId);
                if (managerData != null && managerData.dyeColors != null && !managerData.dyeColors.isEmpty()) {
                    List<String> mutableColors = new ArrayList<>(managerData.dyeColors);
                    CLIENT_COLOR_CACHE.put(itemFrame.getId(), mutableColors);
                    return mutableColors;
                }
            }

            return new ArrayList<>();
        }

        CompoundTag data = itemFrame.getPersistentData();
        List<String> colors = new ArrayList<>();
        if (data.contains(PARTICLE_COLORS_KEY)) {
            ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
            for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                colors.add(colorList.getString(i));
            }
        }
        return colors;
    }

    public static void addParticleColor(ItemFrame itemFrame, String color) {
        List<String> colors = getParticleColors(itemFrame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(Locale.ROOT));

        CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);

        CompoundTag data = itemFrame.getPersistentData();
        ListTag colorList = new ListTag();
        for (String c : colors) {
            colorList.add(StringTag.valueOf(c));
        }
        data.put(PARTICLE_COLORS_KEY, colorList);
    }

    private static String getNextColor(
            ItemFrame itemFrame,
            PillarParticleConfig cfg,
            int colorLimit
    ) {
        int entityId = itemFrame.getId();

        List<String> customColors = getParticleColors(itemFrame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = Math.floorMod(counter, Math.min(colorLimit, customColors.size()));
            COLOR_COUNTERS.put(entityId, counter + 1);

            String color = customColors.get(colorIndex);
            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                return color.toUpperCase(Locale.ROOT);
            }
        }

        if (cfg.particle_color == null || cfg.particle_color.isEmpty()) {
            return "#FFFFFF";
        }

        int maxColors = Math.max(
                1,
                Math.min(colorLimit, cfg.particle_color.size())
        );
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = Math.floorMod(counter, maxColors);
        COLOR_COUNTERS.put(entityId, counter + 1);

        if (colorIndex < 0 || colorIndex >= cfg.particle_color.size()) {
            colorIndex = 0;
        }

        String color = cfg.particle_color.get(colorIndex);
        if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
            return color.toUpperCase();
        }
        return "#FFFFFF";
    }

    public static String getParticlePattern(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            String cached = CLIENT_PATTERN_CACHE.get(itemFrame.getId());
            if (cached != null) return cached;

            CompoundTag data = itemFrame.getPersistentData();
            if (data.contains(PARTICLE_PATTERN_KEY)) {
                String pattern = data.getString(PARTICLE_PATTERN_KEY);
                if (pattern != null && !pattern.isEmpty()) {
                    CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);
                    return pattern;
                }
            }

            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData =
                        PillarIdManager.get(itemFrame.level).getPillarData(frameId);
                if (managerData != null && managerData.pattern != null && !managerData.pattern.isEmpty()) {
                    CLIENT_PATTERN_CACHE.put(itemFrame.getId(), managerData.pattern);
                    return managerData.pattern;
                }
            }

            return "none";
        }

        CompoundTag data = itemFrame.getPersistentData();
        if (data.contains(PARTICLE_PATTERN_KEY)) {
            String p = data.getString(PARTICLE_PATTERN_KEY);
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        return "none";
    }

    public static void setParticlePattern(ItemFrame itemFrame, String pattern) {
        CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);

        if (!itemFrame.level.isClientSide) {
            CompoundTag data = itemFrame.getPersistentData();
            data.putString(PARTICLE_PATTERN_KEY, pattern);

            if (!data.contains(FRAME_ID_KEY)) {
                String frameId = generateFrameId(PillarIdManager.get(itemFrame.level));
                data.putString(FRAME_ID_KEY, frameId);
                CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
            }
        }
    }

    public static void clearPatternCache(int entityId) {
        CLIENT_PATTERN_CACHE.remove(entityId);
    }

    private static String cyclePattern(String current) {
        if (current.equals(PATTERN_NOT_SET)) {
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
        if (pattern == null) {
            return ChatFormatting.WHITE;
        }

        switch (pattern) {
            case "none":
                return ChatFormatting.DARK_GRAY;
            case "default":
                return ChatFormatting.WHITE;
            case "beam":
                return ChatFormatting.AQUA;
            case "spiral":
                return ChatFormatting.LIGHT_PURPLE;
            case "fountain":
                return ChatFormatting.BLUE;
            case "pulse":
                return ChatFormatting.RED;
            case "ring":
                return ChatFormatting.GOLD;
            case "burst":
                return ChatFormatting.YELLOW;
            case "snowflake":
                return ChatFormatting.AQUA;
            default:
                return ChatFormatting.GRAY;
        }
    }

    private static Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) return null;

        Item item = stack.getItem();

        if (item == Items.WHITE_DYE) {
            return new AbstractMap.SimpleEntry<>("#E8FEFD", "White");
        } else if (item == Items.ORANGE_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF5C00", "Orange");
        } else if (item == Items.MAGENTA_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF00FF", "Magenta");
        } else if (item == Items.LIGHT_BLUE_DYE) {
            return new AbstractMap.SimpleEntry<>("#3CDFFF", "Light Blue");
        } else if (item == Items.YELLOW_DYE) {
            return new AbstractMap.SimpleEntry<>("#FFFF00", "Yellow");
        } else if (item == Items.LIME_DYE) {
            return new AbstractMap.SimpleEntry<>("#BFFE00", "Lime");
        } else if (item == Items.PINK_DYE) {
            return new AbstractMap.SimpleEntry<>("#F686B7", "Pink");
        } else if (item == Items.GRAY_DYE) {
            return new AbstractMap.SimpleEntry<>("#232526", "Gray");
        } else if (item == Items.LIGHT_GRAY_DYE) {
            return new AbstractMap.SimpleEntry<>("#B1B8C5", "Light Gray");
        } else if (item == Items.CYAN_DYE) {
            return new AbstractMap.SimpleEntry<>("#00FFFF", "Cyan");
        } else if (item == Items.PURPLE_DYE) {
            return new AbstractMap.SimpleEntry<>("#AB87FF", "Purple");
        } else if (item == Items.BLUE_DYE) {
            return new AbstractMap.SimpleEntry<>("#1919EA", "Blue");
        } else if (item == Items.BROWN_DYE) {
            return new AbstractMap.SimpleEntry<>("#411900", "Brown");
        } else if (item == Items.GREEN_DYE) {
            return new AbstractMap.SimpleEntry<>("#39FF14", "Green");
        } else if (item == Items.RED_DYE) {
            return new AbstractMap.SimpleEntry<>("#FF0000", "Red");
        } else if (item == Items.BLACK_DYE) {
            return new AbstractMap.SimpleEntry<>("#07010C", "Black");
        }

        return null;
    }

    public static boolean hasCustomColors(ItemFrame itemFrame) {
        return !getParticleColors(itemFrame).isEmpty();
    }

    public static String getFrameId(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            String cached = CLIENT_FRAME_ID_CACHE.get(itemFrame.getId());
            if (cached != null && !cached.equals("F-????")) return cached;

            String dimension = PillarIdManager.getDimensionKey(itemFrame.level);
            BlockPos pos = itemFrame.blockPosition();
            Direction dir = itemFrame.getDirection();

            String idFromPos = PillarIdManager.get(itemFrame.level)
                    .getFrameIdForPosition(dimension, pos, dir);

            if (idFromPos != null) {
                CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), idFromPos);
                return idFromPos;
            }

            return "F-????";
        }

        CompoundTag data = itemFrame.getPersistentData();
        if (data.contains(FRAME_ID_KEY)) {
            String id = data.getString(FRAME_ID_KEY);
            if (id != null && !id.isEmpty()) return id;
        }

        String frameId = generateFrameId(PillarIdManager.get(itemFrame.level));
        data.putString(FRAME_ID_KEY, frameId);
        CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
        return frameId;
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

    public static String reassignFrameId(Entity frame) {
        frame.getPersistentData().remove(FRAME_ID_KEY);
        CLIENT_FRAME_ID_CACHE.remove(frame.getId());
        if (frame instanceof ColoredItemFrameEntity coloredFrame) {
            return getFrameIdColored(coloredFrame);
        }
        return getFrameId((ItemFrame) frame);
    }

    private static String getColorCode(String colorName) {
        if (colorName == null || colorName.isEmpty()) return "W";
        switch (colorName.toLowerCase()) {
            case "white":
                return "W";
            case "orange":
                return "O";
            case "magenta":
                return "M";
            case "light_blue":
                return "LB";
            case "yellow":
                return "Y";
            case "lime":
                return "L";
            case "pink":
                return "P";
            case "gray":
                return "GR";
            case "light_gray":
                return "LG";
            case "cyan":
                return "C";
            case "purple":
                return "PU";
            case "blue":
                return "B";
            case "brown":
                return "BR";
            case "green":
                return "G";
            case "red":
                return "R";
            case "black":
                return "BL";
            case "invisible":
                return "I";
            default:
                return "W";
        }
    }

    public static void clearCaches(int entityId) {
        CLIENT_PATTERN_CACHE.remove(entityId);
        CLIENT_COLOR_CACHE.remove(entityId);
        CLIENT_FRAME_ID_CACHE.remove(entityId);
    }

    public static void clearCaches() {
        CLIENT_PATTERN_CACHE.clear();
        CLIENT_COLOR_CACHE.clear();
        CLIENT_FRAME_ID_CACHE.clear();
        COLOR_COUNTERS.clear();
    }


    private static void handleColoredItemFrameInteraction(
            PlayerInteractEvent.EntityInteract event
    ) {
        ColoredItemFrameEntity coloredFrame =
                (ColoredItemFrameEntity) event.getTarget();

        Player player = event.getPlayer();
        Level level = event.getWorld();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!coloredFrame.getItem().isEmpty() && !heldItem.isEmpty() && !player.isShiftKeyDown()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                List<String> currentColors = getParticleColorsColored(coloredFrame);
                if (currentColors.size() >= MAX_DYE_COLORS) {
                    if (!level.isClientSide) {
                        TextComponent message = new TextComponent(
                                "Item frame already has " + MAX_DYE_COLORS + " colors!"
                        );
                        message.withStyle(ChatFormatting.RED);
                        player.displayClientMessage(message, true);
                    }
                    event.setCanceled(true);
                    return;
                }

                addParticleColorColored(coloredFrame, dyeColor);
                PillarIdManager.get(coloredFrame.level).registerColoredItemFrame(coloredFrame, true);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            coloredFrame.getX(),
                            coloredFrame.getY(),
                            coloredFrame.getZ(),
                            SoundEvents.DYE_USE,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );

                    String frameId = getFrameIdColored(coloredFrame);
                    int colorCount = getParticleColorsColored(coloredFrame).size();
                    TextComponent message = new TextComponent(
                            "[" + frameId + "] + " + dyeName + " (" + colorCount + "/" + MAX_DYE_COLORS + ")"
                    );
                    player.displayClientMessage(message, true);
                }

                event.setCanceled(true);
                return;
            }
        }

        if (!player.isShiftKeyDown()) {
            return;
        }

        event.setCanceled(true);

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        String currentPattern = getParticlePatternColored(coloredFrame);
        String nextPattern = cyclePattern(currentPattern);

        setParticlePatternColored(coloredFrame, nextPattern);
        PillarIdManager.get(coloredFrame.level).registerColoredItemFrame(coloredFrame, true);

        if (!level.isClientSide) {
            level.playSound(
                    null,
                    coloredFrame.getX(),
                    coloredFrame.getY(),
                    coloredFrame.getZ(),
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.0f
            );

            ChatFormatting color = getPatternColor(nextPattern);
            TextComponent patternText = (TextComponent) new TextComponent(nextPattern)
                    .withStyle(color);
            player.displayClientMessage(patternText, true);
        }
    }

    public static String getParticlePatternColored(
            ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            String cached = CLIENT_PATTERN_CACHE.get(frame.getId());
            if (cached != null) {
                return cached;
            }
            String pattern = frame.getParticlePattern();
            if (pattern != null && !pattern.isEmpty() && !"none".equals(pattern)) {
                CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
                return pattern;
            }

            String frameId = getFrameIdColored(frame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                PillarIdManager.PillarData managerData =
                        PillarIdManager.get(frame.level).getPillarData(frameId);
                if (managerData != null) {
                    pattern = (managerData.pattern == null || managerData.pattern.isEmpty()) ? "none" : managerData.pattern;
                    CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
                    return pattern;
                }
            }

            return "none";
        }

        CompoundTag data = frame.getPersistentData();
        if (data.contains(PARTICLE_PATTERN_KEY)) {
            String p = data.getString(PARTICLE_PATTERN_KEY);
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        if (data.contains("PATTERN", 8)) {
            String p = data.getString("PATTERN");
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        return "none";
    }

    public static void setParticlePatternColored(
            ColoredItemFrameEntity frame,
            String pattern
    ) {
        CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);

        frame.setParticlePattern(pattern);

        if (!frame.level.isClientSide) {
            CompoundTag data = frame.getPersistentData();
            data.putString(PARTICLE_PATTERN_KEY, pattern);
        }
    }

    public static List<String> getParticleColorsColored(
            ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            List<String> cached = CLIENT_COLOR_CACHE.get(frame.getId());
            if (cached != null) return cached;

            CompoundTag data = frame.getPersistentData();
            if (data.contains(PARTICLE_COLORS_KEY)) {
                List<String> colors = new ArrayList<>();
                ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
                for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                    colors.add(colorList.getString(i));
                }
                CLIENT_COLOR_CACHE.put(frame.getId(), colors);
                return colors;
            }

            String colorsStr = frame.getParticleColorsRaw();
            if (colorsStr != null && !colorsStr.isEmpty()) {
                List<String> synchronizedColors = new ArrayList<>(Arrays.asList(colorsStr.split(";")));
                if (!synchronizedColors.isEmpty()) {
                    CLIENT_COLOR_CACHE.put(frame.getId(), synchronizedColors);
                    return synchronizedColors;
                }
            }

            String frameId = getFrameIdColored(frame);

            return new ArrayList<>();
        }

        CompoundTag data = frame.getPersistentData();
        List<String> colors = new ArrayList<>();

        if (data.contains(PARTICLE_COLORS_KEY)) {
            ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
            for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                String color = colorList.getString(i);
                if (color != null && !color.isEmpty()) {
                    colors.add(color);
                }
            }
        }

        return colors;
    }

    public static void addParticleColorColored(
            ColoredItemFrameEntity frame,
            String color
    ) {
        List<String> colors = getParticleColorsColored(frame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(Locale.ROOT));

        CLIENT_COLOR_CACHE.put(frame.getId(), colors);

        if (!frame.level.isClientSide) {
            CompoundTag data = frame.getPersistentData();
            ListTag colorList = new ListTag();
            for (String c : colors) {
                colorList.add(StringTag.valueOf(c));
            }
            data.put(PARTICLE_COLORS_KEY, colorList);

            frame.setParticleColorsRaw(String.join(";", colors));

            if (!data.contains(FRAME_ID_KEY)) {
                String colorVariant = frame.getColorVariant();
                String frameId = generateColoredFrameId(colorVariant, PillarIdManager.get(frame.level));
                data.putString(FRAME_ID_KEY, frameId);
                CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
            }
        }
    }

    public static String getFrameIdColored(
            ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            String cached = CLIENT_FRAME_ID_CACHE.get(frame.getId());
            if (cached != null && !cached.equals("F-????")) return cached;

            String dimension = PillarIdManager.getDimensionKey(frame.level);
            BlockPos pos = frame.blockPosition();
            Direction dir = frame.getDirection();

            String idFromPos = PillarIdManager.get(frame.level)
                    .getFrameIdForPosition(dimension, pos, dir);

            if (idFromPos != null) {
                CLIENT_FRAME_ID_CACHE.put(frame.getId(), idFromPos);
                return idFromPos;
            }

            return "F-????";
        }

        CompoundTag data = frame.getPersistentData();
        if (data.contains(FRAME_ID_KEY)) {
            String id = data.getString(FRAME_ID_KEY);
            if (id != null && !id.isEmpty()) return id;
        }

        String colorVariant = frame.getColorVariant();
        String frameId = generateColoredFrameId(colorVariant, PillarIdManager.get(frame.level));
        data.putString(FRAME_ID_KEY, frameId);
        CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
        return frameId;
    }

    private static String getNextColorColored(
            ColoredItemFrameEntity frame,
            PillarParticleConfig cfg,
            int colorLimit
    ) {
        int entityId = frame.getId();

        List<String> customColors = getParticleColorsColored(frame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = Math.floorMod(counter, Math.min(colorLimit, customColors.size()));
            COLOR_COUNTERS.put(entityId, counter + 1);

            String color = customColors.get(colorIndex);
            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                return color.toUpperCase();
            }
        }

        if (cfg.particle_color == null || cfg.particle_color.isEmpty()) {
            return "#E8FEFD";
        }

        int maxColors = Math.max(
                1,
                Math.min(colorLimit, cfg.particle_color.size())
        );
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = Math.floorMod(counter, maxColors);
        COLOR_COUNTERS.put(entityId, counter + 1);

        if (colorIndex < 0 || colorIndex >= cfg.particle_color.size()) {
            colorIndex = 0;
        }

        String color = cfg.particle_color.get(colorIndex);
        if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
            return color.toUpperCase();
        }
        return "#E8FEFD";
    }

    private static void spawnColoredItemFrameParticles(
            Level level,
            ColoredItemFrameEntity coloredFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time
    ) {
        FrameSettings settings = resolveFrameSettings(level, getFrameIdColored(coloredFrame), pattern, cfg);
        if (settings.count() == 0) return;
        pattern = settings.pattern();
        Random rand = new Random();

        Direction facing = coloredFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = coloredFrame.getX();
        double centerY = coloredFrame.getY();
        double centerZ = coloredFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int count = settings.count();
        double speed = settings.speed();
        double spread = settings.spread();

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
                String colorCode = getNextColorColored(coloredFrame, cfg, settings.maxColors());
                PillarSparkleParticle.queueColor(
                        particleX,
                        particleY,
                        particleZ,
                        colorCode
                );
            }

            level.addParticle(
                    particleType,
                    particleX,
                    particleY,
                    particleZ,
                    vx,
                    vy,
                    vz
            );
        }
    }
}

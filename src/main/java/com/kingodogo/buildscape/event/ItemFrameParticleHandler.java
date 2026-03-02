package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<Integer, java.util.List<String>> CLIENT_COLOR_CACHE =
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

    public static void clearClientCaches() {
        CLIENT_PATTERN_CACHE.clear();
        CLIENT_COLOR_CACHE.clear();
        CLIENT_FRAME_ID_CACHE.clear();
        COLOR_COUNTERS.clear();
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide) return;

        net.minecraft.world.entity.Entity entity = event.getEntity();
        if (entity instanceof ItemFrame itemFrame) {
            CompoundTag data = itemFrame.getPersistentData();
            // Always register if it has custom data, otherwise just ensure it's tracked
            com.kingodogo.buildscape.config.PillarIdManager.get().registerItemFrame(itemFrame);
        } else if (entity instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity coloredFrame) {
            CompoundTag data = coloredFrame.getPersistentData();
            com.kingodogo.buildscape.config.PillarIdManager.get().registerColoredItemFrame(coloredFrame);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(net.minecraftforge.event.entity.EntityLeaveWorldEvent event) {
        if (event.getWorld().isClientSide()) return;

        net.minecraft.world.entity.Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity) {
            // Get the frame ID and remove from manager
            CompoundTag data = entity.getPersistentData();
            String frameId = data.getString("BuildScapeFrameId");
            if (frameId != null && !frameId.isEmpty()) {
                com.kingodogo.buildscape.config.PillarIdManager.get().removePillar(frameId);
                // Clear client caches for this entity
                clearCaches(entity.getId());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(
            PlayerInteractEvent.EntityInteract event
    ) {
        // Support both vanilla ItemFrame and our ColoredItemFrameEntity
        if (!(event.getTarget() instanceof ItemFrame) &&
                !(event.getTarget() instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity)) {
            return;
        }

        // ColoredItemFrameEntity extends HangingEntity, not ItemFrame
        // So we need to handle it separately
        if (event.getTarget() instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity) {
            handleColoredItemFrameInteraction(event);
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getTarget();

        Player player = event.getPlayer();
        Level level = event.getWorld();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                // Remove dyeing interaction as requested.
                // We only allow adding particle colors.
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                java.util.List<String> currentColors = getParticleColors(itemFrame);
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
                com.kingodogo.buildscape.config.PillarIdManager.get().registerItemFrame(itemFrame);

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
        com.kingodogo.buildscape.config.PillarIdManager.get().registerItemFrame(itemFrame);

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

        net.minecraft.client.Minecraft mc =
                net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        net.minecraft.client.multiplayer.ClientLevel level = mc.level;
        long gameTime = level.getGameTime();

        if (gameTime % 5 != 0) {
            return;
        }

        Vec3 playerPos = mc.player.position();
        double maxDistance = 32.0;

        PillarParticleConfig cfg = PillarParticleConfig.get();

        for (net.minecraft.world.entity.Entity entity : level.entitiesForRendering()) {
            // Handle vanilla ItemFrame
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

                if (pattern.equals(PATTERN_NOT_SET) || pattern.equals("none")) {
                    continue;
                }

                spawnItemFrameParticles(level, itemFrame, pattern, cfg, gameTime);
            }
            // Handle ColoredItemFrameEntity
            else if (entity instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity coloredFrame) {
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

                if (pattern.equals(PATTERN_NOT_SET) || pattern.equals("none")) {
                    continue;
                }

                spawnColoredItemFrameParticles(level, coloredFrame, pattern, cfg, gameTime);
            }
        }
    }

    private static void spawnItemFrameParticles(
            Level level,
            ItemFrame itemFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time
    ) {
        Random rand = new Random();

        net.minecraft.core.Direction facing = itemFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = itemFrame.getX();
        double centerY = itemFrame.getY();
        double centerZ = itemFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int baseCount = cfg.particle_density;
        int count = Math.max(
                1,
                cfg.use_pattern
                        ? (int) Math.round(baseCount * cfg.pattern_intensity)
                        : baseCount
        );

        double speed = cfg.use_pattern
                ? cfg.pattern_speed * cfg.pattern_intensity
                : cfg.particle_speed;
        double spread = cfg.use_pattern ? cfg.pattern_spread : cfg.particle_spread;

        Vec3 upVec, rightVec;
        if (facing == net.minecraft.core.Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == net.minecraft.core.Direction.DOWN) {
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
                    double bElevation = (rand.nextDouble() - 0.5) * Math.PI * 0.5;
                    u = Math.cos(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    v = Math.sin(bAngle) * Math.cos(bElevation) * spread * 0.3;
                    w = Math.sin(bElevation) * spread * 0.3;
                    vu = Math.cos(bAngle) * Math.cos(bElevation) * speed;
                    vv = Math.sin(bAngle) * Math.cos(bElevation) * speed;
                    vw = Math.sin(bElevation) * speed;
                    break;
                case "snowflake":
                    // Snowflake pattern: Reverse of beam pattern
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0; // Fixed 2 blocks far
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

            // Determine particle type and color queuing based on pattern
            boolean isSnowflake = "snowflake".equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get()
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            // Only queue color for non-snowflake particles
            if (!isSnowflake) {
                String colorCode = getNextColor(itemFrame, cfg);
                com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
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

    public static java.util.List<String> getParticleColors(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            java.util.List<String> cached = CLIENT_COLOR_CACHE.get(itemFrame.getId());
            if (cached != null) return cached;

            // 1. Check direct NBT on the entity (immediate response)
            CompoundTag data = itemFrame.getPersistentData();
            if (data.contains(PARTICLE_COLORS_KEY)) {
                java.util.List<String> colors = new java.util.ArrayList<>();
                net.minecraft.nbt.ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
                for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                    colors.add(colorList.getString(i));
                }
                CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);
                return colors;
            }

            // 2. Check synced MANAGER data (fallback for persistence)
            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                com.kingodogo.buildscape.config.PillarIdManager.PillarData managerData =
                        com.kingodogo.buildscape.config.PillarIdManager.get().getPillarData(frameId);
                if (managerData != null && managerData.dyeColors != null && !managerData.dyeColors.isEmpty()) {
                    java.util.List<String> mutableColors = new java.util.ArrayList<>(managerData.dyeColors);
                    CLIENT_COLOR_CACHE.put(itemFrame.getId(), mutableColors);
                    return mutableColors;
                }
            }

            return new java.util.ArrayList<>();
        }

        CompoundTag data = itemFrame.getPersistentData();
        java.util.List<String> colors = new java.util.ArrayList<>();
        if (data.contains(PARTICLE_COLORS_KEY)) {
            net.minecraft.nbt.ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
            for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                colors.add(colorList.getString(i));
            }
        }
        return colors;
    }

    public static void addParticleColor(ItemFrame itemFrame, String color) {
        java.util.List<String> colors = getParticleColors(itemFrame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(java.util.Locale.ROOT));

        CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);

        CompoundTag data = itemFrame.getPersistentData();
        net.minecraft.nbt.ListTag colorList = new net.minecraft.nbt.ListTag();
        for (String c : colors) {
            colorList.add(net.minecraft.nbt.StringTag.valueOf(c));
        }
        data.put(PARTICLE_COLORS_KEY, colorList);
    }

    private static String getNextColor(
            ItemFrame itemFrame,
            PillarParticleConfig cfg
    ) {
        int entityId = itemFrame.getId();

        java.util.List<String> customColors = getParticleColors(itemFrame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = counter % customColors.size();
            COLOR_COUNTERS.put(entityId, counter + 1);

            String color = customColors.get(colorIndex);
            if (color != null && color.matches("^#[0-9A-Fa-f]{6}$")) {
                return color.toUpperCase(java.util.Locale.ROOT);
            }
        }

        if (cfg.particle_color == null || cfg.particle_color.isEmpty()) {
            return "#FFFFFF";
        }

        int maxColors = Math.max(
                1,
                Math.min(7, Math.min(cfg.max_particle_color, cfg.particle_color.size()))
        );
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = counter % maxColors;
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

            // 1. Check direct NBT on the entity (immediate response on rejoin)
            CompoundTag data = itemFrame.getPersistentData();
            if (data.contains(PARTICLE_PATTERN_KEY)) {
                String pattern = data.getString(PARTICLE_PATTERN_KEY);
                if (pattern != null && !pattern.isEmpty()) {
                    CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);
                    return pattern;
                }
            }

            // 2. Fallback to PillarIdManager data synced from server
            String frameId = getFrameId(itemFrame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                com.kingodogo.buildscape.config.PillarIdManager.PillarData managerData =
                        com.kingodogo.buildscape.config.PillarIdManager.get().getPillarData(frameId);
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

            // Generate ID if missing during pattern set
            if (!data.contains(FRAME_ID_KEY)) {
                String frameId = generateFrameId();
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

    private static java.util.Map.Entry<String, String> getDyeColorAndName(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) return null;

        net.minecraft.world.item.Item item = stack.getItem();

        if (item == net.minecraft.world.item.Items.WHITE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#E8FEFD", "White");
        } else if (item == net.minecraft.world.item.Items.ORANGE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF5C00", "Orange");
        } else if (item == net.minecraft.world.item.Items.MAGENTA_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF00FF", "Magenta");
        } else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3CDFFF", "Light Blue");
        } else if (item == net.minecraft.world.item.Items.YELLOW_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FFFF00", "Yellow");
        } else if (item == net.minecraft.world.item.Items.LIME_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#BFFE00", "Lime");
        } else if (item == net.minecraft.world.item.Items.PINK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#F686B7", "Pink");
        } else if (item == net.minecraft.world.item.Items.GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#232526", "Gray");
        } else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#B1B8C5", "Light Gray");
        } else if (item == net.minecraft.world.item.Items.CYAN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#00FFFF", "Cyan");
        } else if (item == net.minecraft.world.item.Items.PURPLE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#AB87FF", "Purple");
        } else if (item == net.minecraft.world.item.Items.BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#1919EA", "Blue");
        } else if (item == net.minecraft.world.item.Items.BROWN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#411900", "Brown");
        } else if (item == net.minecraft.world.item.Items.GREEN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#39FF14", "Green");
        } else if (item == net.minecraft.world.item.Items.RED_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF0000", "Red");
        } else if (item == net.minecraft.world.item.Items.BLACK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#07010C", "Black");
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

            // Fallback to position-based identification on client
            String dimension = com.kingodogo.buildscape.config.PillarIdManager.getDimensionKey(itemFrame.level);
            net.minecraft.core.BlockPos pos = itemFrame.blockPosition();
            net.minecraft.core.Direction dir = itemFrame.getDirection();

            // 1. Exact match (pos + direction)
            String exactKey = com.kingodogo.buildscape.config.PillarIdManager.get().positionKey(dimension, pos, dir);
            String idFromPos = com.kingodogo.buildscape.config.PillarIdManager.get().getIdForPosition(exactKey);

            // 2. Fallback: If exact match fails, check if we're waiting for entity sync (dir might be wrong)
            if (idFromPos == null) {
                // Try to find any frame ID at this exact position if there's no direction-specific index yet
                // or if the direction is currently default (SOUTH) but the frame is actually on a side.
                // NOTE: We only do this on the client during initial identification.
                String fuzzyKey = com.kingodogo.buildscape.config.PillarIdManager.get().positionKey(dimension, pos, null);
                // ID for position without direction might return a pillar, so we must verify it's a frame ID if found
                String potentialId = com.kingodogo.buildscape.config.PillarIdManager.get().getIdForPosition(fuzzyKey);
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

        CompoundTag data = itemFrame.getPersistentData();
        if (data.contains(FRAME_ID_KEY)) {
            String id = data.getString(FRAME_ID_KEY);
            if (id != null && !id.isEmpty()) return id;
        }

        String frameId = generateFrameId();
        data.putString(FRAME_ID_KEY, frameId);
        CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
        return frameId;
    }


    private static String generateFrameId() {
        // Use a more unique ID including coordinate hash to avoid session collisions
        return FRAME_PREFIX + Long.toHexString(Double.doubleToLongBits(Math.random())).substring(8, 12).toUpperCase();
    }

    /**
     * Generate a frame ID with color code for colored item frames.
     * Format: I-F[COLOR]nnnn where COLOR is like W, LB, R, etc. and nnnn is 4 hex digits
     */
    private static String generateColoredFrameId(String colorVariant) {
        String colorCode = getColorCode(colorVariant);
        String randomPart = Long.toHexString(Double.doubleToLongBits(Math.random())).substring(8, 12).toUpperCase();
        return "I-F" + colorCode + randomPart;
    }

    /**
     * Convert color name to short code.
     */
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

    // ========== ColoredItemFrameEntity Support ==========

    private static void handleColoredItemFrameInteraction(
            PlayerInteractEvent.EntityInteract event
    ) {
        com.kingodogo.buildscape.entity.ColoredItemFrameEntity coloredFrame =
                (com.kingodogo.buildscape.entity.ColoredItemFrameEntity) event.getTarget();

        Player player = event.getPlayer();
        Level level = event.getWorld();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                // Remove re-dyeing interaction as requested.
                // We only allow adding particle colors.
                String dyeColor = dyeInfo.getKey();
                String dyeName = dyeInfo.getValue();

                java.util.List<String> currentColors = getParticleColorsColored(coloredFrame);
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
                com.kingodogo.buildscape.config.PillarIdManager.get().registerColoredItemFrame(coloredFrame);

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
        com.kingodogo.buildscape.config.PillarIdManager.get().registerColoredItemFrame(coloredFrame);

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
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            String cached = CLIENT_PATTERN_CACHE.get(frame.getId());
            if (cached != null) {
                return cached;
            }
            // Fallback: Check synchronized data for our custom frame
            String pattern = frame.getParticlePattern();
            if (pattern != null && !pattern.isEmpty() && !"none".equals(pattern)) {
                CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);
                return pattern;
            }

            // Fallback to PillarIdManager data synced from server
            String frameId = getFrameIdColored(frame);
            if (frameId != null && !frameId.startsWith("F-????")) {
                com.kingodogo.buildscape.config.PillarIdManager.PillarData managerData =
                        com.kingodogo.buildscape.config.PillarIdManager.get().getPillarData(frameId);
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
        // Also check PATTERN tag from NBT (for /give commands)
        if (data.contains("PATTERN", 8)) {
            String p = data.getString("PATTERN");
            return (p == null || p.isEmpty()) ? "none" : p;
        }
        return "none";
    }

    public static void setParticlePatternColored(
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame,
            String pattern
    ) {
        CLIENT_PATTERN_CACHE.put(frame.getId(), pattern);

        frame.setParticlePattern(pattern);

        if (!frame.level.isClientSide) {
            CompoundTag data = frame.getPersistentData();
            data.putString(PARTICLE_PATTERN_KEY, pattern);
        }
    }

    public static java.util.List<String> getParticleColorsColored(
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            java.util.List<String> cached = CLIENT_COLOR_CACHE.get(frame.getId());
            if (cached != null) return cached;

            // 1. Check direct NBT on the entity (immediate response)
            CompoundTag data = frame.getPersistentData();
            if (data.contains(PARTICLE_COLORS_KEY)) {
                java.util.List<String> colors = new java.util.ArrayList<>();
                net.minecraft.nbt.ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
                for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                    colors.add(colorList.getString(i));
                }
                CLIENT_COLOR_CACHE.put(frame.getId(), colors);
                return colors;
            }

            // 2. Check synchronized data for our custom frame
            String colorsStr = frame.getParticleColorsRaw();
            if (colorsStr != null && !colorsStr.isEmpty()) {
                java.util.List<String> synchronizedColors = new java.util.ArrayList<>(java.util.Arrays.asList(colorsStr.split(";")));
                if (!synchronizedColors.isEmpty()) {
                    CLIENT_COLOR_CACHE.put(frame.getId(), synchronizedColors);
                    return synchronizedColors;
                }
            }

            // 3. Check synced MANAGER data (fallback for persistence)
            String frameId = getFrameIdColored(frame);

            return new java.util.ArrayList<>();
        }

        CompoundTag data = frame.getPersistentData();
        java.util.List<String> colors = new java.util.ArrayList<>();

        if (data.contains(PARTICLE_COLORS_KEY)) {
            net.minecraft.nbt.ListTag colorList = data.getList(PARTICLE_COLORS_KEY, 8);
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
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame,
            String color
    ) {
        java.util.List<String> colors = getParticleColorsColored(frame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase(java.util.Locale.ROOT));

        CLIENT_COLOR_CACHE.put(frame.getId(), colors);

        if (!frame.level.isClientSide) {
            CompoundTag data = frame.getPersistentData();
            net.minecraft.nbt.ListTag colorList = new net.minecraft.nbt.ListTag();
            for (String c : colors) {
                colorList.add(net.minecraft.nbt.StringTag.valueOf(c));
            }
            data.put(PARTICLE_COLORS_KEY, colorList);

            frame.setParticleColorsRaw(String.join(";", colors));

            if (!data.contains(FRAME_ID_KEY)) {
                String colorVariant = frame.getColorVariant();
                String frameId = generateColoredFrameId(colorVariant);
                data.putString(FRAME_ID_KEY, frameId);
                CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
            }
        }
    }

    public static String getFrameIdColored(
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame
    ) {
        if (frame.level.isClientSide) {
            String cached = CLIENT_FRAME_ID_CACHE.get(frame.getId());
            if (cached != null && !cached.equals("F-????")) return cached;

            // Fallback to position-based identification on client
            String dimension = com.kingodogo.buildscape.config.PillarIdManager.getDimensionKey(frame.level);
            net.minecraft.core.BlockPos pos = frame.blockPosition();
            net.minecraft.core.Direction dir = frame.getDirection();

            // 1. Exact match (pos + direction)
            String exactKey = com.kingodogo.buildscape.config.PillarIdManager.get().positionKey(dimension, pos, dir);
            String idFromPos = com.kingodogo.buildscape.config.PillarIdManager.get().getIdForPosition(exactKey);

            // 2. Fallback: If exact match fails, check if we're waiting for entity sync
            if (idFromPos == null) {
                String fuzzyKey = com.kingodogo.buildscape.config.PillarIdManager.get().positionKey(dimension, pos, null);
                String potentialId = com.kingodogo.buildscape.config.PillarIdManager.get().getIdForPosition(fuzzyKey);
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

        CompoundTag data = frame.getPersistentData();
        if (data.contains(FRAME_ID_KEY)) {
            String id = data.getString(FRAME_ID_KEY);
            if (id != null && !id.isEmpty()) return id;
        }

        // Generate ID with color code for colored frames
        String colorVariant = frame.getColorVariant();
        String frameId = generateColoredFrameId(colorVariant);
        data.putString(FRAME_ID_KEY, frameId);
        CLIENT_FRAME_ID_CACHE.put(frame.getId(), frameId);
        return frameId;
    }

    private static String getNextColorColored(
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame,
            PillarParticleConfig cfg
    ) {
        int entityId = frame.getId();

        java.util.List<String> customColors = getParticleColorsColored(frame);

        if (customColors != null && !customColors.isEmpty()) {
            int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
            int colorIndex = counter % customColors.size();
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
                Math.min(7, Math.min(cfg.max_particle_color, cfg.particle_color.size()))
        );
        int counter = COLOR_COUNTERS.getOrDefault(entityId, 0);
        int colorIndex = counter % maxColors;
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
            com.kingodogo.buildscape.entity.ColoredItemFrameEntity coloredFrame,
            String pattern,
            PillarParticleConfig cfg,
            long time
    ) {
        Random rand = new Random();

        net.minecraft.core.Direction facing = coloredFrame.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

        double centerX = coloredFrame.getX();
        double centerY = coloredFrame.getY();
        double centerZ = coloredFrame.getZ();

        double frameOffset = 0.0625;
        centerX += normal.x * frameOffset;
        centerY += normal.y * frameOffset;
        centerZ += normal.z * frameOffset;

        int baseCount = cfg.particle_density;
        int count = Math.max(
                1,
                cfg.use_pattern
                        ? (int) Math.round(baseCount * cfg.pattern_intensity)
                        : baseCount
        );

        double speed = cfg.use_pattern
                ? cfg.pattern_speed * cfg.pattern_intensity
                : cfg.particle_speed;
        double spread = cfg.use_pattern ? cfg.pattern_spread : cfg.particle_spread;

        Vec3 upVec, rightVec;
        if (facing == net.minecraft.core.Direction.UP) {
            upVec = new Vec3(0, 0, -1);
            rightVec = new Vec3(1, 0, 0);
        } else if (facing == net.minecraft.core.Direction.DOWN) {
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
                    // Snowflake pattern: Reverse of beam pattern
                    u = (rand.nextDouble() - 0.5) * spread * 0.3;
                    v = (rand.nextDouble() - 0.5) * spread * 0.3;
                    w = 2.0; // Fixed 2 blocks far
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

            // Determine particle type and color queuing based on pattern
            boolean isSnowflake = "snowflake".equals(pattern);
            SimpleParticleType particleType = isSnowflake
                    ? ModParticles.SNOWFLAKE_STILL.get() // Use no-gravity version
                    : ModParticles.GLOW_LIME_SPARKLE.get();

            // Only queue color for non-snowflake particles
            if (!isSnowflake) {
                String colorCode = getNextColorColored(coloredFrame, cfg);
                com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
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

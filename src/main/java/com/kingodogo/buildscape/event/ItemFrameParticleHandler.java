package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.particle.ModParticles;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

    private static final Map<Integer, String> CLIENT_PATTERN_CACHE =
            new ConcurrentHashMap<>();

    private static final Map<Integer, java.util.List<String>> CLIENT_COLOR_CACHE =
            new ConcurrentHashMap<>();

    private static final Map<Integer, String> CLIENT_FRAME_ID_CACHE =
            new ConcurrentHashMap<>();

    private static final String PATTERN_NOT_SET = "__not_set__";

    public static final int MAX_DYE_COLORS = 5;

    private static final String[] PATTERNS = {
            "none",
            "default",
            "beam",
            "spiral",
            "fountain",
            "pulse",
            "ring",
            "burst",
    };

    private static final String FRAME_PREFIX = "F";
    private static int frameIdCounter = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(
            PlayerInteractEvent.EntityInteract event
    ) {
        if (!(event.getTarget() instanceof ItemFrame)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) event.getTarget();

        Player player = event.getPlayer();
        Level level = event.getWorld();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!heldItem.isEmpty()) {
            java.util.Map.Entry<String, String> dyeInfo = getDyeColorAndName(
                    heldItem
            );
            if (dyeInfo != null) {
                if (itemFrame.getItem().isEmpty()) {
                    if (!level.isClientSide) {
                        ItemStack dyeToPlace = heldItem.copy();
                        dyeToPlace.setCount(1);
                        itemFrame.setItem(dyeToPlace);

                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }

                        level.playSound(
                                null,
                                itemFrame.getX(),
                                itemFrame.getY(),
                                itemFrame.getZ(),
                                SoundEvents.ITEM_FRAME_ADD_ITEM,
                                SoundSource.BLOCKS,
                                1.0f,
                                1.0f
                        );
                    }
                    event.setCanceled(true);
                    return;
                }

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

        if (!player.getItemInHand(event.getHand()).isEmpty()) {
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        String currentPattern = getParticlePattern(itemFrame);
        String nextPattern = cyclePattern(currentPattern);

        setParticlePattern(itemFrame, nextPattern);

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
    public static void onItemFrameInteract(
            PlayerInteractEvent.EntityInteract event
    ) {
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
            if (!(entity instanceof ItemFrame itemFrame)) {
                continue;
            }

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

            String colorCode = getNextColor(itemFrame, cfg);
            com.kingodogo.buildscape.particle.PillarSparkleParticle.queueColor(
                    colorCode
            );

            level.addParticle(
                    (SimpleParticleType) ModParticles.GLOW_LIME_SPARKLE.get(),
                    centerX + sx,
                    centerY + sy,
                    centerZ + sz,
                    vx,
                    vy,
                    vz
            );
        }
    }

    private static final Map<Integer, Integer> COLOR_COUNTERS =
            new ConcurrentHashMap<>();

    private static String getNextColor(
            ItemFrame itemFrame,
            PillarParticleConfig cfg
    ) {
        int entityId = itemFrame.getId();

        java.util.List<String> customColors = CLIENT_COLOR_CACHE.get(entityId);
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
            if (cached != null) {
                return cached;
            }
            return PATTERN_NOT_SET;
        }

        CompoundTag data = itemFrame.getPersistentData();
        if (data.contains(PARTICLE_PATTERN_KEY)) {
            return data.getString(PARTICLE_PATTERN_KEY);
        }
        return PATTERN_NOT_SET;
    }

    public static void setParticlePattern(ItemFrame itemFrame, String pattern) {
        CLIENT_PATTERN_CACHE.put(itemFrame.getId(), pattern);

        if (!itemFrame.level.isClientSide) {
            CompoundTag data = itemFrame.getPersistentData();
            data.putString(PARTICLE_PATTERN_KEY, pattern);
        }
    }

    public static void clearPatternCache(int entityId) {
        CLIENT_PATTERN_CACHE.remove(entityId);
    }

    private static String cyclePattern(String current) {
        if (current.equals(PATTERN_NOT_SET)) {
            return "default";
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
            return new java.util.AbstractMap.SimpleEntry<>("#FFFFFF", "White");
        } else if (item == net.minecraft.world.item.Items.ORANGE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FF9800", "Orange");
        } else if (item == net.minecraft.world.item.Items.MAGENTA_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#C74EBD", "Magenta");
        } else if (item == net.minecraft.world.item.Items.LIGHT_BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3AB3DA", "Light Blue");
        } else if (item == net.minecraft.world.item.Items.YELLOW_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#FED83D", "Yellow");
        } else if (item == net.minecraft.world.item.Items.LIME_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#80C71F", "Lime");
        } else if (item == net.minecraft.world.item.Items.PINK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#F38BAA", "Pink");
        } else if (item == net.minecraft.world.item.Items.GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#474F52", "Gray");
        } else if (item == net.minecraft.world.item.Items.LIGHT_GRAY_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#9D9D97", "Light Gray");
        } else if (item == net.minecraft.world.item.Items.CYAN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#169C9C", "Cyan");
        } else if (item == net.minecraft.world.item.Items.PURPLE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#8932B8", "Purple");
        } else if (item == net.minecraft.world.item.Items.BLUE_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#3C44AA", "Blue");
        } else if (item == net.minecraft.world.item.Items.BROWN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#835432", "Brown");
        } else if (item == net.minecraft.world.item.Items.GREEN_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#5E7C16", "Green");
        } else if (item == net.minecraft.world.item.Items.RED_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#B02E26", "Red");
        } else if (item == net.minecraft.world.item.Items.BLACK_DYE) {
            return new java.util.AbstractMap.SimpleEntry<>("#1D1D21", "Black");
        }

        return null;
    }

    public static java.util.List<String> getParticleColors(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            java.util.List<String> cached = CLIENT_COLOR_CACHE.get(itemFrame.getId());
            return cached != null ? cached : new java.util.ArrayList<>();
        }

        CompoundTag data = itemFrame.getPersistentData();
        java.util.List<String> colors = new java.util.ArrayList<>();

        if (data.contains(PARTICLE_COLORS_KEY)) {
            net.minecraft.nbt.ListTag colorList = data.getList(
                    PARTICLE_COLORS_KEY,
                    8
            );
            for (int i = 0; i < colorList.size() && i < MAX_DYE_COLORS; i++) {
                String color = colorList.getString(i);
                if (color != null && !color.isEmpty()) {
                    colors.add(color);
                }
            }
        }

        return colors;
    }

    public static void addParticleColor(ItemFrame itemFrame, String color) {
        java.util.List<String> colors = getParticleColors(itemFrame);
        if (colors.size() >= MAX_DYE_COLORS) return;

        colors.add(color.toUpperCase());

        CLIENT_COLOR_CACHE.put(itemFrame.getId(), colors);

        if (!itemFrame.level.isClientSide) {
            CompoundTag data = itemFrame.getPersistentData();
            net.minecraft.nbt.ListTag colorList = new net.minecraft.nbt.ListTag();
            for (String c : colors) {
                colorList.add(net.minecraft.nbt.StringTag.valueOf(c));
            }
            data.put(PARTICLE_COLORS_KEY, colorList);

            if (!data.contains(FRAME_ID_KEY)) {
                String frameId = generateFrameId();
                data.putString(FRAME_ID_KEY, frameId);
                CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
            }
        }
    }

    public static boolean hasCustomColors(ItemFrame itemFrame) {
        return !getParticleColors(itemFrame).isEmpty();
    }

    public static String getFrameId(ItemFrame itemFrame) {
        if (itemFrame.level.isClientSide) {
            String cached = CLIENT_FRAME_ID_CACHE.get(itemFrame.getId());
            return cached != null ? cached : "F-????";
        }

        CompoundTag data = itemFrame.getPersistentData();
        if (data.contains(FRAME_ID_KEY)) {
            return data.getString(FRAME_ID_KEY);
        }

        String frameId = generateFrameId();
        data.putString(FRAME_ID_KEY, frameId);
        CLIENT_FRAME_ID_CACHE.put(itemFrame.getId(), frameId);
        return frameId;
    }

    private static String generateFrameId() {
        frameIdCounter++;
        return (
                FRAME_PREFIX +
                        "-P" +
                        String.format("%04d", (frameIdCounter + new Random().nextInt(9000)))
        );
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
}

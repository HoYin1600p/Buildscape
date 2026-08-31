package com.kingodogo.buildscape.trophy;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class TrophyDefinition {
    public static final VoxelShape DEFAULT_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 18.0D, 14.0D);

    private final String id;
    private final TrophyTier tier;
    private final int lightEmission;
    private final boolean foil;
    private final boolean isFallback;
    private final SoundType soundType;
    private final float hardness;
    private final float resistance;
    private final Map<Direction, VoxelShape> directionalShapes;
    private final String associatedAdvancement;
    private final String customDisplayName;
    private final String customDescription;

    private RegistryObject<TrophyBlock> blockObject;
    private RegistryObject<Item> itemObject;
    private Supplier<Item> fallbackItemSupplier;

    private TrophyDefinition(Builder builder) {
        this.id = builder.id;
        this.tier = builder.tier;
        this.lightEmission = builder.lightEmission;
        this.foil = builder.foil;
        this.isFallback = builder.isFallback;
        this.soundType = builder.soundType != null ? builder.soundType : builder.tier.getDefaultSound();
        this.hardness = builder.hardness;
        this.resistance = builder.resistance;
        this.directionalShapes = builder.directionalShapes != null ? builder.directionalShapes : createDirectionalShapes(new double[]{2.0D, 0.0D, 2.0D, 14.0D, 18.0D, 14.0D});
        this.associatedAdvancement = builder.associatedAdvancement;
        this.customDisplayName = builder.customDisplayName;
        this.customDescription = builder.customDescription;
        this.fallbackItemSupplier = builder.fallbackItemSupplier;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static Map<Direction, VoxelShape> createDirectionalShapes(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return createDirectionalShapes(new double[]{minX, minY, minZ, maxX, maxY, maxZ});
    }

    public static Map<Direction, VoxelShape> createDirectionalShapes(double[]... boxes) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        VoxelShape north = Shapes.empty();
        VoxelShape south = Shapes.empty();
        VoxelShape west = Shapes.empty();
        VoxelShape east = Shapes.empty();

        for (double[] box : boxes) {
            double minX = box[0];
            double minY = box[1];
            double minZ = box[2];
            double maxX = box[3];
            double maxY = box[4];
            double maxZ = box[5];

            north = Shapes.or(north, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
            south = Shapes.or(south, Block.box(16.0D - maxX, minY, 16.0D - maxZ, 16.0D - minX, maxY, 16.0D - minZ));
            west = Shapes.or(west, Block.box(minZ, minY, 16.0D - maxX, maxZ, maxY, 16.0D - minX));
            east = Shapes.or(east, Block.box(16.0D - maxZ, minY, minX, 16.0D - minZ, maxY, maxX));
        }

        map.put(Direction.NORTH, north);
        map.put(Direction.SOUTH, south);
        map.put(Direction.WEST, west);
        map.put(Direction.EAST, east);
        map.put(Direction.UP, north);
        map.put(Direction.DOWN, north);
        return map;
    }

    public String getId() {
        return id;
    }

    public TrophyTier getTier() {
        return tier;
    }

    public int getLightEmission() {
        return lightEmission;
    }

    public boolean isFoil() {
        return foil;
    }

    public boolean isFallback() {
        return isFallback;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public VoxelShape getShape(Direction facing) {
        return directionalShapes.getOrDefault(facing, DEFAULT_SHAPE);
    }

    public String getAssociatedAdvancement() {
        return associatedAdvancement;
    }

    public String getCustomDisplayName() {
        return customDisplayName;
    }

    public String getCustomDescription() {
        return customDescription;
    }

    public Rarity getRarity() {
        return tier.getDefaultRarity();
    }

    public void setBlockRegistryObject(RegistryObject<TrophyBlock> blockObject) {
        this.blockObject = blockObject;
    }

    public void setItemRegistryObject(RegistryObject<Item> itemObject) {
        this.itemObject = itemObject;
    }

    public TrophyBlock getBlock() {
        return blockObject != null ? blockObject.get() : null;
    }

    public Item getItem() {
        if (isFallback && fallbackItemSupplier != null) {
            return fallbackItemSupplier.get();
        }
        return itemObject != null ? itemObject.get() : null;
    }

    public static class Builder {
        private final String id;
        private TrophyTier tier = TrophyTier.FALLBACK;
        private int lightEmission = 0;
        private boolean foil = false;
        private boolean isFallback = false;
        private SoundType soundType;
        private float hardness = 1.5F;
        private float resistance = 6.0F;
        private Map<Direction, VoxelShape> directionalShapes;
        private String associatedAdvancement;
        private String customDisplayName;
        private String customDescription;
        private Supplier<Item> fallbackItemSupplier;

        public Builder(String id) {
            this.id = id;
        }

        public Builder tier(TrophyTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder lightEmission(int lightEmission) {
            this.lightEmission = lightEmission;
            return this;
        }

        public Builder foil(boolean foil) {
            this.foil = foil;
            return this;
        }

        public Builder fallback(boolean isFallback) {
            this.isFallback = isFallback;
            return this;
        }

        public Builder soundType(SoundType soundType) {
            this.soundType = soundType;
            return this;
        }

        public Builder hardness(float hardness) {
            this.hardness = hardness;
            return this;
        }

        public Builder resistance(float resistance) {
            this.resistance = resistance;
            return this;
        }

        public Builder shape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.directionalShapes = createDirectionalShapes(minX, minY, minZ, maxX, maxY, maxZ);
            return this;
        }

        public Builder shape(Map<Direction, VoxelShape> directionalShapes) {
            this.directionalShapes = directionalShapes;
            return this;
        }

        public Builder shape(double[]... boxes) {
            this.directionalShapes = createDirectionalShapes(boxes);
            return this;
        }

        public Builder advancement(String associatedAdvancement) {
            this.associatedAdvancement = associatedAdvancement;
            return this;
        }

        public Builder displayName(String displayName) {
            this.customDisplayName = displayName;
            return this;
        }

        public Builder description(String description) {
            this.customDescription = description;
            return this;
        }

        public Builder fallbackItemSupplier(Supplier<Item> fallbackItemSupplier) {
            this.fallbackItemSupplier = fallbackItemSupplier;
            return this;
        }

        public TrophyDefinition build() {
            return new TrophyDefinition(this);
        }
    }
}

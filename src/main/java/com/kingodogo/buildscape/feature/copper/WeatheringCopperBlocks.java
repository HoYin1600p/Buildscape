package com.kingodogo.buildscape.feature.copper;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.function.Supplier;

public final class WeatheringCopperBlocks {
    private WeatheringCopperBlocks() {
    }

    public static class FreshBulb extends FreshCopperBulbBlock implements BuildscapeWeatheringCopper {
        public FreshBulb(Properties properties) {
            super(properties);
        }
    }

    public static class ExposedBulb extends ExposedCopperBulbBlock implements BuildscapeWeatheringCopper {
        public ExposedBulb(Properties properties) {
            super(properties);
        }
    }

    public static class WeatheredBulb extends WeatheredCopperBulbBlock implements BuildscapeWeatheringCopper {
        public WeatheredBulb(Properties properties) {
            super(properties);
        }
    }

    public static class OxidizedBulb extends OxidizedCopperBulbBlock implements BuildscapeWeatheringCopper {
        public OxidizedBulb(Properties properties) {
            super(properties);
        }
    }

    public static class Chest extends CopperChestBlock implements BuildscapeWeatheringCopper {
        public Chest(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
            super(false, properties, blockEntityTypeSupplier);
        }
    }
}

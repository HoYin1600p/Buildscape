package com.kingodogo.buildscape.adapter.client;

/** Reference 1.18.2 tint handlers resolved against the shared content registry. */
public final class FoliageColors {
    private FoliageColors() {}
    private static net.minecraft.world.level.block.Block block(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", path));
    }
    private static net.minecraft.world.item.Item item(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", path));
    }
    public static void registerBlocks(net.minecraft.client.color.block.BlockColors blockColors) {
    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (reader == null || pos == null) {
                    return 0x7FA832;
                }
                if (tintIndex == 1) {
                    if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                        return levelReader
                                .getBiome(pos)
                                .value()
                                .getGrassColor(pos.getX(),
                                        pos.getZ());
                    }
                    return 0x7FA832;
                }
                return -1;
            },
            block("clover"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (reader == null || pos == null) {
                    return 0x7FA832;
                }
                if (tintIndex == 1) {
                    if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                        return levelReader
                                .getBiome(pos)
                                .value()
                                .getGrassColor(pos.getX(),
                                        pos.getZ());
                    }
                    return 0x7FA832;
                }
                return -1;
            },
            block("red_petal"),
            block("blue_petal"),
            block("orange_petal"),
            block("pink_petal"),
            block("purple_petal"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }

                if (state.hasProperty(
                        com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR)) {
                    com.kingodogo.buildscape.block.OrnamentBlock.StringColor stringColor = state
                            .getValue(
                                    com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR);
                    return stringColor.getColor();
                }

                return 0xFFFFFF;
            },
            block("white_ornament"),
            block("orange_ornament"),
            block("magenta_ornament"),
            block("light_blue_ornament"),
            block("yellow_ornament"),
            block("lime_ornament"),
            block("pink_ornament"),
            block("gray_ornament"),
            block("light_gray_ornament"),
            block("cyan_ornament"),
            block("purple_ornament"),
            block("blue_ornament"),
            block("brown_ornament"),
            block("green_ornament"),
            block("red_ornament"),
            block("black_ornament"),
            block("glass_ornament"),
            block("tinted_glass_ornament"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }

                if (state.hasProperty(
                        com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR)) {
                    com.kingodogo.buildscape.block.OrnamentBlock.StringColor stringColor = state
                            .getValue(
                                    com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR);
                    return stringColor.getColor();
                }

                return 0xFFFFFF;
            },
            block("white_string_light"),
            block("orange_string_light"),
            block("magenta_string_light"),
            block("light_blue_string_light"),
            block("yellow_string_light"),
            block("lime_string_light"),
            block("pink_string_light"),
            block("gray_string_light"),
            block("light_gray_string_light"),
            block("cyan_string_light"),
            block("purple_string_light"),
            block("blue_string_light"),
            block("brown_string_light"),
            block("green_string_light"),
            block("red_string_light"),
            block("black_string_light"),
            block("multicolor_string_light"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }

                if (reader != null
                        && pos != null
                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                    return levelReader.getBiome(pos).value()
                            .getFoliageColor();
                }

                return 0x48B518;
            },
            block("oak_leaf_layers"),
            block("jungle_leaf_layers"),
            block("acacia_leaf_layers"),
            block("dark_oak_leaf_layers"),
            block("oak_leaf_hedge"),
            block("jungle_leaf_hedge"),
            block("acacia_leaf_hedge"),
            block("dark_oak_leaf_hedge"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) return -1;
                if (reader != null && pos != null) {
                    return net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(reader, pos);
                }
                return 0x48B518;
            },
            block("bush"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) return -1;
                if (reader != null && pos != null) {
                    return DryFoliageColor.getDryFoliageColor(reader, pos);
                }
                return DryFoliageColor.getDefaultColor();
            },
            block("leaf_litter"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex == 1 || tintIndex == 0) {
                    if (reader != null && pos != null) {
                        return net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(reader, pos);
                    }
                    return 0x48B518;
                }
                return -1;
            },
            block("wildflowers"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                return 0x619961;
            },
            block("spruce_leaf_layers"),
            block("spruce_leaf_hedge"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                return 0x80a755;
            },
            block("birch_leaf_layers"),
            block("birch_leaf_hedge"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                return -1;
            },
            block("azalea_leaf_layers"),
            block("flowering_azalea_leaf_layers"),
            block("azalea_leaf_hedge"),
            block("flowering_azalea_leaf_hedge"),
            block("orange_poplar_leaf_layers"),
            block("red_poplar_leaf_layers"),
            block("yellow_poplar_leaf_layers"),
            block("cherry_leaf_layers"),
            block("pale_oak_leaf_layers"),
            block("snowy_cherry_leaf_layers"),
            block("snowy_pale_oak_leaf_layers"),
            block("orange_poplar_leaf_hedge"),
            block("red_poplar_leaf_hedge"),
            block("yellow_poplar_leaf_hedge"),
            block("cherry_leaf_hedge"),
            block("pale_oak_leaf_hedge"),
            block("snowy_cherry_leaf_hedge"),
            block("snowy_pale_oak_leaf_hedge"),
            block("snowy_cherry_leaves"),
            block("snowy_pale_oak_leaves"),
            block("snowy_orange_poplar_leaf_layers"),
            block("snowy_red_poplar_leaf_layers"),
            block("snowy_yellow_poplar_leaf_layers"),
            block("snowy_orange_poplar_leaf_hedge"),
            block("snowy_red_poplar_leaf_hedge"),
            block("snowy_yellow_poplar_leaf_hedge"),
            block("snowy_orange_poplar_leaves"),
            block("snowy_red_poplar_leaves"),
            block("snowy_yellow_poplar_leaves"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                return -1;
            },
            block("snowy_oak_leaf_layers"),
            block("snowy_spruce_leaf_layers"),
            block("snowy_birch_leaf_layers"),
            block("snowy_jungle_leaf_layers"),
            block("snowy_acacia_leaf_layers"),
            block("snowy_dark_oak_leaf_layers"),
            block("snowy_mangrove_leaf_layers"),
            block("snowy_azalea_leaf_layers"),
            block("snowy_flowering_azalea_leaf_layers"),
            block("snowy_oak_leaf_hedge"),
            block("snowy_spruce_leaf_hedge"),
            block("snowy_birch_leaf_hedge"),
            block("snowy_jungle_leaf_hedge"),
            block("snowy_acacia_leaf_hedge"),
            block("snowy_dark_oak_leaf_hedge"),
            block("snowy_mangrove_leaf_hedge"),
            block("snowy_azalea_leaf_hedge"),
            block("snowy_flowering_azalea_leaf_hedge"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                if (reader != null
                        && pos != null
                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                    return levelReader.getBiome(pos).value()
                            .getFoliageColor();
                }
                return 0x92c648;
            },
            block("mangrove_leaf_layers"),
            block("mangrove_leaf_hedge"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                if (reader != null
                        && pos != null
                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                    return levelReader.getBiome(pos).value()
                            .getFoliageColor();
                }
                return 0x92c648;
            },
            block("mangrove_leaves"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                if (reader != null && pos != null) {
                    return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(reader, pos);
                }
                return 0x3F76E4;
            },
            block("cascade_block")
    );

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                if (reader != null && pos != null) {
                    return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(reader, pos);
                }
                return 0x3F76E4;
            },
            block("cascade_block_no_mist")
    );

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }

                if (reader != null && pos != null) {
                    net.minecraft.world.level.block.entity.BlockEntity be = reader
                            .getBlockEntity(pos);
                    if (be instanceof com.kingodogo.buildscape.block.GlowLightsBlockEntity glowBE) {
                        return com.kingodogo.buildscape.client.GlowLightTint.color(
                                pos.hashCode() ^ state.hashCode(), glowBE.getDyeColors());
                    }
                }

                return -1;
            },
            block("glow_lights"));

    blockColors.register(
            (state, reader, pos, tintIndex) -> {
                if (tintIndex == 0) {
                    if (reader != null && pos != null) {
                        return net.minecraft.client.renderer.BiomeColors.getAverageGrassColor(reader, pos);
                    }
                    return net.minecraft.world.level.GrassColor.get(0.5D, 1.0D);
                }
                return -1;
            },
            net.minecraft.world.level.block.Blocks.COMPOSTER);

    }

    public static void registerItems(Object ignored) {
    }
    /* ItemColors removed in 1.21.4; item tints are handled by client item models. */
}

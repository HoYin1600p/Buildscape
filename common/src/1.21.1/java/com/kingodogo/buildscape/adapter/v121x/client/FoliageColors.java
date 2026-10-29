package com.kingodogo.buildscape.adapter.v121x.client;

/** Reference 1.18.2 tint handlers resolved against the shared content registry. */
public final class FoliageColors {
    private FoliageColors() {}
    private static net.minecraft.world.level.block.Block block(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", path));
    }
    private static net.minecraft.world.item.Item item(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", path));
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
                return net.minecraft.world.level.FoliageColor.getDefaultColor();
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
                    return net.minecraft.world.level.FoliageColor.getDefaultColor();
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
                        java.util.List<String> dyeColors = glowBE
                                .getDyeColors();
                        if (dyeColors != null && !dyeColors.isEmpty()) {
                            int hash = pos.hashCode()
                                    ^ state.hashCode();
                            int colorIndex = Math.floorMod(hash, dyeColors.size());
                            String colorCode = dyeColors
                                    .get(colorIndex);

                            try {
                                if (colorCode != null
                                        && colorCode.startsWith(
                                        "#")
                                        && colorCode.length() == 7) {
                                    int color = Integer
                                            .parseInt(colorCode
                                                            .substring(1),
                                                    16);
                                    return color | 0xFF000000;
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
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

    public static void registerItems(net.minecraft.client.color.item.ItemColors itemColors) {
    net.minecraft.client.color.item.ItemColors vanillaItemColors = itemColors;

    itemColors.register(
            (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
            item("cascade_block")
    );

    itemColors.register(
            (stack, tintIndex) -> {
                if (tintIndex != 1) return -1;
                var explosion = stack.get(net.minecraft.core.component.DataComponents.FIREWORK_EXPLOSION);
                int[] colors = explosion != null ? explosion.colors().toIntArray() : null;
                if (colors != null && colors.length > 0) {
                    if (colors.length == 1) return colors[0];
                    int r = 0, g = 0, b = 0;
                    for (int c : colors) {
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += c & 0xFF;
                    }
                    return ((r / colors.length) << 16) | ((g / colors.length) << 8) | (b / colors.length);
                }
                return 0xFFB000;
            },
            item("infinite_phoenix_firework_star")
    );

    itemColors.register(
            (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
            item("cascade_block_no_mist")
    );

    itemColors.register(
            (stack, tintIndex) -> {
                if (tintIndex != 0) {
                    return -1;
                }
                net.minecraft.world.item.Item item = stack.getItem();
                if (item instanceof net.minecraft.world.item.BlockItem) {
                    net.minecraft.world.level.block.Block block = ((net.minecraft.world.item.BlockItem) item)
                            .getBlock();

                    int color = -1;
                    if (block == block("leaf_litter")) {
                        color = DryFoliageColor.getDefaultColor();
                    } else if (block == block("bush")
                            || block == block("oak_leaf_hedge")
                            || block == block("oak_leaf_layers")) {
                        color = vanillaItemColors.getColor(
                                new net.minecraft.world.item.ItemStack(
                                        net.minecraft.world.item.Items.OAK_LEAVES),
                                0);
                    } else if (block == block("jungle_leaf_hedge")
                            || block == block("jungle_leaf_layers")) {
                        color = vanillaItemColors.getColor(
                                new net.minecraft.world.item.ItemStack(
                                        net.minecraft.world.item.Items.JUNGLE_LEAVES),
                                0);
                    } else if (block == block("acacia_leaf_hedge")
                            || block == block("acacia_leaf_layers")) {
                        color = vanillaItemColors.getColor(
                                new net.minecraft.world.item.ItemStack(
                                        net.minecraft.world.item.Items.ACACIA_LEAVES),
                                0);
                    } else if (block == block("dark_oak_leaf_hedge")
                            || block == block("dark_oak_leaf_layers")) {
                        color = vanillaItemColors.getColor(
                                new net.minecraft.world.item.ItemStack(
                                        net.minecraft.world.item.Items.DARK_OAK_LEAVES),
                                0);
                    } else if (block == block("spruce_leaf_hedge")
                            || block == block("spruce_leaf_layers")) {
                        color = 0x619961;
                    } else if (block == block("birch_leaf_hedge")
                            || block == block("birch_leaf_layers")) {
                        color = 0x80a755;
                    } else if (block == block("mangrove_leaves")
                            || block == block("mangrove_leaf_layers")
                            ||
                            block == block("mangrove_leaf_hedge")) {
                        color = 0x92c648;
                    }

                    return color;
                }
                return -1;
            },
            item("oak_leaf_layers"),
            item("spruce_leaf_layers"),
            item("birch_leaf_layers"),
            item("jungle_leaf_layers"),
            item("acacia_leaf_layers"),
            item("dark_oak_leaf_layers"),
            item("azalea_leaf_layers"),
            item("flowering_azalea_leaf_layers"),
            item("snowy_oak_leaf_layers"),
            item("snowy_spruce_leaf_layers"),
            item("snowy_birch_leaf_layers"),
            item("snowy_jungle_leaf_layers"),
            item("snowy_acacia_leaf_layers"),
            item("snowy_dark_oak_leaf_layers"),
            item("snowy_mangrove_leaf_layers"),
            item("snowy_azalea_leaf_layers"),
            item("snowy_flowering_azalea_leaf_layers"),
            item("mangrove_leaf_layers"),
            item("oak_leaf_hedge"),
            item("spruce_leaf_hedge"),
            item("birch_leaf_hedge"),
            item("jungle_leaf_hedge"),
            item("acacia_leaf_hedge"),
            item("dark_oak_leaf_hedge"),
            item("azalea_leaf_hedge"),
            item("flowering_azalea_leaf_hedge"),
            item("bush"),
            item("snowy_oak_leaf_hedge"),
            item("snowy_spruce_leaf_hedge"),
            item("snowy_birch_leaf_hedge"),
            item("snowy_jungle_leaf_hedge"),
            item("snowy_acacia_leaf_hedge"),
            item("snowy_dark_oak_leaf_hedge"),
            item("snowy_mangrove_leaf_hedge"),
            item("snowy_azalea_leaf_hedge"),
            item("snowy_flowering_azalea_leaf_hedge"),
            item("mangrove_leaf_hedge"),
            item("mangrove_leaves"));

    itemColors.register(
            (stack, tintIndex) -> {
                if (tintIndex != 1) {
                    return -1;
                }
                if (stack.getItem() instanceof com.kingodogo.buildscape.item.BiomeBrushItem brush) {
                    String biomeStr = brush.getCapturedBiome(stack);
                    if (biomeStr != null && !biomeStr.isEmpty()) {
                        net.minecraft.resources.ResourceLocation biomeKey = net.minecraft.resources.ResourceLocation.tryParse(biomeStr);
                        if (biomeKey != null) {
                            net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
                            if (level != null) {
                                java.util.Optional<? extends net.minecraft.core.Registry<net.minecraft.world.level.biome.Biome>> registryOpt = level.registryAccess().registry(net.minecraft.core.registries.Registries.BIOME);
                                if (registryOpt.isPresent()) {
                                    net.minecraft.world.level.biome.Biome biome = registryOpt.get().get(biomeKey);
                                    if (biome != null) {
                                        return biome.getFoliageColor();
                                    }
                                }
                            }
                        }
                    }
                }
                return -1;
            },
            item("copper_biome_brush"),
            item("diamond_biome_brush"),
            item("netherite_biome_brush")
    );
    }
}


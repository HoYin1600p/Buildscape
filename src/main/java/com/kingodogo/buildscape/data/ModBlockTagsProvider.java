package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public static final TagKey<Block> VERTICAL_SLABS = BlockTags.create(new ResourceLocation(BuildScape.MODID, "vertical_slabs"));
    public static final TagKey<Block> VERTICAL_STAIRS = BlockTags.create(new ResourceLocation(BuildScape.MODID, "vertical_stairs"));

    public ModBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, BuildScape.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Vertical slab/stair tags are maintained as static tag JSON.
    }
}

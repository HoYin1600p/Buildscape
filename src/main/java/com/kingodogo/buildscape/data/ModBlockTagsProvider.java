package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
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

    public ModBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, BuildScape.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Tag all vertical slabs
        ModVerticalSlabs.VERTICAL_SLABS.values().forEach(block -> {
            tag(VERTICAL_SLABS).add(block);
            tag(BlockTags.SLABS).add(block);

            if (block instanceof VerticalSlabBlock vsb) {
                Block parent = vsb.getParentBlock();
                if (parent != null) {
                    if (parent.defaultBlockState().getMaterial() == net.minecraft.world.level.material.Material.WOOD) {
                        tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                        tag(BlockTags.WOODEN_SLABS).add(block);
                    } else {
                        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                    }
                }
            }
        });
    }
}

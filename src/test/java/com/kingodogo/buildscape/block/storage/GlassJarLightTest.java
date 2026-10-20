package com.kingodogo.buildscape.block.storage;

import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.registry.items.CardboardItems;
import com.kingodogo.buildscape.registry.items.ItemsRegister;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlassJarLightTest extends BootstrappedGameTest {
    @BeforeClass
    public static void bindExperienceBucket() {
        RegistryTestSupport.bind(ItemsRegister.items(), path -> path.equals("experience_bucket"));
    }

    @AfterClass
    public static void unbindExperienceBucket() {
        RegistryTestSupport.unbind(ItemsRegister.items(), path -> path.equals("experience_bucket"));
    }

    @Test
    public void experienceAndLavaGlowAtPartialAndFullLevels() {
        for (ItemStack liquid : new ItemStack[]{new ItemStack(Items.EXPERIENCE_BOTTLE),
                new ItemStack(CardboardItems.EXPERIENCE_BUCKET.get()), new ItemStack(Items.LAVA_BUCKET)}) {
            assertTrue(GlassJarBlockEntity.shouldGlow(liquid, 1));
            assertTrue(GlassJarBlockEntity.shouldGlow(liquid, GlassJarBlockEntity.getLiquidCap(liquid)));
            assertFalse(GlassJarBlockEntity.shouldGlow(liquid, 0));
        }
    }

    @Test
    public void waterEmptyAndStoredItemsDoNotGlow() {
        for (ItemStack contents : new ItemStack[]{ItemStack.EMPTY, new ItemStack(Items.WATER_BUCKET),
                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER),
                new ItemStack(Items.APPLE), new ItemStack(Items.GLOW_BERRIES), new ItemStack(Items.GLOWSTONE_DUST)}) {
            assertFalse(GlassJarBlockEntity.shouldGlow(contents, 0));
            assertFalse(GlassJarBlockEntity.shouldGlow(contents, 1));
            assertFalse(GlassJarBlockEntity.shouldGlow(contents, GlassJarBlockEntity.MAX_LIQUID_LEVEL));
        }
    }

    @Test
    public void everyJarDefaultsToDarkAndGlowingEmitsFifteenEvenWhenWaterlogged() {
        int jarCount = 0;
        for (var entry : BlocksRegister.blocks().getEntries()) {
            String path = entry.getId().getPath();
            if (!path.equals("glass_jar") && !path.endsWith("_glass_jar") && !path.equals("golden_jar")) {
                continue;
            }
            Block block = RegistryTestSupport.create(BlocksRegister.blocks(), path);
            assertTrue(path, block instanceof GlassJarBlock);
            BlockState state = block.defaultBlockState();
            assertEquals(GlassJarBlock.GLOWING, block.getStateDefinition().getProperty("glowing"));
            assertFalse(path, state.getValue(GlassJarBlock.GLOWING));
            assertFalse(path, state.getValue(GlassJarBlock.WATERLOGGED));
            for (boolean waterlogged : new boolean[]{false, true}) {
                BlockState dark = state.setValue(GlassJarBlock.WATERLOGGED, waterlogged);
                BlockState glowing = dark.setValue(GlassJarBlock.GLOWING, true);
                assertEquals(path, 0, dark.getLightEmission());
                assertEquals(path, 15, glowing.getLightEmission());
                assertEquals(waterlogged, glowing.getValue(GlassJarBlock.WATERLOGGED));
            }
            jarCount++;
        }
        assertEquals(19, jarCount);
    }
}

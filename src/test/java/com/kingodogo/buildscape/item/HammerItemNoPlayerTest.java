package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;

public class HammerItemNoPlayerTest extends BootstrappedGameTest {
    @Test
    public void useOnWithoutPlayerReturnsPass() throws ReflectiveOperationException {
        // This path uses no item state; skip the constructor to avoid the frozen item registry.
        Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        HammerItem hammer = (HammerItem) unsafe.getClass().getMethod("allocateInstance", Class.class)
                .invoke(unsafe, HammerItem.class);
        BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.UP, BlockPos.ZERO, false);
        UseOnContext context = new UseOnContext(null, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, hit) {
        };

        assertSame(InteractionResult.PASS, hammer.useOn(context));
    }
}

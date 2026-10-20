package com.kingodogo.buildscape.feature.copper;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "buildscape")
public final class CopperToolActions {
    private CopperToolActions() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        ToolAction action = event.getToolAction();
        if (action != ToolActions.AXE_SCRAPE && action != ToolActions.AXE_WAX_OFF) {
            return;
        }
        UseOnContext context = event.getContext();
        if (context == null || !context.getItemInHand().canPerformAction(action)
                || event.getFinalState() != event.getState()) {
            return;
        }
        BlockState result = CopperOxidationHandler.getToolModifiedState(event.getState(), action);
        if (result == null) {
            return;
        }
        event.setFinalState(result);
        if (!event.isSimulated() && !context.getLevel().isClientSide) {
            CopperOxidationHandler.setBlockStateOrDoor(context.getLevel(), context.getClickedPos(),
                    event.getState(), result.getBlock());
        }
    }
}

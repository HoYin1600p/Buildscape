package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.FestiveGlintAnvilHandler;
import com.kingodogo.buildscape.mixin.ItemCombinerMenuAccessor;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilUpdateMixin {
    @Shadow @Final private DataSlot cost;
    @Shadow private String itemName;
    @Shadow private int repairItemCountCost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void buildscape$update(CallbackInfo ci) {
        var input = ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots();
        var result = FestiveGlintAnvilHandler.processAnvil(input.getItem(0), input.getItem(1), itemName);
        if (result != null) {
            var menu = (AnvilMenu) (Object) this;
            menu.getSlot(menu.getResultSlot()).set(result.output);
            cost.set(result.cost);
            repairItemCountCost = result.materialCost;
            menu.broadcastChanges();
            ci.cancel();
        }
    }
}

package com.kingodogo.buildscape.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Keeps Buildscape's vanilla vertical slabs beside their horizontal variants.
 *
 * @author hoyin1600p
 */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Inject(method = "fillItemList", at = @At("TAIL"))
    private void buildscape$arrangeVerticalSlabs(NonNullList<ItemStack> items, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        if (tab == CreativeModeTab.TAB_BUILDING_BLOCKS || tab == CreativeModeTab.TAB_DECORATIONS) {
            Map<ResourceLocation, ItemStack> stacksById = new HashMap<>();
            for (ItemStack stack : items) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id != null) {
                    stacksById.put(id, stack);
                }
            }

            Map<ItemStack, ItemStack> insertAfter = new IdentityHashMap<>();
            Set<ItemStack> moved = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Map.Entry<ResourceLocation, ItemStack> entry : stacksById.entrySet()) {
                ResourceLocation id = entry.getKey();
                String path = id.getPath();
                if (!"buildscape".equals(id.getNamespace()) || !path.endsWith("_vertical_slab")) {
                    continue;
                }

                String base = path.substring(0, path.length() - "_vertical_slab".length());
                ItemStack anchor = stacksById.get(new ResourceLocation("minecraft", base + "_slab"));
                if (anchor == null) {
                    anchor = stacksById.get(new ResourceLocation("minecraft", base));
                }
                if (anchor != null) {
                    insertAfter.put(anchor, entry.getValue());
                    moved.add(entry.getValue());
                }
            }

            NonNullList<ItemStack> ordered = NonNullList.create();
            for (ItemStack stack : items) {
                if (!moved.contains(stack)) {
                    ordered.add(stack);
                    ItemStack verticalSlab = insertAfter.get(stack);
                    if (verticalSlab != null) {
                        ordered.add(verticalSlab);
                    }
                }
            }

            items.clear();
            items.addAll(ordered);
        }
    }
}

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

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Inject(method = "fillItemList", at = @At("TAIL"))
    private void buildscape$arrangeVerticalSlabs(NonNullList<ItemStack> items, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        if (tab == CreativeModeTab.TAB_BUILDING_BLOCKS || tab == CreativeModeTab.TAB_DECORATIONS) {
            List<ItemStack> ordered = new ArrayList<>(items);
            List<ItemStack> toMove = new ArrayList<>();
            
            // Find all Buildscape vertical slabs in this tab
            for (ItemStack stack : ordered) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id != null && "buildscape".equals(id.getNamespace())) {
                    String path = id.getPath();
                    if (path.endsWith("_vertical_slab")) {
                        String base = path.substring(0, path.length() - "_vertical_slab".length());
                        ResourceLocation vanillaBlockId = new ResourceLocation("minecraft", base);
                        ResourceLocation vanillaSlabId = new ResourceLocation("minecraft", base + "_slab");
                        boolean isVanilla = ForgeRegistries.ITEMS.containsKey(vanillaBlockId)
                                || ForgeRegistries.ITEMS.containsKey(vanillaSlabId);
                        if (isVanilla) {
                            toMove.add(stack);
                        }
                    }
                }
            }
            
            // Move each to its proper place
            for (ItemStack stack : toMove) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id == null) continue;
                String path = id.getPath();
                String base = path.substring(0, path.length() - "_vertical_slab".length());
                ResourceLocation anchorId1 = new ResourceLocation("minecraft", base + "_slab");
                ResourceLocation anchorId2 = new ResourceLocation("minecraft", base);
                
                ordered.remove(stack);
                int targetIndex = -1;
                
                // First look for slab anchor
                for (int i = 0; i < ordered.size(); i++) {
                    ResourceLocation currentId = ForgeRegistries.ITEMS.getKey(ordered.get(i).getItem());
                    if (currentId != null && anchorId1.equals(currentId)) {
                        targetIndex = i;
                        break;
                    }
                }
                
                // If not found, look for base block anchor
                if (targetIndex < 0) {
                    for (int i = 0; i < ordered.size(); i++) {
                        ResourceLocation currentId = ForgeRegistries.ITEMS.getKey(ordered.get(i).getItem());
                        if (currentId != null && anchorId2.equals(currentId)) {
                            targetIndex = i;
                            break;
                        }
                    }
                }
                
                if (targetIndex >= 0) {
                    ordered.add(targetIndex + 1, stack);
                } else {
                    ordered.add(stack);
                }
            }
            
            items.clear();
            items.addAll(ordered);
        }
    }
}

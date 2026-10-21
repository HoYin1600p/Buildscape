package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.resources.bundle.adapter.TagAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {

    @Shadow
    @Final
    private String directory;

    @Inject(method = "load", at = @At("RETURN"))
    private void buildscape$applyBundledTags(
            ResourceManager resourceManager,
            CallbackInfoReturnable<Map<ResourceLocation, Tag.Builder>> cir) {
        Map<ResourceLocation, Tag.Builder> map = cir.getReturnValue();
        if (map != null) {
            TagAdapter.applyTags(this.directory, map, resourceManager);
        }
    }
}

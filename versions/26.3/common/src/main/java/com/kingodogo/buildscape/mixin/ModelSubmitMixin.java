package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SubmitNodeCollection.class)
public abstract class ModelSubmitMixin {
    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private RenderType buildscape$festiveModelGlint(RenderType type) {
        return FestiveSubmission.currentGlint(type);
    }
}

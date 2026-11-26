package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.platform.FabricPlatformAdapter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Custom Fabric entities have no vanilla data-fixer schema, as in FabricEntityTypeBuilder. */
@Mixin(EntityType.Builder.class)
public abstract class EntityTypeBuilderMixin {
    @org.spongepowered.asm.mixin.Unique
    private static final java.util.Set<String> BUILDSCAPE_ENTITIES = java.util.Set.of(
            "falling_icicle", "festive_stocking", "mangrove_boat", "colored_item_frame",
            "seat", "poplar_boat", "wandering_homemaker", "festive_wandering_homemaker");

    @Redirect(method = "build", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/Util;fetchChoiceType(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;"))
    private Type<?> buildscape$customEntitySchema(DSL.TypeReference reference, String id) {
        String path = id.startsWith("buildscape:") ? id.substring("buildscape:".length()) : id;
        if (BUILDSCAPE_ENTITIES.contains(path) && FabricPlatformAdapter.isRegistryAction()) return null;
        return Util.fetchChoiceType(reference, id);
    }
}

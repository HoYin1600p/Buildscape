package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.resources.bundle.runtime.BundledJsonPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.resource.PathResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(value = PathResourcePack.class, remap = false)
public abstract class PathResourcePackMixin {
    @Shadow
    protected abstract Path resolve(String... paths);

    @Unique
    private BundledJsonPack buildscape$jsonPack;

    @Unique
    private synchronized BundledJsonPack buildscape$jsonPack() {
        if (buildscape$jsonPack == null) {
            buildscape$jsonPack = new BundledJsonPack(path -> resolve(path));
        }
        return buildscape$jsonPack;
    }

    @Inject(method = {"getResource(Ljava/lang/String;)Ljava/io/InputStream;",
            "m_5541_(Ljava/lang/String;)Ljava/io/InputStream;"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$openJson(String path, CallbackInfoReturnable<InputStream> cir) throws IOException {
        InputStream stream = buildscape$jsonPack().open(path);
        if (stream != null) {
            cir.setReturnValue(stream);
        }
    }

    @Inject(method = {"hasResource(Ljava/lang/String;)Z",
            "m_6105_(Ljava/lang/String;)Z"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$hasJson(String path, CallbackInfoReturnable<Boolean> cir) {
        if (buildscape$jsonPack().contains(path)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"getResources", "m_7466_"}, at = @At("RETURN"), cancellable = true, require = 0)
    private void buildscape$listJson(PackType type, String namespace, String path, int maxDepth,
            Predicate<String> filter, CallbackInfoReturnable<Collection<ResourceLocation>> cir) {
        Collection<ResourceLocation> additional = buildscape$jsonPack().list(type, namespace, path, maxDepth, filter);
        if (!additional.isEmpty()) {
            Set<ResourceLocation> combined = new LinkedHashSet<>(cir.getReturnValue());
            combined.addAll(additional);
            cir.setReturnValue(combined);
        }
    }

    @Inject(method = {"getNamespaces", "m_5698_"}, at = @At("RETURN"), cancellable = true, require = 0)
    private void buildscape$jsonNamespaces(PackType type, CallbackInfoReturnable<Set<String>> cir) {
        Set<String> additional = buildscape$jsonPack().namespaces(type);
        if (!additional.isEmpty()) {
            Set<String> combined = new LinkedHashSet<>(cir.getReturnValue());
            combined.addAll(additional);
            cir.setReturnValue(combined);
        }
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void buildscape$closeJson(CallbackInfo ci) {
        if (buildscape$jsonPack != null) {
            buildscape$jsonPack.close();
        }
    }
}

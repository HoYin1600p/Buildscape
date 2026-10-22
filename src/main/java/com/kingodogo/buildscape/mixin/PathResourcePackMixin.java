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

@Mixin(value = PathResourcePack.class, remap = false, priority = 1100)
public abstract class PathResourcePackMixin {
    @Shadow
    protected abstract Path resolve(String... paths);

    @Unique
    private volatile BundledJsonPack buildscape$jsonPack;

    @Unique
    private BundledJsonPack buildscape$jsonPack() {
        BundledJsonPack pack = buildscape$jsonPack;
        if (pack != null) {
            return pack;
        }
        synchronized (this) {
            if (buildscape$jsonPack == null) {
                buildscape$jsonPack = new BundledJsonPack(path -> resolve(path));
            }
            return buildscape$jsonPack;
        }
    }

    @Inject(method = {"getResource(Ljava/lang/String;)Ljava/io/InputStream;",
            "m_5541_(Ljava/lang/String;)Ljava/io/InputStream;"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$openJson(String path, CallbackInfoReturnable<InputStream> cir) throws IOException {
        if (!path.startsWith("assets/buildscape/") && !path.startsWith("data/buildscape/")) {
            return;
        }
        InputStream stream = buildscape$jsonPack().open(path);
        if (stream != null) {
            cir.setReturnValue(stream);
        }
    }

    @Inject(method = {
            "getResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Ljava/io/InputStream;",
            "m_8031_(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Ljava/io/InputStream;"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$openLocation(PackType type, ResourceLocation location,
            CallbackInfoReturnable<InputStream> cir) throws IOException {
        if (!"buildscape".equals(location.getNamespace())) {
            return;
        }
        String path = (type == PackType.CLIENT_RESOURCES ? "assets/" : "data/") + location.getNamespace() + "/"
                + location.getPath();
        InputStream stream = buildscape$jsonPack().open(path);
        if (stream != null) {
            cir.setReturnValue(stream);
        }
    }

    @Inject(method = {"hasResource(Ljava/lang/String;)Z",
            "m_6105_(Ljava/lang/String;)Z"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$hasJson(String path, CallbackInfoReturnable<Boolean> cir) {
        if (!path.startsWith("assets/buildscape/") && !path.startsWith("data/buildscape/")) {
            return;
        }
        if (buildscape$jsonPack().contains(path)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"hasResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Z",
            "m_7211_(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Z"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$hasLocation(PackType type, ResourceLocation location, CallbackInfoReturnable<Boolean> cir) {
        if (!"buildscape".equals(location.getNamespace())) {
            return;
        }
        String path = (type == PackType.CLIENT_RESOURCES ? "assets/" : "data/") + location.getNamespace() + "/"
                + location.getPath();
        if (buildscape$jsonPack().contains(path)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"getResources", "m_7466_"}, at = @At("RETURN"), cancellable = true, require = 0)
    private void buildscape$listJson(PackType type, String namespace, String path, int maxDepth,
            Predicate<String> filter, CallbackInfoReturnable<Collection<ResourceLocation>> cir) {
        if (!"buildscape".equals(namespace)) {
            return;
        }
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

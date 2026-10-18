package com.kingodogo.buildscape.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class BuildscapeMixinPlugin implements IMixinConfigPlugin {
    private static final Set<String> OVERLAPPING_CACHE_MIXINS = Set.of(
            "com.kingodogo.buildscape.mixin.BuildscapeBlockModelMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeBlockStateCacheMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeForgeRegistryMixin"
    );

    private boolean standaloneLaunchFasterPresent;

    @Override
    public void onLoad(String mixinPackage) {
        standaloneLaunchFasterPresent = classExists("com.ruben.launchfaster.Launchfaster");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith(".EmbeddiumPipeSpillMixin")) {
            return classExists("me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder");
        }
        return !standaloneLaunchFasterPresent ||
                !OVERLAPPING_CACHE_MIXINS.contains(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }

    @Override
    public void postApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }

    private static boolean classExists(String className) {
        String resource = className.replace('.', '/') + ".class";
        ClassLoader loader = BuildscapeMixinPlugin.class.getClassLoader();
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        return (loader != null && loader.getResource(resource) != null)
                || (contextLoader != null && contextLoader.getResource(resource) != null);
    }
}

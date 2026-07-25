package com.kingodogo.buildscape.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Prevents Buildscape's cache hooks from competing with the standalone
 * LaunchFaster mod when both jars are installed. Model loading and baking
 * remain available because they can yield to LaunchFaster at runtime based on
 * its individual configuration values.
 *
 * @author hoyin1600p
 */
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
        try {
            Class.forName(className, false, BuildscapeMixinPlugin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}

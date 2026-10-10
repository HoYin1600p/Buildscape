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
            "com.kingodogo.buildscape.mixin.BuildscapeForgeRegistryMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeModelBakeryMixin"
    );

    private static final Set<String> OBSOLETE_118_MIXINS = Set.of(
            "com.kingodogo.buildscape.mixin.BuildscapeBlockModelMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeBlockStateCacheMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeForgeRegistryMixin",
            "com.kingodogo.buildscape.mixin.BuildscapeModelBakeryMixin",
            "com.kingodogo.buildscape.mixin.CreativeModeTabMixin",
            "com.kingodogo.buildscape.mixin.RenderBuffersMixin",
            "com.kingodogo.buildscape.mixin.ScreenMixin",
            "com.kingodogo.buildscape.mixin.AdvancementWidgetMixin",
            "com.kingodogo.buildscape.mixin.GeneralStatisticsListMixin",
            "com.kingodogo.buildscape.mixin.GeneralStatisticsListEntryMixin"
    );

    private boolean standaloneLaunchFasterPresent;
    private boolean isPost118;
    private boolean is26x;

    @Override
    public void onLoad(String mixinPackage) {
        standaloneLaunchFasterPresent = classExists("com.ruben.launchfaster.Launchfaster");
        isPost118 = classExists("net.minecraft.world.flag.FeatureFlagSet");
        is26x = classExists("net.minecraft.resources.Identifier");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("com.kingodogo.buildscape.mixin.client.")) return is26x;
        if (is26x && (mixinClassName.endsWith(".ScreenMixin") || mixinClassName.endsWith(".RenderBuffersMixin")
                || mixinClassName.endsWith(".AdvancementWidgetMixin") || mixinClassName.endsWith(".GeneralStatisticsListMixin")
                || mixinClassName.endsWith(".GeneralStatisticsListEntryMixin"))) return true;
        if (mixinClassName.endsWith(".WorldOverlayExtractionMixin")) return is26x;
        if (mixinClassName.endsWith(".EmbeddiumPipeSpillMixin")) {
            // No Sodium for 26.2 is cached; the legacy renderer API is incompatible.
            if (is26x) return false;
            return classExists("me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder");
        }
        if (standaloneLaunchFasterPresent && OVERLAPPING_CACHE_MIXINS.contains(mixinClassName)) {
            return false;
        }
        if (is26x && mixinClassName.endsWith(".CreativeModeTabMixin")) return true;
        if (isPost118 && OBSOLETE_118_MIXINS.contains(mixinClassName)) {
            return false;
        }
        if (is26x && mixinClassName.endsWith(".LiquidBlockRendererMixin")) {
            return true;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
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

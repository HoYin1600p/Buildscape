package com.kingodogo.buildscape.mixin;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import com.kingodogo.buildscape.util.SynchronizedListenerSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;

@Mixin(SimpleReloadInstance.class)
public abstract class SimpleReloadInstanceMixin {
    @Redirect(method = "<init>(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Lnet/minecraft/server/packs/resources/SimpleReloadInstance$StateFactory;Ljava/util/concurrent/CompletableFuture;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet(Ljava/lang/Iterable;)Ljava/util/HashSet;", remap = false))
    private HashSet<PreparableReloadListener> buildscape$synchronizePreparingListeners(
            Iterable<PreparableReloadListener> listeners) {
        // The initial server reload uses a direct game executor, so preparation
        // workers can remove listeners concurrently. Install the lock before any start.
        return new SynchronizedListenerSet(listeners);
    }
}

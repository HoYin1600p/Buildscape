package net.minecraft.client.renderer.feature;

import com.mojang.renderpearl.api.commands.RenderPass;
import java.util.List;
import net.minecraft.client.renderer.oit.OitStage;
import org.jspecify.annotations.Nullable;

public interface FeatureRenderer extends AutoCloseable {
   default void beginPrepare(final FeatureFrameContext context) {
   }

   void prepareGroup(FeatureFrameContext context, List submits, boolean strictlyOrdered);

   default void finishPrepare(final FeatureFrameContext context) {
   }

   void executeGroup(FeatureFrameContext context, @Nullable OitStage stage, RenderPass renderPass, int groupIndex, List submits, boolean strictlyOrdered);

   default void finishExecute(final FeatureFrameContext context) {
   }

   default void close() {
   }
}

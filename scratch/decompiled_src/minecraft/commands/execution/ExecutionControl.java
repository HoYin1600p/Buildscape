package net.minecraft.commands.execution;

import org.jspecify.annotations.Nullable;

public interface ExecutionControl {
   void queueNext(EntryAction action);

   void tracer(@Nullable TraceCallbacks tracer);

   @Nullable TraceCallbacks tracer();

   Frame currentFrame();

   static ExecutionControl create(final ExecutionContext context, final Frame frame) {
      return new ExecutionControl() {
         public void queueNext(final EntryAction action) {
            context.queueNext(new CommandQueueEntry(frame, action));
         }

         public void tracer(final @Nullable TraceCallbacks tracer) {
            context.tracer(tracer);
         }

         public @Nullable TraceCallbacks tracer() {
            return context.tracer();
         }

         public Frame currentFrame() {
            return frame;
         }
      };
   }
}

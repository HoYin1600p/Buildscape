package net.minecraft.commands.execution.tasks;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.network.chat.Component;

public class BuildContexts {
   @VisibleForTesting
   public static final DynamicCommandExceptionType ERROR_FORK_LIMIT_REACHED = new DynamicCommandExceptionType((limit) -> Component.translatableEscape("command.forkLimit", limit));
   private final String commandInput;
   private final ContextChain command;

   public BuildContexts(final String commandInput, final ContextChain command) {
      this.commandInput = commandInput;
      this.command = command;
   }

   protected void execute(final ExecutionCommandSource originalSource, final List initialSources, final ExecutionContext context, final Frame frame, final ChainModifiers initialModifiers) {
      ContextChain currentStage = this.command;
      ChainModifiers modifiers = initialModifiers;
      List currentSources = initialSources;
      if (currentStage.getStage() != Stage.EXECUTE) {
         context.profiler().push((Supplier)(() -> "prepare " + this.commandInput));

         try {
            for(int forkLimit = context.forkLimit(); currentStage.getStage() != Stage.EXECUTE; currentStage = currentStage.nextStage()) {
               CommandContext contextToRun = currentStage.getTopContext();
               if (contextToRun.isForked()) {
                  modifiers = modifiers.setForked();
               }

               RedirectModifier modifier = contextToRun.getRedirectModifier();
               if (modifier instanceof CustomModifierExecutor) {
                  CustomModifierExecutor customModifierExecutor = (CustomModifierExecutor)modifier;
                  customModifierExecutor.apply(originalSource, currentSources, currentStage, modifiers, ExecutionControl.create(context, frame));
                  return;
               }

               if (modifier != null) {
                  context.incrementCost();
                  boolean forkedMode = modifiers.isForked();
                  List nextSources = new ObjectArrayList();

                  for(ExecutionCommandSource source : currentSources) {
                     try {
                        Collection newSources = ContextChain.runModifier(contextToRun, source, (c, s, r) -> {
                        }, forkedMode);
                        if (nextSources.size() + newSources.size() >= forkLimit) {
                           originalSource.handleError(ERROR_FORK_LIMIT_REACHED.create(forkLimit), forkedMode, context.tracer());
                           return;
                        }

                        nextSources.addAll(newSources);
                     } catch (CommandSyntaxException var20) {
                        source.handleError(var20, forkedMode, context.tracer());
                        if (!forkedMode) {
                           return;
                        }
                     }
                  }

                  currentSources = nextSources;
               }
            }
         } finally {
            context.profiler().pop();
         }
      }

      if (currentSources.isEmpty()) {
         if (modifiers.isReturn()) {
            context.queueNext(new CommandQueueEntry(frame, FallthroughTask.instance()));
         }

      } else {
         CommandContext executeContext = currentStage.getTopContext();
         Command command = executeContext.getCommand();
         if (command instanceof CustomCommandExecutor) {
            CustomCommandExecutor customCommandExecutor = (CustomCommandExecutor)command;
            ExecutionControl executionControl = ExecutionControl.create(context, frame);

            for(ExecutionCommandSource executionSource : currentSources) {
               customCommandExecutor.run(executionSource, currentStage, modifiers, executionControl);
            }
         } else {
            if (modifiers.isReturn()) {
               ExecutionCommandSource returningSource = (T)((ExecutionCommandSource)currentSources.get(0));
               returningSource = (T)returningSource.withCallback(CommandResultCallback.chain(returningSource.callback(), frame.returnValueConsumer()));
               currentSources = List.of(returningSource);
            }

            ExecuteCommand action = new ExecuteCommand(this.commandInput, modifiers, executeContext);
            ContinuationTask.schedule(context, frame, currentSources, (frame1, entrySource) -> new CommandQueueEntry(frame1, action.bind(entrySource)));
         }

      }
   }

   protected void traceCommandStart(final ExecutionContext context, final Frame frame) {
      TraceCallbacks tracer = context.tracer();
      if (tracer != null) {
         tracer.onCommand(frame.depth(), this.commandInput);
      }

   }

   public String toString() {
      return this.commandInput;
   }

   public static class Continuation extends BuildContexts implements EntryAction {
      private final ChainModifiers modifiers;
      private final ExecutionCommandSource originalSource;
      private final List sources;

      public Continuation(final String commandInput, final ContextChain command, final ChainModifiers modifiers, final ExecutionCommandSource originalSource, final List sources) {
         super(commandInput, command);
         this.originalSource = originalSource;
         this.sources = sources;
         this.modifiers = modifiers;
      }

      public void execute(final ExecutionContext context, final Frame frame) {
         this.execute(this.originalSource, this.sources, context, frame, this.modifiers);
      }
   }

   public static class TopLevel extends BuildContexts implements EntryAction {
      private final ExecutionCommandSource source;

      public TopLevel(final String commandInput, final ContextChain command, final ExecutionCommandSource source) {
         super(commandInput, command);
         this.source = source;
      }

      public void execute(final ExecutionContext context, final Frame frame) {
         this.traceCommandStart(context, frame);
         this.execute(this.source, List.of(this.source), context, frame, ChainModifiers.DEFAULT);
      }
   }

   public static class Unbound extends BuildContexts implements UnboundEntryAction {
      public Unbound(final String commandInput, final ContextChain command) {
         super(commandInput, command);
      }

      public void execute(final ExecutionCommandSource sender, final ExecutionContext context, final Frame frame) {
         this.traceCommandStart(context, frame);
         this.execute(sender, List.of(sender), context, frame, ChainModifiers.DEFAULT);
      }
   }
}

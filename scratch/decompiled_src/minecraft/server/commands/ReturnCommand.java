package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.FallthroughTask;

public class ReturnCommand {
   public static void register(final CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal("return").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(RequiredArgumentBuilder.argument("value", IntegerArgumentType.integer()).executes(new ReturnCommand.ReturnValueCustomExecutor()))).then(LiteralArgumentBuilder.literal("fail").executes(new ReturnCommand.ReturnFailCustomExecutor()))).then(LiteralArgumentBuilder.literal("run").forward(dispatcher.getRoot(), new ReturnCommand.ReturnFromCommandCustomModifier(), false)));
   }

   private static class ReturnFailCustomExecutor implements CustomCommandExecutor.CommandAdapter {
      public void run(final ExecutionCommandSource sender, final ContextChain currentStep, final ChainModifiers modifiers, final ExecutionControl output) {
         sender.callback().onFailure();
         Frame frame = output.currentFrame();
         frame.returnFailure();
         frame.discard();
      }
   }

   private static class ReturnFromCommandCustomModifier implements CustomModifierExecutor.ModifierAdapter {
      public void apply(final ExecutionCommandSource originalSource, final List currentSources, final ContextChain currentStep, final ChainModifiers modifiers, final ExecutionControl output) {
         if (currentSources.isEmpty()) {
            if (modifiers.isReturn()) {
               output.queueNext(FallthroughTask.instance());
            }

         } else {
            output.currentFrame().discard();
            ContextChain nextState = currentStep.nextStage();
            String command = nextState.getTopContext().getInput();
            output.queueNext(new BuildContexts.Continuation(command, nextState, modifiers.setReturn(), originalSource, currentSources));
         }
      }
   }

   private static class ReturnValueCustomExecutor implements CustomCommandExecutor.CommandAdapter {
      public void run(final ExecutionCommandSource sender, final ContextChain currentStep, final ChainModifiers modifiers, final ExecutionControl output) {
         int returnValue = IntegerArgumentType.getInteger(currentStep.getTopContext(), "value");
         sender.callback().onSuccess(returnValue);
         Frame frame = output.currentFrame();
         frame.returnSuccess(returnValue);
         frame.discard();
      }
   }
}

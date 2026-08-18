package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DebugConfigCommand;
import net.minecraft.server.commands.DebugMobSpawningCommand;
import net.minecraft.server.commands.DebugPathCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FetchProfileCommand;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PostEffectCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RaidCommand;
import net.minecraft.server.commands.RandomCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReturnCommand;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.server.commands.RotateCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.ServerPackCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.StopwatchCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.SwingCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TickCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TransferCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.UnpublishCommand;
import net.minecraft.server.commands.VersionCommand;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.server.commands.WaypointCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.commands.item.ItemCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Commands {
   public static final String COMMAND_PREFIX = "/";
   private static final ThreadLocal CURRENT_EXECUTION_CONTEXT = new ThreadLocal();
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final PermissionCheck LEVEL_ALL = PermissionCheck.AlwaysPass.INSTANCE;
   public static final PermissionCheck LEVEL_MODERATORS = new PermissionCheck.Require(Permissions.COMMANDS_MODERATOR);
   public static final PermissionCheck LEVEL_GAMEMASTERS = new PermissionCheck.Require(Permissions.COMMANDS_GAMEMASTER);
   public static final PermissionCheck LEVEL_ADMINS = new PermissionCheck.Require(Permissions.COMMANDS_ADMIN);
   public static final PermissionCheck LEVEL_OWNERS = new PermissionCheck.Require(Permissions.COMMANDS_OWNER);
   private static final ClientboundCommandsPacket.NodeInspector COMMAND_NODE_INSPECTOR = new ClientboundCommandsPacket.NodeInspector() {
      private final CommandSourceStack noPermissionSource = Commands.createCompilationContext(PermissionSet.NO_PERMISSIONS);

      public @Nullable Identifier suggestionId(final ArgumentCommandNode node) {
         SuggestionProvider suggestionProvider = node.getCustomSuggestions();
         return suggestionProvider != null ? SuggestionProviders.getName(suggestionProvider) : null;
      }

      public boolean isExecutable(final CommandNode node) {
         return node.getCommand() != null;
      }

      public boolean isRestricted(final CommandNode node) {
         Predicate requirement = node.getRequirement();
         return !requirement.test(this.noPermissionSource);
      }
   };
   private final CommandDispatcher dispatcher = new CommandDispatcher();

   public Commands(final Commands.CommandSelection commandSelection, final CommandBuildContext context) {
      AdvancementCommands.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher, context);
      ExecuteCommand.register(this.dispatcher, context);
      BossBarCommands.register(this.dispatcher, context);
      ClearInventoryCommands.register(this.dispatcher, context);
      CloneCommands.register(this.dispatcher, context);
      DamageCommand.register(this.dispatcher, context);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher, context);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DialogCommand.register(this.dispatcher, context);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher, context);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher, context);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher, context);
      FillBiomeCommand.register(this.dispatcher, context);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher, context);
      GiveCommand.register(this.dispatcher, context);
      HelpCommand.register(this.dispatcher);
      ItemCommands.register(this.dispatcher, context);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher, context);
      LootCommand.register(this.dispatcher, context);
      MsgCommand.register(this.dispatcher);
      SwingCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher, context);
      PlaceCommand.register(this.dispatcher, context);
      PlaySoundCommand.register(this.dispatcher);
      PostEffectCommand.register(this.dispatcher);
      RandomCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      FetchProfileCommand.register(this.dispatcher);
      ReturnCommand.register(this.dispatcher);
      RideCommand.register(this.dispatcher);
      RotateCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher, context);
      SeedCommand.register(this.dispatcher, commandSelection != Commands.CommandSelection.INTEGRATED);
      VersionCommand.register(this.dispatcher, commandSelection != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(this.dispatcher, context);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      StopwatchCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher, context);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher, context);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher, context);
      TestCommand.register(this.dispatcher, context);
      TickCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher, context);
      TitleCommand.register(this.dispatcher, context);
      TriggerCommand.register(this.dispatcher);
      WaypointCommand.register(this.dispatcher, context);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (JvmProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(this.dispatcher);
      }

      if (SharedConstants.DEBUG_CHASE_COMMAND) {
         ChaseCommand.register(this.dispatcher);
      }

      if (SharedConstants.DEBUG_DEV_COMMANDS || SharedConstants.IS_RUNNING_IN_IDE) {
         RaidCommand.register(this.dispatcher, context);
         DebugPathCommand.register(this.dispatcher);
         DebugMobSpawningCommand.register(this.dispatcher);
         WardenSpawnTrackerCommand.register(this.dispatcher);
         SpawnArmorTrimsCommand.register(this.dispatcher);
         ServerPackCommand.register(this.dispatcher);
         if (commandSelection.includeDedicated) {
            DebugConfigCommand.register(this.dispatcher, context);
         }
      }

      if (commandSelection.includeDedicated) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         PerfCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetPlayerIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         TransferCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      if (commandSelection.includeIntegrated) {
         PublishCommand.register(this.dispatcher);
         UnpublishCommand.register(this.dispatcher);
      }

      this.dispatcher.setConsumer(ExecutionCommandSource.resultConsumer());
   }

   public static ParseResults mapSource(final ParseResults parse, final UnaryOperator sourceOperator) {
      CommandContextBuilder context = parse.getContext();
      CommandContextBuilder source = context.withSource(sourceOperator.apply(context.getSource()));
      return new ParseResults(source, parse.getReader(), parse.getExceptions());
   }

   public void performPrefixedCommand(final CommandSourceStack sender, String command) {
      command = trimOptionalPrefix(command);
      this.performCommand(this.dispatcher.parse(command, sender), command);
   }

   public static String trimOptionalPrefix(final String command) {
      return command.startsWith("/") ? command.substring(1) : command;
   }

   public void performCommand(final ParseResults command, final String commandString) {
      CommandSourceStack sender = (CommandSourceStack)command.getContext().getSource();
      Profiler.get().push((Supplier)(() -> "/" + commandString));
      ContextChain commandChain = finishParsing(command, commandString, sender);

      try {
         if (commandChain != null) {
            executeCommandInContext(sender, (executionContext) -> ExecutionContext.queueInitialCommandExecution(executionContext, commandString, commandChain, sender, CommandResultCallback.EMPTY));
         }
      } catch (Exception var12) {
         MutableComponent hover = Component.literal(var12.getMessage() == null ? var12.getClass().getName() : var12.getMessage());
         if (LOGGER.isDebugEnabled()) {
            LOGGER.error("Command exception: /{}", commandString, var12);
            StackTraceElement[] stackTrace = var12.getStackTrace();

            for(int i = 0; i < Math.min(stackTrace.length, 3); ++i) {
               hover.append("\n\n").append(stackTrace[i].getMethodName()).append("\n ").append(stackTrace[i].getFileName()).append(":").append(String.valueOf(stackTrace[i].getLineNumber()));
            }
         }

         sender.sendFailure(Component.translatable("command.failed").withStyle((s) -> s.withHoverEvent(new HoverEvent.ShowText(hover))));
         if (SharedConstants.DEBUG_VERBOSE_COMMAND_ERRORS || SharedConstants.IS_RUNNING_IN_IDE) {
            sender.sendFailure(Component.literal(Util.describeError(var12)));
            LOGGER.error("'/{}' threw an exception", commandString, var12);
         }
      } finally {
         Profiler.get().pop();
      }

   }

   private static @Nullable ContextChain finishParsing(final ParseResults command, final String commandString, final CommandSourceStack sender) {
      try {
         validateParseResults(command);
         return (ContextChain)ContextChain.tryFlatten(command.getContext().build(commandString)).orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(command.getReader()));
      } catch (CommandSyntaxException var7) {
         sender.sendFailure(ComponentUtils.fromMessage(var7.getRawMessage()));
         if (var7.getInput() != null && var7.getCursor() >= 0) {
            int cursor = Math.min(var7.getInput().length(), var7.getCursor());
            MutableComponent context = Component.empty().withStyle(ChatFormatting.GRAY).withStyle((s) -> s.withClickEvent(new ClickEvent.SuggestCommand("/" + commandString)));
            if (cursor > 10) {
               context.append(CommonComponents.ELLIPSIS);
            }

            context.append(var7.getInput().substring(Math.max(0, cursor - 10), cursor));
            if (cursor < var7.getInput().length()) {
               Component remaining = Component.literal(var7.getInput().substring(cursor)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
               context.append(remaining);
            }

            context.append(Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
            sender.sendFailure(context);
         }

         return null;
      }
   }

   public static void executeCommandInContext(final CommandSourceStack context, final Consumer config) {
      ExecutionContext currentContext = (ExecutionContext)CURRENT_EXECUTION_CONTEXT.get();
      boolean isTopContext = currentContext == null;
      if (isTopContext) {
         GameRules gameRules = context.getLevel().getGameRules();
         int chainLimit = Math.max(1, gameRules.get(GameRules.MAX_COMMAND_SEQUENCE_LENGTH));
         int forkLimit = gameRules.get(GameRules.MAX_COMMAND_FORKS);

         try (ExecutionContext executionContext = new ExecutionContext(chainLimit, forkLimit, Profiler.get())) {
            CURRENT_EXECUTION_CONTEXT.set(executionContext);
            config.accept(executionContext);
            executionContext.runCommandQueue();
         } finally {
            CURRENT_EXECUTION_CONTEXT.set((Object)null);
         }
      } else {
         config.accept(currentContext);
      }

   }

   public void sendCommands(final ServerPlayer player) {
      Map playerCommands = new HashMap();
      RootCommandNode root = new RootCommandNode();
      playerCommands.put(this.dispatcher.getRoot(), root);
      fillUsableCommands(this.dispatcher.getRoot(), root, player.createCommandSourceStack(), playerCommands);
      player.connection.send(new ClientboundCommandsPacket(root, COMMAND_NODE_INSPECTOR));
   }

   private static void fillUsableCommands(final CommandNode source, final CommandNode target, final Object commandFilter, final Map converted) {
      for(CommandNode child : source.getChildren()) {
         if (child.canUse(commandFilter)) {
            ArgumentBuilder builder = child.createBuilder();
            if (builder.getRedirect() != null) {
               builder.redirect((CommandNode)converted.get(builder.getRedirect()));
            }

            CommandNode node = builder.build();
            converted.put(child, node);
            target.addChild(node);
            if (!child.getChildren().isEmpty()) {
               fillUsableCommands(child, node, commandFilter, converted);
            }
         }
      }

   }

   public static LiteralArgumentBuilder literal(final String literal) {
      return LiteralArgumentBuilder.literal(literal);
   }

   public static RequiredArgumentBuilder argument(final String name, final ArgumentType type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   public static Predicate createValidator(final Commands.ParseFunction parser) {
      return (value) -> {
         try {
            parser.parse(new StringReader(value));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher getDispatcher() {
      return this.dispatcher;
   }

   public static void validateParseResults(final ParseResults command) throws CommandSyntaxException {
      CommandSyntaxException parseException = getParseException(command);
      if (parseException != null) {
         throw parseException;
      }
   }

   public static @Nullable CommandSyntaxException getParseException(final ParseResults parse) {
      if (!parse.getReader().canRead()) {
         return null;
      } else if (parse.getExceptions().size() == 1) {
         return (CommandSyntaxException)parse.getExceptions().values().iterator().next();
      } else {
         return parse.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
      }
   }

   public static CommandBuildContext createValidationContext(final HolderLookup.Provider registries) {
      return new CommandBuildContext() {
         public FeatureFlagSet enabledFeatures() {
            return FeatureFlags.REGISTRY.allFlags();
         }

         public Stream listRegistryKeys() {
            return registries.listRegistryKeys();
         }

         public Optional lookup(final ResourceKey key) {
            return registries.lookup(key).map(this::createLookup);
         }

         private HolderLookup.RegistryLookup.Delegate createLookup(final HolderLookup.RegistryLookup original) {
            return new HolderLookup.RegistryLookup.Delegate(this) {
               {
                  Objects.requireNonNull(this$0);
               }

               public HolderLookup.RegistryLookup parent() {
                  return original;
               }

               public Optional get(final TagKey id) {
                  return Optional.of(this.getOrThrow(id));
               }

               public HolderSet.Named getOrThrow(final TagKey id) {
                  Optional tag = this.parent().get(id);
                  return (HolderSet.Named)tag.orElseGet(() -> HolderSet.emptyNamed(this.parent(), id));
               }
            };
         }
      };
   }

   public static void validate() {
      CommandBuildContext context = createValidationContext(VanillaRegistries.createWorldLookup());
      CommandDispatcher dispatcher = (new Commands(Commands.CommandSelection.ALL, context)).getDispatcher();
      RootCommandNode root = dispatcher.getRoot();
      dispatcher.findAmbiguities((parent, child, sibling, ambiguities) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{dispatcher.getPath(child), dispatcher.getPath(sibling), ambiguities}));
      Set usedArgumentTypes = ArgumentUtils.findUsedArgumentTypes(root);
      Set unregisteredTypes = (Set)usedArgumentTypes.stream().filter((arg) -> !ArgumentTypeInfos.isClassRecognized(arg.getClass())).collect(Collectors.toSet());
      if (!unregisteredTypes.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", unregisteredTypes.stream().map((arg) -> "\t" + String.valueOf(arg)).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static PermissionProviderCheck hasPermission(final PermissionCheck permission) {
      return new PermissionProviderCheck(permission);
   }

   public static CommandSourceStack createCompilationContext(final PermissionSet compilationPermissions) {
      return new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, compilationPermissions, "", CommonComponents.EMPTY, (MinecraftServer)null, (Entity)null);
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      private final boolean includeIntegrated;
      private final boolean includeDedicated;

      private CommandSelection(final boolean includeIntegrated, final boolean includeDedicated) {
         this.includeIntegrated = includeIntegrated;
         this.includeDedicated = includeDedicated;
      }

      // $FF: synthetic method
      private static Commands.CommandSelection[] $values() {
         return new Commands.CommandSelection[]{ALL, DEDICATED, INTEGRATED};
      }
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader value) throws CommandSyntaxException;
   }
}

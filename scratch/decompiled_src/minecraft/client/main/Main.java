package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.blaze3d.TracyBootstrap;
import com.mojang.blaze3d.platform.ClientShutdownWatchdog;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.NativeLibrariesBootstrap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.EnumConverter;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Optionull;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.PreferredGraphicsApi;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Main {
   public static void main(final String[] args) {
      try {
         SharedConstants.tryDetectVersion();
      } catch (Throwable var76) {
         logEarlyException(var76);
         System.exit(-7);
         return;
      }

      OptionParser parser;
      try {
         parser = new OptionParser();
      } catch (Throwable var75) {
         logEarlyException(var75);
         System.exit(-4);
         return;
      }

      parser.allowsUnrecognizedOptions();
      parser.accepts("demo");
      parser.accepts("disableMultiplayer");
      parser.accepts("disableChat");
      parser.accepts("fullscreen");
      parser.accepts("checkGlErrors");
      OptionSpec renderDebugLabelsOption = parser.accepts("renderDebugLabels");
      OptionSpec vulkanValidationOption = parser.accepts("vulkanValidation");
      OptionSpec graphicsBackendOption = parser.accepts("graphicsBackend").withRequiredArg().withValuesConvertedBy(new EnumConverter(PreferredGraphicsApi.class) {
      });
      OptionSpec jfrProfilingOption = parser.accepts("jfrProfile");
      OptionSpec tracyProfilingOption = parser.accepts("tracy");
      OptionSpec tracyNoImageOption = parser.accepts("tracyNoImages");
      OptionSpec quickPlayPathOption = parser.accepts("quickPlayPath").withRequiredArg();
      OptionSpec quickPlaySingleplayerOption = parser.accepts("quickPlaySingleplayer").withOptionalArg();
      OptionSpec quickPlayMultiplayerOption = parser.accepts("quickPlayMultiplayer").withRequiredArg();
      OptionSpec quickPlayRealmsOption = parser.accepts("quickPlayRealms").withRequiredArg();
      OptionSpec gameDirOption = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      OptionSpec assetsDirOption = parser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec resourcePackDirOption = parser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec proxyHostOption = parser.accepts("proxyHost").withRequiredArg();
      OptionSpec proxyPortOption = parser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      OptionSpec proxyUserOption = parser.accepts("proxyUser").withRequiredArg();
      OptionSpec proxyPassOption = parser.accepts("proxyPass").withRequiredArg();
      OptionSpec usernameOption = parser.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L, new String[0]);
      OptionSpec offlineDeveloperMode = parser.accepts("offlineDeveloperMode");
      OptionSpec uuidOption = parser.accepts("uuid").withRequiredArg();
      OptionSpec xuidOption = parser.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      OptionSpec clientIdOption = parser.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      OptionSpec accessTokenOption = parser.accepts("accessToken").withRequiredArg().required();
      OptionSpec versionOption = parser.accepts("version").withRequiredArg().required();
      OptionSpec widthOption = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      OptionSpec heightOption = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      OptionSpec fullscreenWidthOption = parser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec fullscreenHeightOption = parser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec assetIndexOption = parser.accepts("assetIndex").withRequiredArg();
      OptionSpec versionTypeString = parser.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      OptionSpec nonOption = parser.nonOptions();

      OptionSet optionSet;
      try {
         optionSet = parser.parse(args);
      } catch (Throwable var74) {
         logEarlyException(var74);
         System.exit(-5);
         return;
      }

      File gameDir = (File)parseArgument(optionSet, gameDirOption);
      String launchedVersion = (String)parseArgument(optionSet, versionOption);

      try {
         NativeLibrariesBootstrap.loadLibraries();
      } catch (Throwable var73) {
         CrashReport report = CrashReport.forThrowable(var73, "Loading native libraries");
         CrashReportCategory initialization = report.addCategory("Initialization");
         NativeModuleLister.addCrashSection(initialization);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, launchedVersion, (Options)null, report);
         Minecraft.crash((Minecraft)null, gameDir, report, -3);
         return;
      }

      String stage = "Pre-bootstrap";

      Logger logger;
      GameConfig gameConfig;
      try {
         if (optionSet.has(jfrProfilingOption)) {
            JvmProfiler.INSTANCE.start(Environment.CLIENT);
         }

         if (optionSet.has(tracyProfilingOption)) {
            TracyBootstrap.setup();
         }

         Stopwatch totalTimePreClassLoadTimer = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch preWindowPreClassLoadTimer = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, totalTimePreClassLoadTimer);
         GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, preWindowPreClassLoadTimer);
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getCurrentVersion().name());
         CompletableFuture dataFixerOptimization = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
         CrashReport.preload();
         logger = LogUtils.getLogger();
         stage = "Bootstrap";
         Bootstrap.bootStrap();
         ClientBootstrap.bootstrap();
         GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
         Bootstrap.validate();
         stage = "Argument parsing";
         List leftoverArgs = optionSet.valuesOf(nonOption);
         if (!leftoverArgs.isEmpty()) {
            logger.info("Completely ignored arguments: {}", leftoverArgs);
         }

         String hostName = (String)parseArgument(optionSet, proxyHostOption);
         Proxy proxy = Proxy.NO_PROXY;
         if (hostName != null) {
            try {
               proxy = new Proxy(Type.SOCKS, new InetSocketAddress(hostName, parseArgument(optionSet, proxyPortOption)));
            } catch (Exception var72) {
            }
         }

         final String proxyUser = (String)parseArgument(optionSet, proxyUserOption);
         final String proxyPass = (String)parseArgument(optionSet, proxyPassOption);
         if (!proxy.equals(Proxy.NO_PROXY) && stringHasValue(proxyUser) && stringHasValue(proxyPass)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
               }
            });
         }

         int width = parseArgument(optionSet, widthOption);
         int height = parseArgument(optionSet, heightOption);
         OptionalInt fullscreenWidth = ofNullable((Integer)parseArgument(optionSet, fullscreenWidthOption));
         OptionalInt fullscreenHeight = ofNullable((Integer)parseArgument(optionSet, fullscreenHeightOption));
         boolean isFullscreen = optionSet.has("fullscreen");
         boolean isDemo = optionSet.has("demo");
         boolean disableMultiplayer = optionSet.has("disableMultiplayer");
         boolean disableChat = optionSet.has("disableChat");
         boolean captureTracyImages = !optionSet.has(tracyNoImageOption);
         boolean renderDebugLabels = optionSet.has(renderDebugLabelsOption);
         String versionType = (String)parseArgument(optionSet, versionTypeString);
         boolean vulkanValidation = optionSet.has(vulkanValidationOption);
         PreferredGraphicsApi forcedGraphicsApi = (PreferredGraphicsApi)parseArgument(optionSet, graphicsBackendOption);
         File assetsDir = optionSet.has(assetsDirOption) ? (File)parseArgument(optionSet, assetsDirOption) : new File(gameDir, "assets/");
         File resourcePackDir = optionSet.has(resourcePackDirOption) ? (File)parseArgument(optionSet, resourcePackDirOption) : new File(gameDir, "resourcepacks/");
         UUID uuid = hasValidUuid(uuidOption, optionSet, logger) ? UndashedUuid.fromStringLenient((String)uuidOption.value(optionSet)) : UUIDUtil.createOfflinePlayerUUID((String)usernameOption.value(optionSet));
         String assetIndex = optionSet.has(assetIndexOption) ? (String)assetIndexOption.value(optionSet) : null;
         String xuid = (String)optionSet.valueOf(xuidOption);
         String clientId = (String)optionSet.valueOf(clientIdOption);
         String quickPlayLogPath = (String)parseArgument(optionSet, quickPlayPathOption);
         GameConfig.QuickPlayVariant quickPlayVariant = getQuickPlayVariant(optionSet, quickPlaySingleplayerOption, quickPlayMultiplayerOption, quickPlayRealmsOption);
         User user = new User((String)usernameOption.value(optionSet), uuid, (String)accessTokenOption.value(optionSet), emptyStringToEmptyOptional(xuid), emptyStringToEmptyOptional(clientId));
         gameConfig = new GameConfig(new GameConfig.UserData(user, proxy), new DisplayData(width, height, fullscreenWidth, fullscreenHeight, isFullscreen), new GameConfig.FolderData(gameDir, resourcePackDir, assetsDir, assetIndex), new GameConfig.GameData(isDemo, launchedVersion, versionType, disableMultiplayer, disableChat, captureTracyImages, vulkanValidation, renderDebugLabels, forcedGraphicsApi, optionSet.has(offlineDeveloperMode)), new GameConfig.QuickPlayData(quickPlayLogPath, quickPlayVariant));
         Util.startTimerHackThread();
         dataFixerOptimization.join();
      } catch (Throwable var77) {
         CrashReport report = CrashReport.forThrowable(var77, stage);
         CrashReportCategory initialization = report.addCategory("Initialization");
         NativeModuleLister.addCrashSection(initialization);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, launchedVersion, (Options)null, report);
         Minecraft.crash((Minecraft)null, gameDir, report, -1);
         return;
      }

      Thread shutdownThread = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft instance = Minecraft.getInstance();
            if (instance != null) {
               IntegratedServer server = instance.getSingleplayerServer();
               if (server != null) {
                  server.halt(true);
               }

            }
         }
      };
      shutdownThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(logger));
      Runtime.getRuntime().addShutdownHook(shutdownThread);
      Minecraft minecraft = null;

      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         minecraft = new Minecraft(gameConfig);
      } catch (SilentInitException var70) {
         Util.shutdownExecutors();
         logger.warn("Failed to create window: ", var70);
         return;
      } catch (Throwable var71) {
         CrashReport report = CrashReport.forThrowable(var71, "Initializing game");
         CrashReportCategory initialization = report.addCategory("Initialization");
         NativeModuleLister.addCrashSection(initialization);
         Minecraft.fillReport(minecraft, (LanguageManager)null, gameConfig.game.launchVersion, (Options)null, report);
         Minecraft.crash(minecraft, gameConfig.location.gameDirectory, report, -1);
         return;
      }

      minecraft.run();

      try {
         minecraft.exitWorldAndClose();
      } catch (Throwable var69) {
         CrashReport report = CrashReport.forThrowable(var69, "Game shutdown");
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, launchedVersion, (Options)null, report);
         Minecraft.crash((Minecraft)null, gameDir, report, -6);
         return;
      }

      ClientShutdownWatchdog.startShutdownWatchdog("post-main", true, (Minecraft)null, gameConfig, Thread.currentThread().threadId());
   }

   @SuppressForbidden(
      reason = "Logging not available yet"
   )
   private static void logEarlyException(final Throwable t) {
      t.printStackTrace();
   }

   private static GameConfig.QuickPlayVariant getQuickPlayVariant(final OptionSet optionSet, final OptionSpec quickPlaySingleplayerOption, final OptionSpec quickPlayMultiplayerOption, final OptionSpec quickPlayRealmsOption) {
      long enabledOptions = Stream.of(quickPlaySingleplayerOption, quickPlayMultiplayerOption, quickPlayRealmsOption).filter(optionSet::has).count();
      if (enabledOptions == 0L) {
         return GameConfig.QuickPlayVariant.DISABLED;
      } else if (enabledOptions > 1L) {
         throw new IllegalArgumentException("Only one quick play option can be specified");
      } else if (optionSet.has(quickPlaySingleplayerOption)) {
         String worldId = unescapeJavaArgument((String)parseArgument(optionSet, quickPlaySingleplayerOption));
         return new GameConfig.QuickPlaySinglePlayerData(worldId);
      } else if (optionSet.has(quickPlayMultiplayerOption)) {
         String serverAddress = unescapeJavaArgument((String)parseArgument(optionSet, quickPlayMultiplayerOption));
         return (GameConfig.QuickPlayVariant)Optionull.mapOrDefault(serverAddress, GameConfig.QuickPlayMultiplayerData::new, GameConfig.QuickPlayVariant.DISABLED);
      } else if (optionSet.has(quickPlayRealmsOption)) {
         String realmId = unescapeJavaArgument((String)parseArgument(optionSet, quickPlayRealmsOption));
         return (GameConfig.QuickPlayVariant)Optionull.mapOrDefault(realmId, GameConfig.QuickPlayRealmsData::new, GameConfig.QuickPlayVariant.DISABLED);
      } else {
         return GameConfig.QuickPlayVariant.DISABLED;
      }
   }

   private static @Nullable String unescapeJavaArgument(final @Nullable String arg) {
      return arg == null ? null : StringEscapeUtils.unescapeJava(arg);
   }

   private static Optional emptyStringToEmptyOptional(final String xuid) {
      return xuid.isEmpty() ? Optional.empty() : Optional.of(xuid);
   }

   private static OptionalInt ofNullable(final @Nullable Integer value) {
      return value != null ? OptionalInt.of(value) : OptionalInt.empty();
   }

   private static @Nullable Object parseArgument(final OptionSet optionSet, final OptionSpec optionSpec) {
      try {
         return optionSet.valueOf(optionSpec);
      } catch (Throwable var5) {
         if (optionSpec instanceof ArgumentAcceptingOptionSpec options) {
            List defaultValues = options.defaultValues();
            if (!defaultValues.isEmpty()) {
               return defaultValues.get(0);
            }
         }

         throw var5;
      }
   }

   private static boolean stringHasValue(final @Nullable String string) {
      return string != null && !string.isEmpty();
   }

   private static boolean hasValidUuid(final OptionSpec uuidOption, final OptionSet optionSet, final Logger logger) {
      return optionSet.has(uuidOption) && isUuidValid(uuidOption, optionSet, logger);
   }

   private static boolean isUuidValid(final OptionSpec uuidOption, final OptionSet optionSet, final Logger logger) {
      try {
         UndashedUuid.fromStringLenient((String)uuidOption.value(optionSet));
         return true;
      } catch (IllegalArgumentException var4) {
         logger.warn("Invalid UUID: '{}", uuidOption.value(optionSet));
         return false;
      }
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}

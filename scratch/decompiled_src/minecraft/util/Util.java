package net.minecraft.util;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.CharPredicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.TracingExecutor;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Util {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_MAX_THREADS = 255;
   private static final int DEFAULT_SAFE_FILE_OPERATION_RETRIES = 10;
   private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";
   private static final TracingExecutor BACKGROUND_EXECUTOR = makeExecutor("Main");
   private static final TracingExecutor IO_POOL = makeIoExecutor("IO-Worker-", false);
   private static final TracingExecutor DOWNLOAD_POOL = makeIoExecutor("Download-", true);
   private static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
   public static final int LINEAR_LOOKUP_THRESHOLD = 8;
   private static final Set ALLOWED_UNTRUSTED_LINK_PROTOCOLS = Set.of("http", "https");
   public static final long NANOS_PER_MILLI = 1000000L;
   public static final long MILLIS_PER_SECOND = 1000L;
   private static TimeSource.NanoTimeSource timeSource = System::nanoTime;
   private static final TimeSource.NanoTimeSource INDIRECT_TIME_SOURCE = () -> timeSource.getAsLong();
   public static final Ticker TICKER = new Ticker() {
      public long read() {
         return Util.timeSource.getAsLong();
      }
   };
   public static final UUID NIL_UUID = new UUID(0L, 0L);
   public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((p) -> p.getScheme().equalsIgnoreCase("jar")).findFirst().orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
   public static final Escaper CONTROL_CHARACTER_ESCAPER = ((Escapers.Builder)make(Escapers.builder(), (escaper) -> {
      HexFormat hexFormat = HexFormat.of().withUpperCase();

      for(char c = 0; c <= 255; ++c) {
         if (Character.isISOControl(c)) {
            String var10000;
            switch (c) {
               case '\u0007':
                  var10000 = "\\a";
                  break;
               case '\b':
                  var10000 = "\\b";
                  break;
               case '\t':
                  var10000 = "\\t";
                  break;
               case '\n':
                  var10000 = "\\n";
                  break;
               case '\u000b':
                  var10000 = "\\v";
                  break;
               case '\f':
                  var10000 = "\\f";
                  break;
               case '\r':
                  var10000 = "\\r";
                  break;
               default:
                  var10000 = "\\x" + hexFormat.toHexDigits((long)c, 2);
            }

            String replacement = var10000;
            escaper.addEscape(c, replacement);
         }
      }

      escaper.addEscape('\\', "\\\\");
   })).build();
   private static Consumer thePauser = (msg) -> {
   };

   public static Collector toMap() {
      return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
   }

   public static Collector toMutableList() {
      return Collectors.toCollection(Lists::newArrayList);
   }

   public static String getPropertyName(final Property key, final Object value) {
      return key.getName((Comparable)value);
   }

   public static String makeDescriptionId(final String prefix, final @Nullable Identifier location) {
      return location == null ? prefix + ".unregistered_sadface" : prefix + "." + location.getNamespace() + "." + location.getPath().replace('/', '.');
   }

   public static TimeSource.NanoTimeSource timeSource() {
      return INDIRECT_TIME_SOURCE;
   }

   public static void setTimeSource(final TimeSource.NanoTimeSource timeSource) {
      Util.timeSource = timeSource;
   }

   public static void shutdownTimeSource() {
      long currentTime = timeSource.getAsLong();
      timeSource = TimeSource.constant(currentTime);
   }

   public static long getMillis() {
      return getNanos() / 1000000L;
   }

   public static long getNanos() {
      return timeSource.getAsLong();
   }

   public static long getEpochMillis() {
      return Instant.now().toEpochMilli();
   }

   public static long toMillis(final double seconds) {
      return (long)Mth.floor(seconds * 1000.0D);
   }

   public static String getFilenameFormattedDateTime() {
      return FILENAME_DATE_TIME_FORMATTER.format(ZonedDateTime.now());
   }

   private static TracingExecutor makeExecutor(final String name) {
      int threads = maxAllowedExecutorThreads();
      ExecutorService executor;
      if (threads <= 0) {
         executor = MoreExecutors.newDirectExecutorService();
      } else {
         AtomicInteger workerCount = new AtomicInteger(1);
         executor = new ForkJoinPool(threads, (pool) -> {
            final String threadName = "Worker-" + name + "-" + workerCount.getAndIncrement();
            ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
               protected void onStart() {
                  TracyClient.setThreadName(threadName, name.hashCode());
                  super.onStart();
               }

               protected void onTermination(final @Nullable Throwable exception) {
                  if (exception != null) {
                     Util.LOGGER.warn("{} died", this.getName(), exception);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(exception);
               }
            };
            thread.setName(threadName);
            return thread;
         }, Util::onThreadException, true);
      }

      return new TracingExecutor(executor);
   }

   public static int maxAllowedExecutorThreads() {
      return Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());
   }

   private static int getMaxThreads() {
      String maxThreadsString = System.getProperty("max.bg.threads");
      if (maxThreadsString != null) {
         try {
            int maxThreads = Integer.parseInt(maxThreadsString);
            if (maxThreads >= 1 && maxThreads <= 255) {
               return maxThreads;
            }

            LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", maxThreadsString, 255});
         } catch (NumberFormatException var2) {
            LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", maxThreadsString, 255});
         }
      }

      return 255;
   }

   public static TracingExecutor backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static TracingExecutor ioPool() {
      return IO_POOL;
   }

   public static TracingExecutor nonCriticalIoPool() {
      return DOWNLOAD_POOL;
   }

   public static void shutdownExecutors() {
      BACKGROUND_EXECUTOR.shutdownAndAwait(3L, TimeUnit.SECONDS);
      IO_POOL.shutdownAndAwait(3L, TimeUnit.SECONDS);
   }

   private static TracingExecutor makeIoExecutor(final String prefix, final boolean daemon) {
      AtomicInteger workerCount = new AtomicInteger(1);
      return new TracingExecutor(Executors.newCachedThreadPool((runnable) -> {
         String name = prefix + workerCount.getAndIncrement();
         Thread thread = new Thread(runnable, name);
         TracyClient.setThreadName(name, prefix.hashCode());
         thread.setDaemon(daemon);
         thread.setUncaughtExceptionHandler(Util::onThreadException);
         return thread;
      }));
   }

   public static void throwAsRuntime(final Throwable throwable) {
      RuntimeException var10000;
      if (throwable instanceof RuntimeException runtimeException) {
         var10000 = runtimeException;
      } else {
         var10000 = new RuntimeException(throwable);
      }

      throw var10000;
   }

   private static void onThreadException(final Thread thread, Throwable throwable) {
      pauseInIde(throwable);
      if (throwable instanceof CompletionException) {
         throwable = throwable.getCause();
      }

      LOGGER.error("Caught exception in thread {}", thread, throwable);
      CrashReport report;
      if (throwable instanceof ReportedException reportedException) {
         report = reportedException.getReport();
      } else {
         report = CrashReport.forThrowable(throwable, "Exception on worker thread");
      }

      CrashReportCategory threadInfo = report.addCategory("ThreadInfo");
      threadInfo.setDetail("Name", thread.getName());
      BlockableEventLoop.relayDelayCrash(report);
   }

   public static @Nullable Type fetchChoiceType(final DSL.TypeReference reference, final String name) {
      return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : doFetchChoiceType(reference, name);
   }

   private static @Nullable Type doFetchChoiceType(final DSL.TypeReference reference, final String name) {
      Type dataType = null;

      try {
         dataType = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().dataVersion().version())).getChoiceType(reference, name);
      } catch (IllegalArgumentException var4) {
         LOGGER.error("No data fixer registered for {}", name);
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw var4;
         }
      }

      return dataType;
   }

   public static void runNamed(final Runnable runnable, final String name) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         Thread thread = Thread.currentThread();
         String oldName = thread.getName();
         thread.setName(name);

         try {
            Zone ignored = TracyClient.beginZone(name, SharedConstants.IS_RUNNING_IN_IDE);

            try {
               runnable.run();
            } catch (Throwable var16) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var14) {
                     var16.addSuppressed(var14);
                  }
               }

               throw var16;
            }

            if (ignored != null) {
               ignored.close();
            }
         } finally {
            thread.setName(oldName);
         }
      } else {
         Zone ignored = TracyClient.beginZone(name, SharedConstants.IS_RUNNING_IN_IDE);

         try {
            runnable.run();
         } catch (Throwable var15) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var13) {
                  var15.addSuppressed(var13);
               }
            }

            throw var15;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

   }

   public static String getRegisteredName(final Registry registry, final Object entry) {
      Identifier key = registry.getKey(entry);
      return key == null ? "[unregistered]" : key.toString();
   }

   public static Predicate allOf() {
      return (context) -> true;
   }

   public static Predicate allOf(final Predicate condition) {
      return condition;
   }

   public static Predicate allOf(final Predicate condition1, final Predicate condition2) {
      return (context) -> condition1.test(context) && condition2.test(context);
   }

   public static Predicate allOf(final Predicate condition1, final Predicate condition2, final Predicate condition3) {
      return (context) -> condition1.test(context) && condition2.test(context) && condition3.test(context);
   }

   public static Predicate allOf(final Predicate condition1, final Predicate condition2, final Predicate condition3, final Predicate condition4) {
      return (context) -> condition1.test(context) && condition2.test(context) && condition3.test(context) && condition4.test(context);
   }

   public static Predicate allOf(final Predicate condition1, final Predicate condition2, final Predicate condition3, final Predicate condition4, final Predicate condition5) {
      return (context) -> condition1.test(context) && condition2.test(context) && condition3.test(context) && condition4.test(context) && condition5.test(context);
   }

   @SafeVarargs
   public static Predicate allOf(final Predicate... conditions) {
      return (context) -> {
         for(Predicate entry : conditions) {
            if (!entry.test(context)) {
               return false;
            }
         }

         return true;
      };
   }

   public static Predicate allOf(final List conditions) {
      Predicate var10000;
      switch (conditions.size()) {
         case 0:
            var10000 = allOf();
            break;
         case 1:
            var10000 = allOf((Predicate)conditions.get(0));
            break;
         case 2:
            var10000 = allOf((Predicate)conditions.get(0), (Predicate)conditions.get(1));
            break;
         case 3:
            var10000 = allOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2));
            break;
         case 4:
            var10000 = allOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2), (Predicate)conditions.get(3));
            break;
         case 5:
            var10000 = allOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2), (Predicate)conditions.get(3), (Predicate)conditions.get(4));
            break;
         default:
            Predicate[] conditionsCopy = (Predicate[])conditions.toArray((x$0) -> new Predicate[x$0]);
            var10000 = allOf(conditionsCopy);
      }

      return var10000;
   }

   public static Predicate anyOf() {
      return (context) -> false;
   }

   public static Predicate anyOf(final Predicate condition1) {
      return condition1;
   }

   public static Predicate anyOf(final Predicate condition1, final Predicate condition2) {
      return (context) -> condition1.test(context) || condition2.test(context);
   }

   public static Predicate anyOf(final Predicate condition1, final Predicate condition2, final Predicate condition3) {
      return (context) -> condition1.test(context) || condition2.test(context) || condition3.test(context);
   }

   public static Predicate anyOf(final Predicate condition1, final Predicate condition2, final Predicate condition3, final Predicate condition4) {
      return (context) -> condition1.test(context) || condition2.test(context) || condition3.test(context) || condition4.test(context);
   }

   public static Predicate anyOf(final Predicate condition1, final Predicate condition2, final Predicate condition3, final Predicate condition4, final Predicate condition5) {
      return (context) -> condition1.test(context) || condition2.test(context) || condition3.test(context) || condition4.test(context) || condition5.test(context);
   }

   @SafeVarargs
   public static Predicate anyOf(final Predicate... conditions) {
      return (context) -> {
         for(Predicate entry : conditions) {
            if (entry.test(context)) {
               return true;
            }
         }

         return false;
      };
   }

   public static Predicate anyOf(final List conditions) {
      Predicate var10000;
      switch (conditions.size()) {
         case 0:
            var10000 = anyOf();
            break;
         case 1:
            var10000 = anyOf((Predicate)conditions.get(0));
            break;
         case 2:
            var10000 = anyOf((Predicate)conditions.get(0), (Predicate)conditions.get(1));
            break;
         case 3:
            var10000 = anyOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2));
            break;
         case 4:
            var10000 = anyOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2), (Predicate)conditions.get(3));
            break;
         case 5:
            var10000 = anyOf((Predicate)conditions.get(0), (Predicate)conditions.get(1), (Predicate)conditions.get(2), (Predicate)conditions.get(3), (Predicate)conditions.get(4));
            break;
         default:
            Predicate[] conditionsCopy = (Predicate[])conditions.toArray((x$0) -> new Predicate[x$0]);
            var10000 = anyOf(conditionsCopy);
      }

      return var10000;
   }

   public static boolean isSymmetrical(final int width, final int height, final List ingredients) {
      if (width == 1) {
         return true;
      } else {
         int centerX = width / 2;

         for(int y = 0; y < height; ++y) {
            for(int leftX = 0; leftX < centerX; ++leftX) {
               int rightX = width - 1 - leftX;
               Object left = (T)ingredients.get(leftX + y * width);
               Object right = (T)ingredients.get(rightX + y * width);
               if (!left.equals(right)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static int growByHalf(final int currentSize, final int minimalNewSize) {
      return (int)Math.max(Math.min((long)currentSize + (long)(currentSize >> 1), 2147483639L), (long)minimalNewSize);
   }

   @SuppressForbidden(
      reason = "Intentional use of default locale for user-visible date"
   )
   public static DateTimeFormatter localizedDateFormatter(final FormatStyle formatStyle) {
      return DateTimeFormatter.ofLocalizedDateTime(formatStyle);
   }

   public static ThreadInfo[] dumpThreadInfo() {
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      return threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(), threadMXBean.isSynchronizerUsageSupported());
   }

   public static Util.OS getPlatform() {
      String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (osName.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (osName.contains("mac")) {
         return Util.OS.OSX;
      } else if (osName.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (osName.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (osName.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return osName.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static boolean isAarch64() {
      String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
      return arch.equals("aarch64");
   }

   public static boolean isAppleSiliconMac(final String renderer) {
      return renderer.startsWith("Apple");
   }

   public static URI parseAndValidateUntrustedUri(final String uri) throws URISyntaxException {
      URI parsedUri = new URI(uri);
      String scheme = parsedUri.getScheme();
      if (scheme == null) {
         throw new URISyntaxException(uri, "Missing protocol in URI: " + uri);
      } else {
         String protocol = scheme.toLowerCase(Locale.ROOT);
         if (!ALLOWED_UNTRUSTED_LINK_PROTOCOLS.contains(protocol)) {
            throw new URISyntaxException(uri, "Unsupported protocol in URI: " + uri);
         } else {
            return parsedUri;
         }
      }
   }

   public static Object findNextInIterable(final Iterable collection, final @Nullable Object current) {
      Iterator iterator = collection.iterator();
      Object first = (T)iterator.next();
      if (current != null) {
         Object property = first;

         while(property != current) {
            if (iterator.hasNext()) {
               property = (T)iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return first;
   }

   public static Object findPreviousInIterable(final Iterable collection, final @Nullable Object current) {
      Iterator iterator = collection.iterator();

      Object last;
      Object next;
      for(last = (T)null; iterator.hasNext(); last = next) {
         next = (T)iterator.next();
         if (next == current) {
            if (last == null) {
               last = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : current);
            }
            break;
         }
      }

      return last;
   }

   public static Object make(final Supplier factory) {
      return factory.get();
   }

   public static Object make(final Object t, final Consumer consumer) {
      consumer.accept(t);
      return t;
   }

   public static Map makeEnumMap(final Class keyType, final Function function) {
      EnumMap map = new EnumMap(keyType);

      for(Enum key : (Enum[])keyType.getEnumConstants()) {
         map.put(key, function.apply(key));
      }

      return map;
   }

   public static Map mapValues(final Map map, final Function valueMapper) {
      return (Map)map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> valueMapper.apply(e.getValue())));
   }

   public static Map mapValuesLazy(final Map map, final com.google.common.base.Function valueMapper) {
      return Maps.transformValues(map, valueMapper);
   }

   public static Set allOfEnumExcept(final Enum value) {
      return EnumSet.complementOf(EnumSet.of(value));
   }

   public static CompletableFuture sequence(final List futures) {
      if (futures.isEmpty()) {
         return CompletableFuture.completedFuture(List.of());
      } else if (futures.size() == 1) {
         return ((CompletableFuture)futures.getFirst()).thenApply(ObjectLists::singleton);
      } else {
         CompletableFuture all = CompletableFuture.allOf((CompletableFuture[])futures.toArray(new CompletableFuture[0]));
         return all.thenApply((ignored) -> futures.stream().map(CompletableFuture::join).toList());
      }
   }

   public static CompletableFuture sequenceFailFast(final List futures) {
      CompletableFuture failureFuture = new CompletableFuture();
      return fallibleSequence(futures, failureFuture::completeExceptionally).applyToEither(failureFuture, Function.identity());
   }

   public static CompletableFuture sequenceFailFastAndCancel(final List futures) {
      CompletableFuture failureFuture = new CompletableFuture();
      return fallibleSequence(futures, (exception) -> {
         if (failureFuture.completeExceptionally(exception)) {
            for(CompletableFuture future : futures) {
               future.cancel(true);
            }
         }

      }).applyToEither(failureFuture, Function.identity());
   }

   private static CompletableFuture fallibleSequence(final List futures, final Consumer failureHandler) {
      ObjectArrayList results = new ObjectArrayList();
      results.size(futures.size());
      CompletableFuture[] decoratedFutures = new CompletableFuture[futures.size()];

      for(int i = 0; i < futures.size(); ++i) {
         int index = i;
         decoratedFutures[i] = ((CompletableFuture)futures.get(i)).whenComplete((result, exception) -> {
            if (exception != null) {
               failureHandler.accept(exception);
            } else {
               results.set(index, result);
            }

         });
      }

      return CompletableFuture.allOf(decoratedFutures).thenApply((nothing) -> results);
   }

   public static Optional ifElse(final Optional input, final Consumer onTrue, final Runnable onFalse) {
      if (input.isPresent()) {
         onTrue.accept(input.get());
      } else {
         onFalse.run();
      }

      return input;
   }

   public static Supplier name(final Supplier task, final Supplier nameGetter) {
      if (SharedConstants.DEBUG_NAMED_RUNNABLES) {
         final String name = (String)nameGetter.get();
         return new Supplier() {
            public Object get() {
               return task.get();
            }

            public String toString() {
               return name;
            }
         };
      } else {
         return task;
      }
   }

   public static Runnable name(final Runnable task, final Supplier nameGetter) {
      if (SharedConstants.DEBUG_NAMED_RUNNABLES) {
         final String name = (String)nameGetter.get();
         return new Runnable() {
            public void run() {
               task.run();
            }

            public String toString() {
               return name;
            }
         };
      } else {
         return task;
      }
   }

   public static void logAndPauseIfInIde(final String message) {
      LOGGER.error("{}", message);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         doPause(message);
      }

   }

   public static void logAndPauseIfInIde(final String message, final Throwable throwable) {
      LOGGER.error("{}", message, throwable);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         doPause(message);
      }

   }

   public static Throwable pauseInIde(final Throwable t) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
         doPause(t.getMessage());
      }

      return t;
   }

   public static void setPause(final Consumer pauseFunction) {
      thePauser = pauseFunction;
   }

   private static void doPause(final String message) {
      Instant preLog = Instant.now();
      LOGGER.warn("Did you remember to set a breakpoint here?");
      boolean dontBotherWithPause = Duration.between(preLog, Instant.now()).toMillis() > 500L;
      if (!dontBotherWithPause) {
         thePauser.accept(message);
      }

   }

   public static String describeError(final Throwable err) {
      if (err.getCause() != null) {
         return describeError(err.getCause());
      } else {
         return err.getMessage() != null ? err.getMessage() : err.toString();
      }
   }

   public static Object getRandom(final Object[] array, final RandomSource random) {
      return array[random.nextInt(array.length)];
   }

   public static int getRandom(final int[] array, final RandomSource random) {
      return array[random.nextInt(array.length)];
   }

   public static Object getRandom(final List list, final RandomSource random) {
      return list.get(random.nextInt(list.size()));
   }

   public static Optional getRandomSafe(final List list, final RandomSource random) {
      return list.isEmpty() ? Optional.empty() : Optional.of(getRandom(list, random));
   }

   private static BooleanSupplier createRenamer(final Path from, final Path to, final CopyOption... options) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.move(from, to, options);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.error("Failed to rename", var2);
               return false;
            }
         }

         public String toString() {
            return "rename " + String.valueOf(from) + " to " + String.valueOf(to);
         }
      };
   }

   private static BooleanSupplier createDeleter(final Path target) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(target);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.warn("Failed to delete", var2);
               return false;
            }
         }

         public String toString() {
            return "delete old " + String.valueOf(target);
         }
      };
   }

   private static BooleanSupplier createFileDeletedCheck(final Path target) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return !Files.exists(target, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(target) + " is deleted";
         }
      };
   }

   private static BooleanSupplier createFileCreatedCheck(final Path target) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return Files.isRegularFile(target, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(target) + " is present";
         }
      };
   }

   private static boolean executeInSequence(final BooleanSupplier... operations) {
      for(BooleanSupplier operation : operations) {
         if (!operation.getAsBoolean()) {
            LOGGER.warn("Failed to execute {}", operation);
            return false;
         }
      }

      return true;
   }

   private static boolean runWithRetries(final int numberOfRetries, final String description, final BooleanSupplier... operations) {
      for(int retry = 0; retry < numberOfRetries; ++retry) {
         if (executeInSequence(operations)) {
            return true;
         }

         LOGGER.error("Failed to {}, retrying {}/{}", new Object[]{description, retry, numberOfRetries});
      }

      LOGGER.error("Failed to {}, aborting, progress might be lost", description);
      return false;
   }

   public static boolean safeMoveFile(final Path fromPath, final Path toPath, final CopyOption... options) {
      return runWithRetries(10, "move from  " + String.valueOf(fromPath) + " to " + String.valueOf(toPath), createRenamer(fromPath, toPath, options), createFileCreatedCheck(toPath));
   }

   public static void safeReplaceFile(final Path targetPath, final Path newPath, final Path backupPath) {
      safeReplaceOrMoveFile(targetPath, newPath, backupPath, false);
   }

   public static boolean safeReplaceOrMoveFile(final Path targetPath, final Path newPath, final Path backupPath, final boolean noRollback) {
      if (Files.exists(targetPath, new LinkOption[0]) && !runWithRetries(10, "create backup " + String.valueOf(backupPath), createDeleter(backupPath), createRenamer(targetPath, backupPath), createFileCreatedCheck(backupPath))) {
         return false;
      } else if (!runWithRetries(10, "remove old " + String.valueOf(targetPath), createDeleter(targetPath), createFileDeletedCheck(targetPath))) {
         return false;
      } else if (!runWithRetries(10, "replace " + String.valueOf(targetPath) + " with " + String.valueOf(newPath), createRenamer(newPath, targetPath), createFileCreatedCheck(targetPath)) && !noRollback) {
         runWithRetries(10, "restore " + String.valueOf(targetPath) + " from " + String.valueOf(backupPath), createRenamer(backupPath, targetPath), createFileCreatedCheck(targetPath));
         return false;
      } else {
         return true;
      }
   }

   public static int offsetByCodepoints(final String input, int pos, final int offset) {
      int length = input.length();
      if (offset >= 0) {
         for(int i = 0; pos < length && i < offset; ++i) {
            if (Character.isHighSurrogate(input.charAt(pos++)) && pos < length && Character.isLowSurrogate(input.charAt(pos))) {
               ++pos;
            }
         }
      } else {
         for(int i = offset; pos > 0 && i < 0; ++i) {
            --pos;
            if (Character.isLowSurrogate(input.charAt(pos)) && pos > 0 && Character.isHighSurrogate(input.charAt(pos - 1))) {
               --pos;
            }
         }
      }

      return pos;
   }

   public static Consumer prefix(final String prefix, final Consumer consumer) {
      return (s) -> consumer.accept(prefix + s);
   }

   public static DataResult fixedSize(final IntStream stream, final int size) {
      int[] ints = stream.limit((long)(size + 1)).toArray();
      if (ints.length != size) {
         Supplier message = () -> "Input is not a list of " + size + " ints";
         return ints.length >= size ? DataResult.error(message, Arrays.copyOf(ints, size)) : DataResult.error(message);
      } else {
         return DataResult.success(ints);
      }
   }

   public static DataResult fixedSize(final LongStream stream, final int size) {
      long[] longs = stream.limit((long)(size + 1)).toArray();
      if (longs.length != size) {
         Supplier message = () -> "Input is not a list of " + size + " longs";
         return longs.length >= size ? DataResult.error(message, Arrays.copyOf(longs, size)) : DataResult.error(message);
      } else {
         return DataResult.success(longs);
      }
   }

   public static DataResult fixedSize(final List list, final int size) {
      if (list.size() != size) {
         Supplier message = () -> "Input is not a list of " + size + " elements";
         return list.size() >= size ? DataResult.error(message, list.subList(0, size)) : DataResult.error(message);
      } else {
         return DataResult.success(list);
      }
   }

   public static void startTimerHackThread() {
      Thread timerThread = new Thread("Timer hack thread") {
         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
                  Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                  return;
               }
            }
         }
      };
      timerThread.setDaemon(true);
      timerThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      timerThread.start();
   }

   public static void copyBetweenDirs(final Path sourceDir, final Path targetDir, final Path sourcePath) throws IOException {
      Path relative = sourceDir.relativize(sourcePath);
      Path target = targetDir.resolve(relative);
      Files.copy(sourcePath, target);
   }

   public static String sanitizeName(final String value, final CharPredicate isAllowedChar) {
      return (String)value.toLowerCase(Locale.ROOT).chars().mapToObj((c) -> isAllowedChar.test((char)c) ? Character.toString((char)c) : "_").collect(Collectors.joining());
   }

   public static SingleKeyCache singleKeyCache(final Function computeValueFunction) {
      return new SingleKeyCache(computeValueFunction);
   }

   public static Function memoize(final Function function) {
      return new Function() {
         private final Map cache = new ConcurrentHashMap();

         public Object apply(final Object arg) {
            return this.cache.computeIfAbsent(arg, function);
         }

         public String toString() {
            return "memoize/1[function=" + String.valueOf(function) + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static BiFunction memoize(final BiFunction function) {
      return new BiFunction() {
         private final Map cache = new ConcurrentHashMap();

         public Object apply(final Object a, final Object b) {
            return this.cache.computeIfAbsent(Pair.of(a, b), (args) -> function.apply(args.getFirst(), args.getSecond()));
         }

         public String toString() {
            return "memoize/2[function=" + String.valueOf(function) + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static List toShuffledList(final Stream stream, final RandomSource random) {
      ObjectArrayList result = (ObjectArrayList)stream.collect(ObjectArrayList.toList());
      shuffle(result, random);
      return result;
   }

   public static IntArrayList toShuffledList(final IntStream stream, final RandomSource random) {
      IntArrayList result = IntArrayList.wrap(stream.toArray());
      int size = result.size();

      for(int i = size; i > 1; --i) {
         int swapTo = random.nextInt(i);
         result.set(i - 1, result.set(swapTo, result.getInt(i - 1)));
      }

      return result;
   }

   public static List shuffledCopy(final Object[] array, final RandomSource random) {
      ObjectArrayList copy = new ObjectArrayList(array);
      shuffle(copy, random);
      return copy;
   }

   public static List shuffledCopy(final ObjectArrayList list, final RandomSource random) {
      ObjectArrayList copy = new ObjectArrayList(list);
      shuffle(copy, random);
      return copy;
   }

   public static void shuffle(final List list, final RandomSource random) {
      int size = list.size();

      for(int i = size; i > 1; --i) {
         int swapTo = random.nextInt(i);
         list.set(i - 1, list.set(swapTo, list.get(i - 1)));
      }

   }

   public static CompletableFuture blockUntilDone(final Function task) {
      return (CompletableFuture)blockUntilDone(task, CompletableFuture::isDone);
   }

   public static Object blockUntilDone(final Function task, final Predicate completionCheck) {
      BlockingQueue tasks = new LinkedBlockingQueue();
      Object result = (T)task.apply(tasks::add);

      while(!completionCheck.test(result)) {
         try {
            Runnable runnable = (Runnable)tasks.poll(100L, TimeUnit.MILLISECONDS);
            if (runnable != null) {
               runnable.run();
            }
         } catch (InterruptedException var5) {
            LOGGER.warn("Interrupted wait");
            break;
         }
      }

      int remainingSize = tasks.size();
      if (remainingSize > 0) {
         LOGGER.warn("Tasks left in queue: {}", remainingSize);
      }

      return result;
   }

   public static ToIntFunction createIndexLookup(final List values) {
      int size = values.size();
      if (size < 8) {
         return values::indexOf;
      } else {
         Object2IntMap lookup = new Object2IntOpenHashMap(size);
         lookup.defaultReturnValue(-1);

         for(int i = 0; i < size; ++i) {
            lookup.put(values.get(i), i);
         }

         return lookup;
      }
   }

   public static ToIntFunction createIndexIdentityLookup(final List values) {
      int size = values.size();
      if (size < 8) {
         ReferenceList referenceLookup = new ReferenceImmutableList(values);
         return referenceLookup::indexOf;
      } else {
         Reference2IntMap lookup = new Reference2IntOpenHashMap(size);
         lookup.defaultReturnValue(-1);

         for(int i = 0; i < size; ++i) {
            lookup.put(values.get(i), i);
         }

         return lookup;
      }
   }

   public static Typed writeAndReadTypedOrThrow(final Typed typed, final Type newType, final UnaryOperator function) {
      Dynamic dynamic = (Dynamic)typed.write().getOrThrow();
      return readTypedOrThrow(newType, (Dynamic)function.apply(dynamic), true);
   }

   public static Typed readTypedOrThrow(final Type type, final Dynamic dynamic) {
      return readTypedOrThrow(type, dynamic, false);
   }

   public static Typed readTypedOrThrow(final Type type, final Dynamic dynamic, final boolean acceptPartial) {
      DataResult result = type.readTyped(dynamic).map(Pair::getFirst);

      try {
         return acceptPartial ? (Typed)result.getPartialOrThrow(IllegalStateException::new) : (Typed)result.getOrThrow(IllegalStateException::new);
      } catch (IllegalStateException var7) {
         CrashReport report = CrashReport.forThrowable(var7, "Reading type");
         CrashReportCategory category = report.addCategory("Info");
         category.setDetail("Data", dynamic);
         category.setDetail("Type", type);
         throw new ReportedException(report);
      }
   }

   public static List copyAndAdd(final List list, final Object element) {
      return ImmutableList.builderWithExpectedSize(list.size() + 1).addAll(list).add(element).build();
   }

   public static List copyAndAdd(final List list, final Object... elements) {
      return ImmutableList.builderWithExpectedSize(list.size() + elements.length).addAll(list).add(elements).build();
   }

   public static List copyAndAdd(final Object element, final List list) {
      return ImmutableList.builderWithExpectedSize(list.size() + 1).add(element).addAll(list).build();
   }

   public static List join(final Collection first, final Collection second) {
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(first.size() + second.size());
      builder.addAll(first);
      builder.addAll(second);
      return builder.build();
   }

   public static List join(final Collection... collections) {
      int size = 0;

      for(Collection collection : collections) {
         size += collection.size();
      }

      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(size);

      for(Collection collection : collections) {
         builder.addAll(collection);
      }

      return builder.build();
   }

   public static Map copyAndPut(final Map map, final Object key, final Object value) {
      return ImmutableMap.builderWithExpectedSize(map.size() + 1).putAll(map).put(key, value).buildKeepingLast();
   }

   public static enum OS {
      LINUX("linux"),
      SOLARIS("solaris"),
      WINDOWS("windows") {
         protected String[] getOpenUriArguments(final URI uri) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
         }
      },
      OSX("mac") {
         protected String[] getOpenUriArguments(final URI uri) {
            return new String[]{"open", uri.toString()};
         }
      },
      UNKNOWN("unknown");

      private final String telemetryName;

      private OS(final String telemetryName) {
         this.telemetryName = telemetryName;
      }

      public void openUri(final URI uri) {
         try {
            Process process = Runtime.getRuntime().exec(this.getOpenUriArguments(uri));
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException var3) {
            Util.LOGGER.error("Couldn't open location '{}'", uri, var3);
         }

      }

      public void openFile(final File file) {
         this.openUri(file.toURI());
      }

      public void openPath(final Path path) {
         this.openUri(path.toUri());
      }

      protected String[] getOpenUriArguments(final URI uri) {
         String string = uri.toString();
         if ("file".equals(uri.getScheme())) {
            string = string.replace("file:", "file://");
         }

         return new String[]{"xdg-open", string};
      }

      public void openUri(final String uri) {
         try {
            this.openUri(new URI(uri));
         } catch (IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
         }

      }

      public String telemetryName() {
         return this.telemetryName;
      }

      // $FF: synthetic method
      private static Util.OS[] $values() {
         return new Util.OS[]{LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN};
      }
   }
}

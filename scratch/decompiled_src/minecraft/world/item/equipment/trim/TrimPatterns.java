package net.minecraft.world.item.equipment.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

public class TrimPatterns {
   public static final ResourceKey SENTRY = registryKey("sentry");
   public static final ResourceKey DUNE = registryKey("dune");
   public static final ResourceKey COAST = registryKey("coast");
   public static final ResourceKey WILD = registryKey("wild");
   public static final ResourceKey WARD = registryKey("ward");
   public static final ResourceKey EYE = registryKey("eye");
   public static final ResourceKey VEX = registryKey("vex");
   public static final ResourceKey TIDE = registryKey("tide");
   public static final ResourceKey SNOUT = registryKey("snout");
   public static final ResourceKey RIB = registryKey("rib");
   public static final ResourceKey SPIRE = registryKey("spire");
   public static final ResourceKey WAYFINDER = registryKey("wayfinder");
   public static final ResourceKey SHAPER = registryKey("shaper");
   public static final ResourceKey SILENCE = registryKey("silence");
   public static final ResourceKey RAISER = registryKey("raiser");
   public static final ResourceKey HOST = registryKey("host");
   public static final ResourceKey FLOW = registryKey("flow");
   public static final ResourceKey BOLT = registryKey("bolt");

   public static void bootstrap(final BootstrapContext context) {
      register(context, SENTRY);
      register(context, DUNE);
      register(context, COAST);
      register(context, WILD);
      register(context, WARD);
      register(context, EYE);
      register(context, VEX);
      register(context, TIDE);
      register(context, SNOUT);
      register(context, RIB);
      register(context, SPIRE);
      register(context, WAYFINDER);
      register(context, SHAPER);
      register(context, SILENCE);
      register(context, RAISER);
      register(context, HOST);
      register(context, FLOW);
      register(context, BOLT);
   }

   public static void register(final BootstrapContext context, final ResourceKey registryKey) {
      TrimPattern pattern = new TrimPattern(defaultAssetId(registryKey), Component.translatable(Util.makeDescriptionId("trim_pattern", registryKey.identifier())), false);
      context.register(registryKey, pattern);
   }

   private static ResourceKey registryKey(final String id) {
      return ResourceKey.create(Registries.TRIM_PATTERN, Identifier.withDefaultNamespace(id));
   }

   public static Identifier defaultAssetId(final ResourceKey registryKey) {
      return registryKey.identifier();
   }
}

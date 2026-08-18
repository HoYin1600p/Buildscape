package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class DecoratedPotPatterns {
   public static final ResourceKey ANGLER = create("angler");
   public static final ResourceKey ARCHER = create("archer");
   public static final ResourceKey ARMS_UP = create("arms_up");
   public static final ResourceKey BLADE = create("blade");
   public static final ResourceKey BREWER = create("brewer");
   public static final ResourceKey BURN = create("burn");
   public static final ResourceKey DANGER = create("danger");
   public static final ResourceKey EXPLORER = create("explorer");
   public static final ResourceKey FLOW = create("flow");
   public static final ResourceKey FRIEND = create("friend");
   public static final ResourceKey GUSTER = create("guster");
   public static final ResourceKey HEART = create("heart");
   public static final ResourceKey HEARTBREAK = create("heartbreak");
   public static final ResourceKey HOWL = create("howl");
   public static final ResourceKey MINER = create("miner");
   public static final ResourceKey MOURNER = create("mourner");
   public static final ResourceKey PLENTY = create("plenty");
   public static final ResourceKey PRIZE = create("prize");
   public static final ResourceKey SCRAPE = create("scrape");
   public static final ResourceKey SHEAF = create("sheaf");
   public static final ResourceKey SHELTER = create("shelter");
   public static final ResourceKey SKULL = create("skull");
   public static final ResourceKey SNORT = create("snort");
   public static final Codec CODEC = RegistryCodecs.holder(Registries.DECORATED_POT_PATTERN);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DECORATED_POT_PATTERN);

   private static ResourceKey create(final String id) {
      return ResourceKey.create(Registries.DECORATED_POT_PATTERN, Identifier.withDefaultNamespace(id));
   }

   public static void bootstrap(final BootstrapContext registry) {
      registerWithDefaultAsset(registry, ANGLER);
      registerWithDefaultAsset(registry, ARCHER);
      registerWithDefaultAsset(registry, ARMS_UP);
      registerWithDefaultAsset(registry, BLADE);
      registerWithDefaultAsset(registry, BREWER);
      registerWithDefaultAsset(registry, BURN);
      registerWithDefaultAsset(registry, DANGER);
      registerWithDefaultAsset(registry, EXPLORER);
      registerWithDefaultAsset(registry, FLOW);
      registerWithDefaultAsset(registry, FRIEND);
      registerWithDefaultAsset(registry, GUSTER);
      registerWithDefaultAsset(registry, HEART);
      registerWithDefaultAsset(registry, HEARTBREAK);
      registerWithDefaultAsset(registry, HOWL);
      registerWithDefaultAsset(registry, MINER);
      registerWithDefaultAsset(registry, MOURNER);
      registerWithDefaultAsset(registry, PLENTY);
      registerWithDefaultAsset(registry, PRIZE);
      registerWithDefaultAsset(registry, SCRAPE);
      registerWithDefaultAsset(registry, SHEAF);
      registerWithDefaultAsset(registry, SHELTER);
      registerWithDefaultAsset(registry, SKULL);
      registerWithDefaultAsset(registry, SNORT);
   }

   private static void registerWithDefaultAsset(final BootstrapContext registry, final ResourceKey key) {
      registry.register(key, new DecoratedPotPattern(key.identifier().withSuffix("_pottery_pattern")));
   }
}

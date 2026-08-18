package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StatType implements Iterable {
   private final Registry registry;
   private final Map map = new IdentityHashMap();
   private final Component displayName;
   private final StreamCodec streamCodec;

   public StatType(final Registry registry, final Component displayName) {
      this.registry = registry;
      this.displayName = displayName;
      this.streamCodec = ByteBufCodecs.registry(registry.key()).map(this::get, Stat::getValue);
   }

   public StreamCodec streamCodec() {
      return this.streamCodec;
   }

   public boolean contains(final Object key) {
      return this.map.containsKey(key);
   }

   public Stat get(final Object argument, final StatFormatter formatter) {
      return (Stat)this.map.computeIfAbsent(argument, (t) -> new Stat(this, t, formatter));
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public Iterator iterator() {
      return this.map.values().iterator();
   }

   public Stat get(final Object argument) {
      return this.get(argument, StatFormatter.DEFAULT);
   }

   public Component getDisplayName() {
      return this.displayName;
   }
}

package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworkExplosionFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter((f) -> f.shape), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("colors").forGetter((f) -> f.colors), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("fade_colors").forGetter((f) -> f.fadeColors), Codec.BOOL.optionalFieldOf("trail").forGetter((f) -> f.trail), Codec.BOOL.optionalFieldOf("twinkle").forGetter((f) -> f.twinkle))).apply(i, SetFireworkExplosionFunction::new));
   public static final FireworkExplosion DEFAULT_VALUE = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
   private final Optional shape;
   private final Optional colors;
   private final Optional fadeColors;
   private final Optional trail;
   private final Optional twinkle;

   public SetFireworkExplosionFunction(final Optional condition, final Optional shape, final Optional colors, final Optional fadeColors, final Optional hasTrail, final Optional hasTwinkle) {
      super(condition);
      this.shape = shape;
      this.colors = colors;
      this.fadeColors = fadeColors;
      this.trail = hasTrail;
      this.twinkle = hasTwinkle;
   }

   protected ItemStack run(final ItemStack itemStack, final LootContext context) {
      itemStack.update(DataComponents.FIREWORK_EXPLOSION, DEFAULT_VALUE, this::apply);
      return itemStack;
   }

   private FireworkExplosion apply(final FireworkExplosion original) {
      return new FireworkExplosion((FireworkExplosion.Shape)this.shape.orElseGet(original::shape), (IntList)this.colors.orElseGet(original::colors), (IntList)this.fadeColors.orElseGet(original::fadeColors), this.trail.orElseGet(original::hasTrail), this.twinkle.orElseGet(original::hasTwinkle));
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}

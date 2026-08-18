package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

public record ColorCollection(Object white, Object orange, Object magenta, Object lightBlue, Object yellow, Object lime, Object pink, Object gray, Object lightGray, Object cyan, Object purple, Object blue, Object brown, Object green, Object red, Object black) {
   public static final ColorCollection VALUES = new ColorCollection(DyeColor.WHITE, DyeColor.ORANGE, DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.PINK, DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.CYAN, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN, DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK);
   public static final ColorCollection NAMES = VALUES.map(DyeColor::getName);

   public static ColorCollection create(final Object value) {
      return new ColorCollection(value, value, value, value, value, value, value, value, value, value, value, value, value, value, value, value);
   }

   public static ColorCollection registerBlocks(final ColorCollection ids, final TriFunction register, final BiFunction colorBlockFactory, final Function propertiesSupplier) {
      return zipMap(VALUES, ids, (color, id) -> (Block)register.apply(id, (Function)(p) -> (Block)colorBlockFactory.apply(color, p), (BlockBehaviour.Properties)propertiesSupplier.apply(color)));
   }

   public static ColorCollection registerBlockItems(final ColorCollection ids, final ColorCollection blocks, final TriFunction itemFactory) {
      return zipMap(VALUES, ids, (color, id) -> (Item)itemFactory.apply(id, (Block)blocks.pick(color), color));
   }

   public static ColorCollection registerItems(final ColorCollection ids, final BiFunction itemFactory) {
      return zipMap(VALUES, ids, (color, id) -> (Item)itemFactory.apply(id, color));
   }

   public static ColorCollection prefixWithColor(final ColorCollection ids) {
      return zipMap(NAMES, ids, (color, id) -> color + "_" + id);
   }

   public List asList() {
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(16);
      this.forEach(builder::add);
      return builder.build();
   }

   public void forEach(final Consumer consumer) {
      consumer.accept(this.white);
      consumer.accept(this.orange);
      consumer.accept(this.magenta);
      consumer.accept(this.lightBlue);
      consumer.accept(this.yellow);
      consumer.accept(this.lime);
      consumer.accept(this.pink);
      consumer.accept(this.gray);
      consumer.accept(this.lightGray);
      consumer.accept(this.cyan);
      consumer.accept(this.purple);
      consumer.accept(this.blue);
      consumer.accept(this.brown);
      consumer.accept(this.green);
      consumer.accept(this.red);
      consumer.accept(this.black);
   }

   public Object pick(final DyeColor dyeColor) {
      Object var10000;
      switch (dyeColor) {
         case WHITE:
            var10000 = this.white;
            break;
         case ORANGE:
            var10000 = this.orange;
            break;
         case MAGENTA:
            var10000 = this.magenta;
            break;
         case LIGHT_BLUE:
            var10000 = this.lightBlue;
            break;
         case YELLOW:
            var10000 = this.yellow;
            break;
         case LIME:
            var10000 = this.lime;
            break;
         case PINK:
            var10000 = this.pink;
            break;
         case GRAY:
            var10000 = this.gray;
            break;
         case LIGHT_GRAY:
            var10000 = this.lightGray;
            break;
         case CYAN:
            var10000 = this.cyan;
            break;
         case PURPLE:
            var10000 = this.purple;
            break;
         case BLUE:
            var10000 = this.blue;
            break;
         case BROWN:
            var10000 = this.brown;
            break;
         case GREEN:
            var10000 = this.green;
            break;
         case RED:
            var10000 = this.red;
            break;
         case BLACK:
            var10000 = this.black;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ColorCollection map(final Function mapper) {
      return new ColorCollection(mapper.apply(this.white), mapper.apply(this.orange), mapper.apply(this.magenta), mapper.apply(this.lightBlue), mapper.apply(this.yellow), mapper.apply(this.lime), mapper.apply(this.pink), mapper.apply(this.gray), mapper.apply(this.lightGray), mapper.apply(this.cyan), mapper.apply(this.purple), mapper.apply(this.blue), mapper.apply(this.brown), mapper.apply(this.green), mapper.apply(this.red), mapper.apply(this.black));
   }

   public static void zipApply(final ColorCollection first, final ColorCollection second, final BiConsumer consumer) {
      consumer.accept(first.white(), second.white());
      consumer.accept(first.orange(), second.orange());
      consumer.accept(first.magenta(), second.magenta());
      consumer.accept(first.lightBlue(), second.lightBlue());
      consumer.accept(first.yellow(), second.yellow());
      consumer.accept(first.lime(), second.lime());
      consumer.accept(first.pink(), second.pink());
      consumer.accept(first.gray(), second.gray());
      consumer.accept(first.lightGray(), second.lightGray());
      consumer.accept(first.cyan(), second.cyan());
      consumer.accept(first.purple(), second.purple());
      consumer.accept(first.blue(), second.blue());
      consumer.accept(first.brown(), second.brown());
      consumer.accept(first.green(), second.green());
      consumer.accept(first.red(), second.red());
      consumer.accept(first.black(), second.black());
   }

   public static ColorCollection zipMap(final ColorCollection first, final ColorCollection second, final BiFunction operation) {
      return new ColorCollection(operation.apply(first.white(), second.white()), operation.apply(first.orange(), second.orange()), operation.apply(first.magenta(), second.magenta()), operation.apply(first.lightBlue(), second.lightBlue()), operation.apply(first.yellow(), second.yellow()), operation.apply(first.lime(), second.lime()), operation.apply(first.pink(), second.pink()), operation.apply(first.gray(), second.gray()), operation.apply(first.lightGray(), second.lightGray()), operation.apply(first.cyan(), second.cyan()), operation.apply(first.purple(), second.purple()), operation.apply(first.blue(), second.blue()), operation.apply(first.brown(), second.brown()), operation.apply(first.green(), second.green()), operation.apply(first.red(), second.red()), operation.apply(first.black(), second.black()));
   }
}

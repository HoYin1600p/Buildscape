package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.predicates.CollectionPredicate;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WritableBookContent;

public record WritableBookPredicate(Optional pages) implements SingleComponentItemPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(CollectionPredicate.codec(WritableBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(WritableBookPredicate::pages)).apply(i, WritableBookPredicate::new));

   public DataComponentType componentType() {
      return DataComponents.WRITABLE_BOOK_CONTENT;
   }

   public boolean matches(final WritableBookContent value) {
      return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test(value.pages());
   }

   public static record PagePredicate(String contents) implements Predicate {
      public static final Codec CODEC = Codec.STRING.xmap(WritableBookPredicate.PagePredicate::new, WritableBookPredicate.PagePredicate::contents);

      public boolean test(final Filterable value) {
         return ((String)value.raw()).equals(this.contents);
      }
   }
}

package net.minecraft.network.chat.contents.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.util.CompilableString;
import net.minecraft.world.entity.Entity;

public record EntityDataSource(CompilableString selector) implements DataSource {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(EntitySelector.COMPILABLE_CODEC.fieldOf("entity").forGetter(EntityDataSource::selector)).apply(i, EntityDataSource::new));

   public Stream getData(final CommandSourceStack sender) throws CommandSyntaxException {
      List entities = ((EntitySelector)this.selector.compiled()).findEntities(sender);
      return entities.stream().map(NbtPredicate::getEntityTagToCompare);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}

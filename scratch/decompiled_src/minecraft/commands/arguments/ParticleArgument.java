package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class ParticleArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("particle.notFound", value));
   public static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType((message) -> Component.translatableEscape("particle.invalidOptions", message));
   private final HolderLookup.Provider registries;
   private static final TagParser VALUE_PARSER = TagParser.create(NbtOps.INSTANCE);

   public ParticleArgument(final CommandBuildContext context) {
      this.registries = context;
   }

   public static ParticleArgument particle(final CommandBuildContext context) {
      return new ParticleArgument(context);
   }

   public static ParticleOptions getParticle(final CommandContext context, final String name) {
      return (ParticleOptions)context.getArgument(name, ParticleOptions.class);
   }

   public ParticleOptions parse(final StringReader reader) throws CommandSyntaxException {
      return readParticle(reader, this.registries);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(final StringReader reader, final HolderLookup.Provider registries) throws CommandSyntaxException {
      ParticleType type = readParticleType(reader, registries.lookupOrThrow(Registries.PARTICLE_TYPE));
      return readParticle(VALUE_PARSER, reader, type, registries);
   }

   private static ParticleType readParticleType(final StringReader reader, final HolderLookup particles) throws CommandSyntaxException {
      Identifier id = Identifier.read(reader);
      ResourceKey key = ResourceKey.create(Registries.PARTICLE_TYPE, id);
      return (ParticleType)((Holder.Reference)particles.get(key).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext(reader, id))).value();
   }

   private static ParticleOptions readParticle(final TagParser parser, final StringReader reader, final ParticleType type, final HolderLookup.Provider registries) throws CommandSyntaxException {
      RegistryOps ops = registries.createSerializationContext(parser.getOps());
      Object extraData;
      if (reader.canRead() && reader.peek() == '{') {
         extraData = (O)parser.parseAsArgument(reader);
      } else {
         extraData = (O)ops.emptyMap();
      }

      return (ParticleOptions)type.codec().codec().parse(ops, extraData).getOrThrow(ERROR_INVALID_OPTIONS::create);
   }

   public CompletableFuture listSuggestions(final CommandContext context, final SuggestionsBuilder builder) {
      HolderLookup.RegistryLookup particles = this.registries.lookupOrThrow(Registries.PARTICLE_TYPE);
      return SharedSuggestionProvider.suggestResource(particles.listElementIds().map(ResourceKey::identifier), builder);
   }
}

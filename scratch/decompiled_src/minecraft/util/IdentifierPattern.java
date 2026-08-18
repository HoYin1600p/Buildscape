package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IdentifierPattern {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((o) -> o.namespacePattern), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((o) -> o.pathPattern)).apply(i, IdentifierPattern::new));
   private final Optional namespacePattern;
   private final Predicate namespacePredicate;
   private final Optional pathPattern;
   private final Predicate pathPredicate;
   private final Predicate locationPredicate;

   private IdentifierPattern(final Optional namespacePattern, final Optional pathPattern) {
      this.namespacePattern = namespacePattern;
      this.namespacePredicate = (Predicate)namespacePattern.map(Pattern::asPredicate).orElse((Predicate)(r) -> true);
      this.pathPattern = pathPattern;
      this.pathPredicate = (Predicate)pathPattern.map(Pattern::asPredicate).orElse((Predicate)(r) -> true);
      this.locationPredicate = (location) -> this.namespacePredicate.test(location.getNamespace()) && this.pathPredicate.test(location.getPath());
   }

   public Predicate namespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate pathPredicate() {
      return this.pathPredicate;
   }

   public Predicate locationPredicate() {
      return this.locationPredicate;
   }
}

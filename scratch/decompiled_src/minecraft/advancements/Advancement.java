package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.jspecify.annotations.Nullable;

public record Advancement(Optional parent, Optional display, AdvancementRewards rewards, Map criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional name) {
   private static final Codec CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC).validate((criteria) -> criteria.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(criteria));
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent), DisplayInfo.CODEC.optionalFieldOf("display").forGetter(Advancement::display), AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards), CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria), AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter((a) -> Optional.of(a.requirements())), Codec.BOOL.optionalFieldOf("sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)).apply(i, (parent, display, rewards, criteria, requirementsOpt, sendsTelemetryEvent) -> {
         AdvancementRequirements requirements = (AdvancementRequirements)requirementsOpt.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
         return new Advancement(parent, display, rewards, criteria, requirements, sendsTelemetryEvent);
      })).validate(Advancement::validate);
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC.apply(ByteBufCodecs::optional), Advancement::parent, DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional), Advancement::display, AdvancementRequirements.STREAM_CODEC, Advancement::requirements, ByteBufCodecs.BOOL, Advancement::sendsTelemetryEvent, (parent, display, requirements, sendsTelemetryEvent) -> new Advancement(parent, display, AdvancementRewards.EMPTY, Map.of(), requirements, sendsTelemetryEvent));

   public Advancement(final Optional parent, final Optional display, final AdvancementRewards rewards, final Map criteria, final AdvancementRequirements requirements, final boolean sendsTelemetryEvent) {
      this(parent, display, rewards, Map.copyOf(criteria), requirements, sendsTelemetryEvent, display.map(Advancement::decorateName));
   }

   private static DataResult validate(final Advancement advancement) {
      return advancement.requirements().validate(advancement.criteria().keySet()).map((r) -> advancement);
   }

   private static Component decorateName(final DisplayInfo display) {
      Component displayTitle = display.getTitle();
      ChatFormatting color = display.getType().getChatColor();
      Component tooltip = ComponentUtils.mergeStyles(displayTitle.copy(), Style.EMPTY.withColor(color)).append("\n").append(display.getDescription());
      Component title = displayTitle.copy().withStyle((s) -> s.withHoverEvent(new HoverEvent.ShowText(tooltip)));
      return ComponentUtils.wrapInSquareBrackets(title).withStyle(color);
   }

   public static Component name(final AdvancementHolder holder) {
      return (Component)holder.value().name().orElseGet(() -> Component.literal(holder.id().toString()));
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(final ProblemReporter reporter, final HolderGetter.Provider lootData) {
      this.criteria.forEach((name, criterion) -> {
         ValidationContextSource validator = new ValidationContextSource(reporter.forChild(new ProblemReporter.RootFieldPathElement(name)), lootData);
         criterion.triggerInstance().validate(validator);
      });
   }

   public static class Builder {
      private Optional parent = Optional.empty();
      private Optional display = Optional.empty();
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private final ImmutableMap.Builder criteria = ImmutableMap.builder();
      private Optional requirements = Optional.empty();
      private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
      private boolean sendsTelemetryEvent;

      public static Advancement.Builder advancement() {
         return (new Advancement.Builder()).sendsTelemetryEvent();
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(final AdvancementHolder parent) {
         this.parent = Optional.of(parent.id());
         return this;
      }

      /** @deprecated */
      @Deprecated(
         forRemoval = true
      )
      public Advancement.Builder parent(final Identifier parent) {
         this.parent = Optional.of(parent);
         return this;
      }

      public Advancement.Builder display(final ItemStackTemplate icon, final Component title, final Component description, final @Nullable Identifier background, final AdvancementType frame, final boolean showToast, final boolean announceChat, final boolean hidden) {
         return this.display(new DisplayInfo(icon, title, description, Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new), frame, showToast, announceChat, hidden));
      }

      public Advancement.Builder display(final ItemLike icon, final Component title, final Component description, final @Nullable Identifier background, final AdvancementType frame, final boolean showToast, final boolean announceChat, final boolean hidden) {
         return this.display(new DisplayInfo(new ItemStackTemplate(icon.asItem()), title, description, Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new), frame, showToast, announceChat, hidden));
      }

      public Advancement.Builder display(final DisplayInfo display) {
         this.display = Optional.of(display);
         return this;
      }

      public Advancement.Builder rewards(final AdvancementRewards.Builder rewards) {
         return this.rewards(rewards.build());
      }

      public Advancement.Builder rewards(final AdvancementRewards rewards) {
         this.rewards = rewards;
         return this;
      }

      public Advancement.Builder addCriterion(final String name, final Criterion criterion) {
         this.criteria.put(name, criterion);
         return this;
      }

      public Advancement.Builder requirements(final AdvancementRequirements.Strategy strategy) {
         this.requirementsStrategy = strategy;
         return this;
      }

      public Advancement.Builder requirements(final AdvancementRequirements requirements) {
         this.requirements = Optional.of(requirements);
         return this;
      }

      public Advancement.Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(final Identifier id) {
         Map criteria = this.criteria.buildOrThrow();
         AdvancementRequirements requirements = (AdvancementRequirements)this.requirements.orElseGet(() -> this.requirementsStrategy.create(criteria.keySet()));
         return new AdvancementHolder(id, new Advancement(this.parent, this.display, this.rewards, criteria, requirements, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(final BootstrapContext output, final String name) {
         AdvancementHolder advancement = this.build(Identifier.parse(name));
         advancement.register(output);
         return advancement;
      }
   }
}

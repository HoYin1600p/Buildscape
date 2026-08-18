package net.minecraft.client.renderer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map internalTargets, List passes) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(Identifier.CODEC, PostChainConfig.InternalTarget.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostChainConfig::internalTargets), PostChainConfig.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostChainConfig::passes)).apply(i, PostChainConfig::new));

   public sealed interface Input permits PostChainConfig.TargetInput, PostChainConfig.TextureInput {
      Codec CODEC = Codec.xor(PostChainConfig.TextureInput.CODEC, PostChainConfig.TargetInput.CODEC).xmap((either) -> (PostChainConfig.Input)either.map(Function.identity(), Function.identity()), (input) -> {
         Objects.requireNonNull(input);
         int index$1 = 0;
         Either var10000;
         switch (input.typeSwitch<invokedynamic>(input, index$1)) {
            case 0:
               PostChainConfig.TextureInput texture = (PostChainConfig.TextureInput)input;
               var10000 = Either.left(texture);
               break;
            case 1:
               PostChainConfig.TargetInput target = (PostChainConfig.TargetInput)input;
               var10000 = Either.right(target);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      String samplerName();

      Set referencedTargets();
   }

   public static record InternalTarget(Optional width, Optional height, boolean persistent, int clearColor) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(PostChainConfig.InternalTarget::width), ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(PostChainConfig.InternalTarget::height), Codec.BOOL.optionalFieldOf("persistent", false).forGetter(PostChainConfig.InternalTarget::persistent), ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("clear_color", 0).forGetter(PostChainConfig.InternalTarget::clearColor)).apply(i, PostChainConfig.InternalTarget::new));
   }

   public static record Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List inputs, Identifier outputTarget, Map uniforms) {
      private static final Codec INPUTS_CODEC = PostChainConfig.Input.CODEC.listOf().validate((inputs) -> {
         Set samplerName = new ObjectArraySet(inputs.size());

         for(PostChainConfig.Input input : inputs) {
            if (!samplerName.add(input.samplerName())) {
               return DataResult.error(() -> "Encountered repeated sampler name: " + input.samplerName());
            }
         }

         return DataResult.success(inputs);
      });
      private static final Codec UNIFORM_BLOCKS_CODEC = Codec.unboundedMap(Codec.STRING, UniformValue.CODEC.listOf());
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("vertex_shader").forGetter(PostChainConfig.Pass::vertexShaderId), Identifier.CODEC.fieldOf("fragment_shader").forGetter(PostChainConfig.Pass::fragmentShaderId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(PostChainConfig.Pass::inputs), Identifier.CODEC.fieldOf("output").forGetter(PostChainConfig.Pass::outputTarget), UNIFORM_BLOCKS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(PostChainConfig.Pass::uniforms)).apply(i, PostChainConfig.Pass::new));

      public Stream referencedTargets() {
         Stream inputTargets = this.inputs.stream().flatMap((input) -> input.referencedTargets().stream());
         return Stream.concat(inputTargets, Stream.of(this.outputTarget));
      }
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements PostChainConfig.Input {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("sampler_name").forGetter(PostChainConfig.TargetInput::samplerName), Identifier.CODEC.fieldOf("target").forGetter(PostChainConfig.TargetInput::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(PostChainConfig.TargetInput::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(PostChainConfig.TargetInput::bilinear)).apply(i, PostChainConfig.TargetInput::new));

      public Set referencedTargets() {
         return Set.of(this.targetId);
      }
   }

   public static record TextureInput(String samplerName, Identifier location, int width, int height, boolean bilinear) implements PostChainConfig.Input {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("sampler_name").forGetter(PostChainConfig.TextureInput::samplerName), Identifier.CODEC.fieldOf("location").forGetter(PostChainConfig.TextureInput::location), ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(PostChainConfig.TextureInput::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(PostChainConfig.TextureInput::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(PostChainConfig.TextureInput::bilinear)).apply(i, PostChainConfig.TextureInput::new));

      public Set referencedTargets() {
         return Set.of();
      }
   }
}

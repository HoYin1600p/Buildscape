package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.resource.RenderTargetDescriptor.TextureProperties;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.UniformType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class PostChain implements AutoCloseable {
   public static final Identifier MAIN_TARGET_ID = Identifier.withDefaultNamespace("main");
   private final Identifier id;
   private final List passes;
   private final Map internalTargets;
   private final Set externalTargets;
   private final Map persistentTargets = new HashMap();
   private final Projection projection;
   private final ProjectionMatrixBuffer projectionMatrixBuffer;

   private PostChain(final Identifier id, final List passes, final Map internalTargets, final Set externalTargets, final Projection projection, final ProjectionMatrixBuffer projectionMatrixBuffer) {
      this.id = id;
      this.passes = passes;
      this.internalTargets = internalTargets;
      this.externalTargets = externalTargets;
      this.projection = projection;
      this.projectionMatrixBuffer = projectionMatrixBuffer;
   }

   public Identifier id() {
      return this.id;
   }

   public static PostChain load(final PostChainConfig config, final TextureManager textureManager, final Set allowedExternalTargets, final Identifier id, final Projection projection, final ProjectionMatrixBuffer projectionMatrixBuffer) throws ShaderManager.CompilationException {
      Set referencedExternalTargets = getReferencedExternalTargets(config);
      Set invalidExternalTargets = Sets.difference(referencedExternalTargets, allowedExternalTargets);
      if (!invalidExternalTargets.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(invalidExternalTargets));
      } else {
         ImmutableList.Builder passes = ImmutableList.builder();

         for(int i = 0; i < config.passes().size(); ++i) {
            PostChainConfig.Pass pass = (PostChainConfig.Pass)config.passes().get(i);
            passes.add(createPass(textureManager, pass, id.withSuffix("/" + i)));
         }

         return new PostChain(id, passes.build(), config.internalTargets(), referencedExternalTargets, projection, projectionMatrixBuffer);
      }
   }

   public static Set getReferencedExternalTargets(final PostChainConfig config) {
      Stream referencedTargets = config.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
      return (Set)referencedTargets.filter((targetId) -> !config.internalTargets().containsKey(targetId)).collect(Collectors.toSet());
   }

   private static PostPass createPass(final TextureManager textureManager, final PostChainConfig.Pass config, final Identifier id) throws ShaderManager.CompilationException {
      RenderPipeline.Builder pipelineBuilder = RenderPipeline.builder(new RenderPipeline.Snippet[]{RenderPipelines.POST_PROCESSING_SNIPPET}).withFragmentShader(config.fragmentShaderId()).withVertexShader(config.vertexShaderId()).withLocation(id);
      BindGroupLayout.Builder bindGroupLayoutBuilder = BindGroupLayout.builder();

      for(PostChainConfig.Input input : config.inputs()) {
         bindGroupLayoutBuilder.withUniform(input.samplerName() + "Sampler", UniformType.COMBINED_IMAGE_SAMPLER);
      }

      bindGroupLayoutBuilder.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);

      for(String uniformGroupName : config.uniforms().keySet()) {
         bindGroupLayoutBuilder.withUniform(uniformGroupName, UniformType.UNIFORM_BUFFER);
      }

      pipelineBuilder.withBindGroupLayout(bindGroupLayoutBuilder.build());
      pipelineBuilder.withColorTargetState(ColorTargetState.DEFAULT);
      RenderPipeline pipeline = pipelineBuilder.build();
      if (RenderSystem.getCompiledPipelineNullable(pipeline) == null) {
         throw new ShaderManager.CompilationException("Failed to compile post processing pipeline " + String.valueOf(pipeline.getLocation()));
      } else {
         List inputs = new ArrayList();

         label124:
         for(PostChainConfig.Input input : config.inputs()) {
            Objects.requireNonNull(input);
            PostChainConfig.Input var9 = input;
            byte var10 = 0;

            while(true) {
               label120:
               while(true) {
                  Throwable var47;
                  switch (var9.typeSwitch<invokedynamic>(var9, var10)) {
                     case 0:
                        PostChainConfig.TextureInput var11 = (PostChainConfig.TextureInput)var9;
                        PostChainConfig.TextureInput var55 = var11;

                        try {
                           var56 = var55.samplerName();
                        } catch (Throwable var32) {
                           var47 = var32;
                           boolean var68 = false;
                           break;
                        }

                        String var38 = var56;
                        var55 = var11;

                        try {
                           var58 = var55.location();
                        } catch (Throwable var31) {
                           var47 = var31;
                           boolean var69 = false;
                           break;
                        }

                        Identifier var39 = var58;
                        var55 = var11;

                        try {
                           var60 = var55.width();
                        } catch (Throwable var30) {
                           var47 = var30;
                           boolean var70 = false;
                           break;
                        }

                        int var40 = var60;
                        if (true) {
                           var55 = var11;

                           try {
                              var62 = var55.height();
                           } catch (Throwable var29) {
                              var47 = var29;
                              boolean var71 = false;
                              break;
                           }

                           var40 = var62;
                           if (true) {
                              var55 = var11;

                              try {
                                 var64 = var55.bilinear();
                              } catch (Throwable var28) {
                                 var47 = var28;
                                 boolean var72 = false;
                                 break;
                              }

                              var40 = var64;
                              if (true) {
                                 AbstractTexture var43 = textureManager.getTexture(var39.withPath((UnaryOperator)((path) -> "textures/effect/" + path + ".png")));
                                 inputs.add(new PostPass.TextureInput(var38, var43, var40, var40, (boolean)var40));
                                 continue label124;
                              }
                           }
                        }

                        var10 = 1;
                        continue;
                     case 1:
                        PostChainConfig.TargetInput texture = (PostChainConfig.TargetInput)var9;
                        PostChainConfig.TargetInput var10000 = texture;

                        try {
                           var48 = var10000.samplerName();
                        } catch (Throwable var27) {
                           var47 = var27;
                           boolean var10001 = false;
                           break;
                        }

                        String var22 = var48;
                        var10000 = texture;

                        try {
                           var50 = var10000.targetId();
                        } catch (Throwable var26) {
                           var47 = var26;
                           boolean var65 = false;
                           break;
                        }

                        Identifier var44 = var50;
                        var10000 = texture;

                        try {
                           var52 = var10000.useDepthBuffer();
                        } catch (Throwable var25) {
                           var47 = var25;
                           boolean var66 = false;
                           break;
                        }

                        boolean var45 = var52;
                        if (false) {
                           break label120;
                        }

                        var10000 = texture;

                        try {
                           var54 = var10000.bilinear();
                        } catch (Throwable var24) {
                           var47 = var24;
                           boolean var67 = false;
                           break;
                        }

                        var45 = var54;
                        if (true) {
                           inputs.add(new PostPass.TargetInput(var22, var44, var45, var45));
                           continue label124;
                        }
                        break label120;
                     default:
                        throw new MatchException((String)null, (Throwable)null);
                  }

                  Throwable var37 = var47;
                  throw new MatchException(var37.toString(), var37);
               }

               var10 = 2;
            }
         }

         return new PostPass(pipeline, config.outputTarget(), config.uniforms(), inputs);
      }
   }

   public void addToFrame(final FrameGraphBuilder frame, final int screenWidth, final int screenHeight, final PostChain.TargetBundle providedTargets) {
      this.projection.setSize((float)screenWidth, (float)screenHeight);
      GpuBufferSlice projectionBuffer = this.projectionMatrixBuffer.getBuffer(this.projection);
      Map targets = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for(Identifier id : this.externalTargets) {
         targets.put(id, providedTargets.getOrThrow(id));
      }

      for(Map.Entry entry : this.internalTargets.entrySet()) {
         Identifier id = (Identifier)entry.getKey();
         PostChainConfig.InternalTarget target = (PostChainConfig.InternalTarget)entry.getValue();
         RenderTargetDescriptor descriptor = new RenderTargetDescriptor(target.width().orElse(screenWidth), target.height().orElse(screenHeight), new RenderTargetDescriptor.TextureProperties(ARGB.vector4fFromARGB32(target.clearColor()), GpuFormat.RGBA8_UNORM), TextureProperties.DEFAULT_DEPTH);
         if (target.persistent()) {
            RenderTarget persistentTarget = this.getOrCreatePersistentTarget(id, descriptor);
            targets.put(id, frame.importExternal(id.toString(), persistentTarget));
         } else {
            targets.put(id, frame.createInternal(id.toString(), descriptor));
         }
      }

      for(PostPass pass : this.passes) {
         pass.addToFrame(frame, targets, projectionBuffer);
      }

      for(Identifier id : this.externalTargets) {
         providedTargets.replace(id, (ResourceHandle)targets.get(id));
      }

   }

   /** @deprecated */
   @Deprecated
   public void process(final RenderTarget mainTarget, final GraphicsResourceAllocator resourceAllocator) {
      FrameGraphBuilder frame = new FrameGraphBuilder();
      PostChain.TargetBundle targets = PostChain.TargetBundle.of(MAIN_TARGET_ID, frame.importExternal("main", mainTarget));
      this.addToFrame(frame, mainTarget.width, mainTarget.height, targets);
      frame.execute(resourceAllocator);
   }

   private RenderTarget getOrCreatePersistentTarget(final Identifier id, final RenderTargetDescriptor descriptor) {
      RenderTarget target = (RenderTarget)this.persistentTargets.get(id);
      if (target == null || target.width != descriptor.width() || target.height != descriptor.height()) {
         if (target != null) {
            target.destroyBuffers();
         }

         target = descriptor.allocate();
         descriptor.prepare(target);
         this.persistentTargets.put(id, target);
      }

      return target;
   }

   public void close() {
      this.persistentTargets.values().forEach(RenderTarget::destroyBuffers);
      this.persistentTargets.clear();

      for(PostPass pass : this.passes) {
         pass.close();
      }

   }

   public interface TargetBundle {
      static PostChain.TargetBundle of(final Identifier targetId, final ResourceHandle target) {
         return new PostChain.TargetBundle() {
            private ResourceHandle handle = target;

            public void replace(final Identifier id, final ResourceHandle handle) {
               if (id.equals(targetId)) {
                  this.handle = handle;
               } else {
                  throw new IllegalArgumentException("No target with id " + String.valueOf(id));
               }
            }

            public @Nullable ResourceHandle get(final Identifier id) {
               return id.equals(targetId) ? this.handle : null;
            }
         };
      }

      void replace(Identifier id, ResourceHandle handle);

      @Nullable ResourceHandle get(Identifier id);

      default ResourceHandle getOrThrow(final Identifier id) {
         ResourceHandle handle = this.get(id);
         if (handle == null) {
            throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
         } else {
            return handle;
         }
      }
   }
}

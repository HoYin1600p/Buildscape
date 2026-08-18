package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.MemoryStack;

public class PostPass implements AutoCloseable {
   private static final int UBO_SIZE_PER_SAMPLER = (new Std140SizeCalculator()).putVec2().get();
   private final String name;
   private final RenderPipeline pipeline;
   private final Identifier outputTargetId;
   private final Map customUniforms = new HashMap();
   private final MappableRingBuffer infoUbo;
   private final List inputs;

   public PostPass(final RenderPipeline pipeline, final Identifier outputTargetId, final Map uniformGroups, final List inputs) {
      this.pipeline = pipeline;
      this.name = pipeline.getLocation().toString();
      this.outputTargetId = outputTargetId;
      this.inputs = inputs;

      for(Map.Entry uniformGroup : uniformGroups.entrySet()) {
         List uniforms = (List)uniformGroup.getValue();
         if (!uniforms.isEmpty()) {
            Std140SizeCalculator calculator = new Std140SizeCalculator();

            for(UniformValue uniform : uniforms) {
               uniform.addSize(calculator);
            }

            int size = calculator.get();
            MemoryStack stack = MemoryStack.stackPush();

            try {
               Std140Builder builder = Std140Builder.onStack(stack, size);

               for(UniformValue uniform : uniforms) {
                  uniform.writeTo(builder);
               }

               this.customUniforms.put((String)uniformGroup.getKey(), RenderSystem.getDevice().createBuffer(() -> this.name + " / " + (String)uniformGroup.getKey(), 128, builder.get()));
            } catch (Throwable var15) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }
               }

               throw var15;
            }

            if (stack != null) {
               stack.close();
            }
         }
      }

      this.infoUbo = new MappableRingBuffer(() -> this.name + " SamplerInfo", 130, (inputs.size() + 1) * UBO_SIZE_PER_SAMPLER);
   }

   public void addToFrame(final FrameGraphBuilder frame, final Map targets, final GpuBufferSlice shaderOrthoMatrix) {
      FramePass pass = frame.addPass(this.name);

      for(PostPass.Input input : this.inputs) {
         input.addToPass(pass, targets);
      }

      ResourceHandle outputHandle = (ResourceHandle)targets.computeIfPresent(this.outputTargetId, (id, handle) -> pass.readsAndWrites(handle));
      if (outputHandle == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         pass.executes(() -> {
            RenderTarget outputTarget = (RenderTarget)outputHandle.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(shaderOrthoMatrix, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            SamplerCache samplerCache = RenderSystem.getSamplerCache();
            List inputTextures = this.inputs.stream().map((i) -> new PostPass.InputTexture(i.samplerName(), i.texture(targets), samplerCache.getClampToEdge(i.bilinear() ? FilterMode.LINEAR : FilterMode.NEAREST))).toList();
            GpuBufferSlice.MappedView view = this.infoUbo.currentBuffer().map(false, true);

            try {
               Std140Builder builder = Std140Builder.intoBuffer(view.data());
               builder.putVec2((float)outputTarget.width, (float)outputTarget.height);

               for(PostPass.InputTexture input : inputTextures) {
                  builder.putVec2((float)input.view.getWidth(0), (float)input.view.getHeight(0));
               }
            } catch (Throwable var15) {
               if (view != null) {
                  try {
                     view.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (view != null) {
               view.close();
            }

            RenderPass renderPass = commandEncoder.createRenderPass(() -> "Post pass " + this.name, outputTarget.getColorTextureView(), Optional.empty(), outputTarget.hasDepth() ? outputTarget.getDepthTextureView() : null, OptionalDouble.empty());

            try {
               renderPass.setPipeline(RenderSystem.getCompiledPipeline(this.pipeline));
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("SamplerInfo", this.infoUbo.currentBuffer());

               for(Map.Entry entry : this.customUniforms.entrySet()) {
                  renderPass.setUniform((String)entry.getKey(), (GpuBuffer)entry.getValue());
               }

               for(PostPass.InputTexture input : inputTextures) {
                  renderPass.setUniform(input.samplerName() + "Sampler", input.view(), input.sampler());
               }

               renderPass.draw(3, 1, 0, 0);
            } catch (Throwable var14) {
               if (renderPass != null) {
                  try {
                     renderPass.close();
                  } catch (Throwable var12) {
                     var14.addSuppressed(var12);
                  }
               }

               throw var14;
            }

            if (renderPass != null) {
               renderPass.close();
            }

            this.infoUbo.rotate();
            RenderSystem.restoreProjectionMatrix();

            for(PostPass.Input input : this.inputs) {
               input.cleanup(targets);
            }

         });
      }
   }

   public void close() {
      for(GpuBuffer buffer : this.customUniforms.values()) {
         buffer.close();
      }

      this.infoUbo.close();
   }

   public interface Input {
      void addToPass(FramePass pass, Map targets);

      default void cleanup(final Map targets) {
      }

      GpuTextureView texture(final Map targets);

      String samplerName();

      boolean bilinear();
   }

   private static record InputTexture(String samplerName, GpuTextureView view, GpuSampler sampler) {
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements PostPass.Input {
      private ResourceHandle getHandle(final Map targets) {
         ResourceHandle handle = (ResourceHandle)targets.get(this.targetId);
         if (handle == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
         } else {
            return handle;
         }
      }

      public void addToPass(final FramePass pass, final Map targets) {
         pass.reads(this.getHandle(targets));
      }

      public GpuTextureView texture(final Map targets) {
         ResourceHandle handle = this.getHandle(targets);
         RenderTarget target = (RenderTarget)handle.get();
         GpuTextureView textureView = this.depthBuffer ? target.getDepthTextureView() : target.getColorTextureView();
         if (textureView == null) {
            throw new IllegalStateException("Missing " + (this.depthBuffer ? "depth" : "color") + "texture for target " + String.valueOf(this.targetId));
         } else {
            return textureView;
         }
      }
   }

   public static record TextureInput(String samplerName, AbstractTexture texture, int width, int height, boolean bilinear) implements PostPass.Input {
      public void addToPass(final FramePass pass, final Map targets) {
      }

      public GpuTextureView texture(final Map targets) {
         return this.texture.getTextureView();
      }
   }
}

package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class TextFeatureRenderer extends RenderTypeFeatureRenderer {
   public static final FeatureRendererType TYPE = FeatureRendererType.create("Text");

   protected void buildGroup(final FeatureFrameContext context, final List submits) {
      Font font = context.font();
      TextFeatureRenderer.GlyphRenderer glyphRenderer = new TextFeatureRenderer.GlyphRenderer();

      for(TextFeatureRenderer.Submit submit : submits) {
         glyphRenderer.pose.set(submit.pose());
         glyphRenderer.lightCoords = submit.lightCoords();
         glyphRenderer.displayMode = submit.displayMode();
         TextFeatureRenderer.Content var10000 = submit.content();
         Objects.requireNonNull(var10000);
         TextFeatureRenderer.Content var7 = var10000;
         byte var8 = 0;
         switch (var7.typeSwitch<invokedynamic>(var7, var8)) {
            case 0:
               TextFeatureRenderer.Content.Text text = (TextFeatureRenderer.Content.Text)var7;
               renderText(font, glyphRenderer, text);
               break;
            case 1:
               TextFeatureRenderer.Content.StandaloneBackground standaloneBackground = (TextFeatureRenderer.Content.StandaloneBackground)var7;
               glyphRenderer.acceptRenderable(font.prepareBackground(standaloneBackground.x0(), standaloneBackground.y0(), standaloneBackground.x1(), standaloneBackground.y1(), standaloneBackground.color()));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

   }

   private static void renderText(final Font font, final TextFeatureRenderer.GlyphRenderer glyphRenderer, final TextFeatureRenderer.Content.Text content) {
      if (content.outlineColor() == 0) {
         Font.PreparedText text = font.prepareText(content.string(), content.x(), content.y(), content.color(), content.dropShadow(), false, content.backgroundColor());
         text.visit(glyphRenderer);
      } else {
         Font.PreparedText outline = font.prepare8xTextOutline(content.string(), content.x(), content.y(), content.outlineColor());
         Font.PreparedText text = font.prepareText(content.string(), content.x(), content.y(), content.color(), false, false, 0);
         glyphRenderer.displayMode = Font.DisplayMode.NORMAL;
         outline.visit(glyphRenderer);
         glyphRenderer.displayMode = Font.DisplayMode.POLYGON_OFFSET;
         text.visit(glyphRenderer);
      }

   }

   public sealed interface Content {
      public static record StandaloneBackground(float x0, float y0, float x1, float y1, int color) implements TextFeatureRenderer.Content {
      }

      public static record Text(float x, float y, FormattedCharSequence string, boolean dropShadow, int color, int backgroundColor, int outlineColor) implements TextFeatureRenderer.Content {
      }
   }

   private class GlyphRenderer implements Font.GlyphVisitor {
      private final Matrix4f pose;
      private int lightCoords;
      private Font.DisplayMode displayMode;

      private GlyphRenderer() {
         Objects.requireNonNull(TextFeatureRenderer.this);
         super();
         this.pose = new Matrix4f();
         this.lightCoords = 15728880;
         this.displayMode = Font.DisplayMode.NORMAL;
      }

      public void acceptRenderable(final TextRenderable renderable) {
         VertexConsumer builder = TextFeatureRenderer.this.getVertexBuilder(renderable.renderType(this.displayMode));
         renderable.render(this.pose, builder, this.lightCoords, false);
      }
   }

   public static record Submit(Matrix4fc pose, Font.DisplayMode displayMode, int lightCoords, TextFeatureRenderer.Content content) implements TranslucentSubmit {
      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
      }

      public FeatureRendererType featureType() {
         return TextFeatureRenderer.TYPE;
      }
   }
}

package net.minecraft.client.model.geom.builders;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public class PartDefinition {
   private final List cubes;
   private final PartPose partPose;
   private final Map children = Maps.newHashMap();

   PartDefinition(final List cubes, final PartPose partPose) {
      this.cubes = cubes;
      this.partPose = partPose;
   }

   public PartDefinition addOrReplaceChild(final String name, final CubeListBuilder cubes, final PartPose partPose) {
      PartDefinition child = new PartDefinition(cubes.getCubes(), partPose);
      return this.addOrReplaceChild(name, child);
   }

   public PartDefinition addOrReplaceChild(final String name, final PartDefinition child) {
      PartDefinition previous = (PartDefinition)this.children.put(name, child);
      if (previous != null) {
         child.children.putAll(previous.children);
      }

      return child;
   }

   public PartDefinition clearRecursively() {
      for(String name : this.children.keySet()) {
         this.clearChild(name).clearRecursively();
      }

      return this;
   }

   public PartDefinition clearChild(final String name) {
      PartDefinition child = (PartDefinition)this.children.get(name);
      if (child == null) {
         throw new IllegalArgumentException("No child with name: " + name);
      } else {
         return this.addOrReplaceChild(name, CubeListBuilder.create(), child.partPose);
      }
   }

   public void retainPartsAndChildren(final Set parts) {
      for(Map.Entry entry : this.children.entrySet()) {
         PartDefinition child = (PartDefinition)entry.getValue();
         if (!parts.contains(entry.getKey())) {
            this.addOrReplaceChild((String)entry.getKey(), CubeListBuilder.create(), child.partPose).retainPartsAndChildren(parts);
         }
      }

   }

   public void retainExactParts(final Set parts) {
      for(Map.Entry entry : this.children.entrySet()) {
         PartDefinition child = (PartDefinition)entry.getValue();
         if (parts.contains(entry.getKey())) {
            child.clearRecursively();
         } else {
            this.addOrReplaceChild((String)entry.getKey(), CubeListBuilder.create(), child.partPose).retainExactParts(parts);
         }
      }

   }

   public ModelPart bake(final int texScaleX, final int texScaleY) {
      Object2ObjectArrayMap bakedChildren = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> ((PartDefinition)e.getValue()).bake(texScaleX, texScaleY), (a, b) -> a, Object2ObjectArrayMap::new));
      List bakedCubes = this.cubes.stream().map((definition) -> definition.bake(texScaleX, texScaleY)).toList();
      ModelPart result = new ModelPart(bakedCubes, bakedChildren);
      result.setInitialPose(this.partPose);
      result.loadPose(this.partPose);
      return result;
   }

   public PartDefinition getChild(final String name) {
      return (PartDefinition)this.children.get(name);
   }

   public Set getChildren() {
      return this.children.entrySet();
   }

   public PartDefinition transformed(final UnaryOperator function) {
      PartDefinition newPart = new PartDefinition(this.cubes, (PartPose)function.apply(this.partPose));
      newPart.children.putAll(this.children);
      return newPart;
   }
}

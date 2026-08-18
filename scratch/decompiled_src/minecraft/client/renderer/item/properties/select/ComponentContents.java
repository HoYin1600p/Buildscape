package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ComponentContents(DataComponentType componentType) implements SelectItemModelProperty {
   private static final SelectItemModelProperty.Type TYPE = createType();

   private static SelectItemModelProperty.Type createType() {
      Codec rawComponentCodec = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().validate((t) -> t.isTransient() ? DataResult.error(() -> "Component can't be serialized") : DataResult.success(t));
      MapCodec switchCodec = rawComponentCodec.dispatchMap("component", (switchObject) -> ((ComponentContents)switchObject.property()).componentType, (componentType) -> SelectItemModelProperty.Type.createCasesFieldCodec(componentType.codecOrThrow()).xmap((cases) -> new SelectItemModel.UnbakedSwitch(new ComponentContents(componentType), cases), SelectItemModel.UnbakedSwitch::cases));
      return new SelectItemModelProperty.Type(switchCodec);
   }

   public static SelectItemModelProperty.Type castType() {
      return TYPE;
   }

   public @Nullable Object get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      return itemStack.get(this.componentType);
   }

   public SelectItemModelProperty.Type type() {
      return castType();
   }

   public Codec valueCodec() {
      return this.componentType.codecOrThrow();
   }
}

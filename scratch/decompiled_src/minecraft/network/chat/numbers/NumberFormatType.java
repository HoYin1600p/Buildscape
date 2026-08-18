package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

public interface NumberFormatType {
   MapCodec mapCodec();

   StreamCodec streamCodec();
}

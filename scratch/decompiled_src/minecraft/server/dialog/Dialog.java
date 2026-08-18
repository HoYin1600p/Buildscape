package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public interface Dialog {
   Codec WIDTH_CODEC = ExtraCodecs.intRange(1, 1024);
   Codec DIRECT_CODEC = BuiltInRegistries.DIALOG_TYPE.byNameCodec().dispatch(Dialog::codec, (c) -> c);
   Codec CODEC = RegistryCodecs.holder(Registries.DIALOG, DIRECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.DIALOG, DIRECT_CODEC);
   StreamCodec STREAM_CODEC = ByteBufCodecs.holder(Registries.DIALOG, ByteBufCodecs.fromCodecWithRegistriesTrusted(DIRECT_CODEC));
   StreamCodec CONTEXT_FREE_STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(DIRECT_CODEC);

   CommonDialogData common();

   MapCodec codec();

   Optional onCancel();
}

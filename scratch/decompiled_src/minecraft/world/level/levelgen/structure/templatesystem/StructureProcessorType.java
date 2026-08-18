package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;

public interface StructureProcessorType {
   Codec SINGLE_CODEC = BuiltInRegistries.STRUCTURE_PROCESSOR.byNameCodec().dispatch("processor_type", StructureProcessor::codec, (c) -> c);
   Codec LIST_OBJECT_CODEC = SINGLE_CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::list);
   Codec DIRECT_CODEC = Codec.withAlternative(LIST_OBJECT_CODEC.fieldOf("processors").codec(), LIST_OBJECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holder(Registries.PROCESSOR_LIST, DIRECT_CODEC);
}

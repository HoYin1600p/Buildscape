package net.minecraft.commands.functions;

import java.util.List;
import net.minecraft.resources.Identifier;

public interface InstantiatedFunction {
   Identifier id();

   List entries();
}

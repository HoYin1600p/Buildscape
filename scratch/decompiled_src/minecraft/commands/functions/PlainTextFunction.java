package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record PlainTextFunction(Identifier id, List entries) implements CommandFunction, InstantiatedFunction {
   public InstantiatedFunction instantiate(final @Nullable CompoundTag arguments, final CommandDispatcher dispatcher) throws FunctionInstantiationException {
      return this;
   }
}

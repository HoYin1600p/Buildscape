package net.minecraft.network.chat.contents.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;

public interface DataSource {
   Stream getData(final CommandSourceStack sender) throws CommandSyntaxException;

   MapCodec codec();
}

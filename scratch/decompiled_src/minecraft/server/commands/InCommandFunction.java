package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface InCommandFunction {
   Object apply(Object t) throws CommandSyntaxException;
}

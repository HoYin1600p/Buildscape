package com.kingodogo.buildscape.command;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class BuildscapeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        FireworkTestCommand.register(dispatcher);

        LiteralArgumentBuilder<CommandSourceStack> buildscapeCommand = Commands.literal("buildscape")
                .then(Commands.literal("recover")
                        .then(Commands.literal("PillarData")
                                .requires(source -> Services.PLATFORM.hasCommandPermission(source, 2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    MinecraftServer server = source.getServer();

                                    if (server == null || !server.isRunning()) {
                                        Services.PLATFORM.sendCommandFailure(source, "Server is not running");
                                        return 0;
                                    }

                                    PillarIdManager manager = PillarIdManager.get();
                                    if (manager == null) {
                                        Services.PLATFORM.sendCommandFailure(source, "PillarIdManager is not available");
                                        return 0;
                                    }

                                    Services.PLATFORM.sendCommandSuccess(source, "Starting pillar recovery...", true);

                                    server.execute(() -> {
                                        try {
                                            manager.recoverPillarsFromWorld(server, false);
                                            Services.PLATFORM.sendCommandSuccess(source, "Pillar recovery completed. Check console for details.", true);
                                        } catch (Exception e) {
                                            Services.PLATFORM.sendCommandFailure(source, "Error during recovery: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    });

                                    return 1;
                                })))
                .then(Commands.literal("cosmatics")
                        .then(Commands.literal("UNLOCK")
                                .requires(source -> Services.PLATFORM.hasCommandPermission(source, 2))
                                .executes(context -> {
                                    Services.PLATFORM.sendCommandSuccess(context.getSource(), "All cosmetics unlocked for development!", true);
                                    return 1;
                                })))
                .then(Commands.literal("cosmetics")
                        .then(Commands.literal("UNLOCK")
                                .requires(source -> Services.PLATFORM.hasCommandPermission(source, 2))
                                .executes(context -> {
                                    Services.PLATFORM.sendCommandSuccess(context.getSource(), "All cosmetics unlocked for development!", true);
                                    return 1;
                                })));

        dispatcher.register(buildscapeCommand);
    }
}

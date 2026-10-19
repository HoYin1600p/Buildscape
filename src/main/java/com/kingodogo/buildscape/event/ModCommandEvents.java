package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.feature.cosmetic.CosmeticManager;
import com.kingodogo.buildscape.feature.firework.FireworkTestCommand;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.kingodogo.buildscape.BuildScape;

public class ModCommandEvents {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        FireworkTestCommand.register(event.getDispatcher());
        LiteralArgumentBuilder<CommandSourceStack> buildscapeCommand = LiteralArgumentBuilder.<CommandSourceStack>literal(
                        "buildscape")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal(
                                "recover")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal(
                                        "PillarData")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CommandSourceStack source = context
                                            .getSource();
                                    MinecraftServer server = source
                                            .getServer();

                                    if (server == null || !server.isRunning()) {
                                        source.sendFailure(
                                                new TextComponent(
                                                        "Server is not running"));
                                        return 0;
                                    }

                                    PillarIdManager manager = PillarIdManager
                                            .get();

                                    if (manager == null) {
                                        source.sendFailure(
                                                new TextComponent(
                                                        "PillarIdManager is not available"));
                                        return 0;
                                    }

                                    source.sendSuccess(
                                            new TextComponent(
                                                    "Starting pillar recovery..."),
                                            true);

                                    server.execute(() -> {
                                        try {
                                            manager.recoverPillarsFromWorld(
                                                    server, false);
                                            source.sendSuccess(
                                                    new TextComponent(
                                                            "Pillar recovery completed. Check console for details."),
                                                    true);
                                        } catch (Exception e) {
                                            source.sendFailure(
                                                    new TextComponent(
                                                            "Error during recovery: "
                                                                    + e.getMessage()));
                                            BuildScape.LOGGER.error("Error during pillar recovery command", e);
                                        }
                                    });

                                    return 1;
                                })))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("cosmatics")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("UNLOCK")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    CosmeticManager.getInstance().setDevUnlockAll(true);
                                    context.getSource().sendSuccess(new TextComponent("All cosmetics unlocked for development!"), true);
                                    return 1;
                                })
                        )
                );

        event.getDispatcher().register(buildscapeCommand);
    }
}

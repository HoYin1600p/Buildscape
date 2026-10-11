package com.kingodogo.buildscape.command;

import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class FireworkTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> fireworkCmd = Commands.literal("firework")
                .requires(source -> source.hasPermission(2));

        addShapeSubcommands(fireworkCmd, "cake", CustomFireworkShapeRegistry.CAKE_ID, new int[]{0xFFFDD0, 0x8B4513, 0xFF2D55, 0xFFD700});
        addShapeSubcommands(fireworkCmd, "crown", CustomFireworkShapeRegistry.CROWN_ID, new int[]{0xFFD700, 0xFFFF77, 0xFF0044, 0x0066FF, 0x00FFFF});
        addShapeSubcommands(fireworkCmd, "trophy", CustomFireworkShapeRegistry.TROPHY_ID, new int[]{0xFFD700, 0xFFFF88, 0xCC7700, 0x00FFFF});
        addShapeSubcommands(fireworkCmd, "christmas_tree", CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID, new int[]{0x227733, 0xFF3030, 0xFFD700});
        addShapeSubcommands(fireworkCmd, "presents", CustomFireworkShapeRegistry.PRESENTS_ID, new int[]{0xFF2233, 0xFFD700});
        addShapeSubcommands(fireworkCmd, "candy_cane", CustomFireworkShapeRegistry.CANDY_CANE_ID, new int[]{0xFF0033});
        addShapeSubcommands(fireworkCmd, "phoenix", CustomFireworkShapeRegistry.PHOENIX_ID, new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00});
        addShapeSubcommands(fireworkCmd, "snowflake", CustomFireworkShapeRegistry.SNOWFLAKE_ID, new int[]{0xFFFFFF, 0xE0F7FF, 0x5AC8FF});

        dispatcher.getRoot().addChild(
                Commands.literal("buildscape").then(fireworkCmd).build()
        );
    }

    private static void addShapeSubcommands(
            LiteralArgumentBuilder<CommandSourceStack> parent,
            String shapeName,
            byte shapeId,
            int[] defaultColors
    ) {
        parent.then(Commands.literal(shapeName)
                .then(Commands.literal("give").executes(ctx -> giveFireworkRocket(ctx.getSource(), shapeName, shapeId, defaultColors)))
                .then(Commands.literal("spawn").executes(ctx -> spawnFireworkExplosion(ctx.getSource(), shapeName, shapeId, defaultColors)))
                .executes(ctx -> spawnFireworkExplosion(ctx.getSource(), shapeName, shapeId, defaultColors))
        );
    }

    public static ItemStack createCustomFireworkRocket(byte shapeId, int[] colors) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworksTag = stack.getOrCreateTagElement("Fireworks");
        ListTag explosionsList = new ListTag();

        CompoundTag explosionTag = new CompoundTag();
        explosionTag.putByte("Type", shapeId);
        explosionTag.putIntArray("Colors", colors);
        explosionTag.putBoolean("Flicker", true);
        explosionTag.putBoolean("Trail", true);

        explosionsList.add(explosionTag);
        fireworksTag.put("Explosions", explosionsList);
        fireworksTag.putByte("Flight", (byte) 1);
        return stack;
    }

    private static int giveFireworkRocket(CommandSourceStack source, String shapeName, byte shapeId, int[] colors) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            ItemStack stack = createCustomFireworkRocket(shapeId, colors);
            stack.setHoverName(new TextComponent("§6Buildscape §e" + capitalize(shapeName) + " Firework Rocket"));

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            source.sendSuccess(new TextComponent("§aGave 1x §6" + capitalize(shapeName) + " Firework Rocket§a to " + player.getScoreboardName()), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(new TextComponent("§cMust be executed by a player"));
            return 0;
        }
    }

    private static int spawnFireworkExplosion(CommandSourceStack source, String shapeName, byte shapeId, int[] colors) {
        Vec3 pos = source.getPosition();
        ItemStack stack = createCustomFireworkRocket(shapeId, colors);

        FireworkRocketEntity rocket = new FireworkRocketEntity(
                source.getLevel(),
                pos.x, pos.y + 1.5D, pos.z,
                stack
        );
        source.getLevel().addFreshEntity(rocket);
        source.getLevel().broadcastEntityEvent(rocket, (byte) 17);
        rocket.discard();

        source.sendSuccess(new TextComponent("§aSpawned §6" + capitalize(shapeName) + " Firework Explosion§a at " + new BlockPos(pos)), true);
        return 1;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

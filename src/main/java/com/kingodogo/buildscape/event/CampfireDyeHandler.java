package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.network.ActionBarMessagePacket;
import com.kingodogo.buildscape.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class CampfireDyeHandler {

    @SubscribeEvent
    public static void onCampfireInteract(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getPlayer();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!(state.getBlock() instanceof CampfireBlock)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CampfireBlockEntity)) {
            return;
        }

        CompoundTag data = be.getTileData();

        // 1. Dyeing logic
        if (!heldItem.isEmpty()) {
            Map.Entry<String, String> dyeInfo = getDyeColorAndName(heldItem);
            if (dyeInfo != null) {
                if (!level.isClientSide) {
                    data.putString("BuildScapeSmokeColor", dyeInfo.getKey());
                    data.putBoolean("BuildScapeSmokeActive", true); // Auto-enable on dye
                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    TextComponent message = new TextComponent("Smoke color: " + dyeInfo.getValue());
                    message.withStyle(ChatFormatting.GRAY);
                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                new ActionBarMessagePacket(message));
                    }
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
                return;
            }

            // 2. Clear color with Water Bucket (Shift-click)
            if (player.isShiftKeyDown() && heldItem.getItem() == Items.WATER_BUCKET) {
                if (!level.isClientSide) {
                    data.remove("BuildScapeSmokeColor");
                    data.putBoolean("BuildScapeSmokeActive", true);
                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);

                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);

                    TextComponent message = new TextComponent("Smoke color cleared");
                    message.withStyle(ChatFormatting.GRAY);
                    if (player instanceof ServerPlayer) {
                        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                                new ActionBarMessagePacket(message));
                    }
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
                return;
            }
        }

        // 3. Toggle smoke (Empty hand)
        if (heldItem.isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
            if (!level.isClientSide) {
                boolean currentActive = !data.contains("BuildScapeSmokeActive") || data.getBoolean("BuildScapeSmokeActive");
                boolean newActive = !currentActive;
                data.putBoolean("BuildScapeSmokeActive", newActive);
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);

                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.5f, newActive ? 1.2f : 0.8f);

                TextComponent message = new TextComponent(newActive ? "Smoke: On" : "Smoke: Off");
                message.withStyle(newActive ? ChatFormatting.GREEN : ChatFormatting.RED);
                if (player instanceof ServerPlayer) {
                    ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new ActionBarMessagePacket(message));
                }
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            event.setCanceled(true);
        }
    }

    private static Map.Entry<String, String> getDyeColorAndName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        net.minecraft.world.item.Item item = stack.getItem();

        if (item == Items.WHITE_DYE) return Map.entry("#E8FEFD", "White");
        else if (item == Items.ORANGE_DYE) return Map.entry("#FF5C00", "Orange");
        else if (item == Items.MAGENTA_DYE) return Map.entry("#FF00FF", "Magenta");
        else if (item == Items.LIGHT_BLUE_DYE) return Map.entry("#3CDFFF", "Light Blue");
        else if (item == Items.YELLOW_DYE) return Map.entry("#FFFF00", "Yellow");
        else if (item == Items.LIME_DYE) return Map.entry("#BFFE00", "Lime");
        else if (item == Items.PINK_DYE) return Map.entry("#F686B7", "Pink");
        else if (item == Items.GRAY_DYE) return Map.entry("#232526", "Gray");
        else if (item == Items.LIGHT_GRAY_DYE) return Map.entry("#B1B8C5", "Light Gray");
        else if (item == Items.CYAN_DYE) return Map.entry("#00FFFF", "Cyan");
        else if (item == Items.PURPLE_DYE) return Map.entry("#AB87FF", "Purple");
        else if (item == Items.BLUE_DYE) return Map.entry("#1919EA", "Blue");
        else if (item == Items.BROWN_DYE) return Map.entry("#411900", "Brown");
        else if (item == Items.GREEN_DYE) return Map.entry("#39FF14", "Green");
        else if (item == Items.RED_DYE) return Map.entry("#FF0000", "Red");
        else if (item == Items.BLACK_DYE) return Map.entry("#07010C", "Black");

        return null;
    }
}

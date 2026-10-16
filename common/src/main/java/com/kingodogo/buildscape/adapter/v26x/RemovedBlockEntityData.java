package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/** Data-only conversion before vanilla attempts to decode or instantiate the saved type. */
public final class RemovedBlockEntityData {
    public record Migration(CompoundTag tag) {}

    private static final Logger LOGGER = LoggerFactory.getLogger(RemovedBlockEntityData.class);
    private static final Set<String> RETIRED_BLOCK_TARGETS = VanillaReplacementAliases.ALL.stream()
            .filter(VanillaReplacementAliases.Alias::block)
            .map(VanillaReplacementAliases.Alias::target).collect(Collectors.toUnmodifiableSet());
    private static final Set<String> SIGN_TYPES = Set.of(
            "buildscape:bamboo_sign_block_entity", "buildscape:mangrove_sign_block_entity",
            "buildscape:bamboo_hanging_sign_block_entity", "buildscape:mangrove_hanging_sign_block_entity");

    private RemovedBlockEntityData() {}

    /** Null means vanilla can load the original; a null tag means quietly drop this invalid entity. */
    public static Migration prepare(BlockState state, CompoundTag saved) {
        Identifier savedId = Identifier.tryParse(saved.getStringOr("id", ""));
        if (savedId == null || !savedId.getNamespace().equals("buildscape")) return null;
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        // In particular, buildscape:stripped_bamboo_shelf must keep its own shelf type and format.
        BlockEntityType<?> oldType = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(savedId);
        if (!isCandidate(savedId, blockId, oldType != null && oldType.isValid(state))) return null;

        String target = blockId.toString();
        boolean chest = savedId.toString().equals("buildscape:copper_chest")
                && RETIRED_BLOCK_TARGETS.contains(target) && target.endsWith("copper_chest");
        boolean shelf = savedId.toString().equals("buildscape:shelf")
                && RETIRED_BLOCK_TARGETS.contains(target) && target.endsWith("_shelf");
        // Hanging and wall signs share the retired wood family's ordinary sign data.
        String signTarget = target.replace("_wall_hanging_sign", "_sign")
                .replace("_hanging_sign", "_sign").replace("_wall_sign", "_sign");
        boolean sign = SIGN_TYPES.contains(savedId.toString())
                && target.endsWith("_sign") && RETIRED_BLOCK_TARGETS.contains(signTarget);
        if (!chest && !shelf && !sign) return new Migration(null);

        // Select the type accepted by this block: chest, sign, hanging_sign or shelf.
        for (BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            Identifier typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
            if (typeId.getNamespace().equals("minecraft") && type.isValid(state)) {
                CompoundTag converted = shelf ? convertShelf(saved, typeId.toString())
                        : chest ? convertChest(saved, typeId.toString()) : convertSign(saved, typeId.toString());
                return new Migration(converted);
            }
        }
        return new Migration(null);
    }

    static boolean isCandidate(Identifier savedType, Identifier block, boolean validSavedType) {
        return savedType.getNamespace().equals("buildscape")
                && block.getNamespace().equals("minecraft") && !validSavedType;
    }

    public static void logDropped(BlockPos pos, BlockState state, CompoundTag saved) {
        LOGGER.debug("Dropping retired Buildscape block entity {} on {} at {}",
                saved.getStringOr("id", ""), BuiltInRegistries.BLOCK.getKey(state.getBlock()), pos);
    }

    public static CompoundTag convertChest(CompoundTag saved, String vanillaType) {
        // CopperChestBlockEntity extends vanilla ChestBlockEntity without overriding persistence.
        // Copy all fields, including Items, CustomName, lock, LootTable and LootTableSeed.
        return withType(saved, vanillaType);
    }

    public static CompoundTag convertSign(CompoundTag saved, String vanillaType) {
        // Both deleted sign subclasses inherit front_text, back_text and is_waxed unchanged.
        return withType(saved, vanillaType);
    }

    public static CompoundTag convertShelf(CompoundTag saved, String vanillaType) {
        CompoundTag converted = withType(saved, vanillaType);
        ListTag items = new ListTag();
        for (Tag entry : saved.getListOrEmpty("Items")) {
            if (!(entry instanceof CompoundTag child)) continue;
            int slot = child.getIntOr("Slot", -1);
            if (slot < 0 || slot >= 3) continue;
            // ValueOutputData.putItemList stores {Slot: int, Item: ItemStack.CODEC}.
            // ContainerHelper expects ItemStack.MAP_CODEC flattened beside an unsigned-byte Slot.
            CompoundTag stack = child.getCompound("Item").map(CompoundTag::copy).orElseGet(child::copy);
            stack.remove("Item");
            stack.putByte("Slot", (byte) slot);
            items.add(stack);
        }
        converted.put("Items", items);
        // align_items_to_bottom has the same key and meaning in the vanilla shelf.
        return converted;
    }

    private static CompoundTag withType(CompoundTag saved, String vanillaType) {
        CompoundTag converted = saved.copy();
        converted.putString("id", vanillaType);
        return converted;
    }
}

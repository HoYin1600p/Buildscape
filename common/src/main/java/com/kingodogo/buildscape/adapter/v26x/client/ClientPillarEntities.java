package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.client.renderer.MobState;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

/** Display entities are created and mutated only while extracting client render states. */
public final class ClientPillarEntities {
    // Client levels do not allocate IDs in 26.2. Display entities never enter the world,
    // but their caches and render-state extraction still require a unique, nonzero ID.
    private static final java.util.concurrent.atomic.AtomicInteger DISPLAY_IDS =
            new java.util.concurrent.atomic.AtomicInteger(-1);

    private ClientPillarEntities() {}

    static int nextDisplayId() {
        return DISPLAY_IDS.getAndDecrement();
    }

    public static Entity createArmorStand(Level level, double x, double y, double z) {
        Entity[] created = new Entity[1];
        Services.PLATFORM.wrapRegistryAction(() -> created[0] = EntityTypes.ARMOR_STAND.create(level, EntitySpawnReason.LOAD));
        if (created[0] != null) {
            created[0].setId(nextDisplayId());
            created[0].setPos(x, y, z);
        }
        return created[0];
    }

    public static void setupArmorStand(Entity entity) {
        if (!(entity instanceof ArmorStand stand)) return;
        stand.setShowArms(true);
        stand.setNoBasePlate(true);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.noPhysics = true;
    }

    public static void updateArmorStand(Entity entity, ItemStack stack, EquipmentSlot slot, boolean standItem) {
        if (!(entity instanceof ArmorStand stand)) return;
        for (EquipmentSlot equipment : EquipmentSlot.values()) stand.setItemSlot(equipment, ItemStack.EMPTY);
        stand.setInvisible(!standItem);
        stand.setNoBasePlate(!standItem);
        if (!standItem && slot != null) stand.setItemSlot(slot, stack.copy());
    }

    public static boolean isArmor(ItemStack stack) {
        var equipment = stack.get(DataComponents.EQUIPPABLE);
        return equipment != null && equipment.assetId().isPresent()
                && (equipment.slot() == EquipmentSlot.HEAD || equipment.slot() == EquipmentSlot.CHEST
                || equipment.slot() == EquipmentSlot.LEGS || equipment.slot() == EquipmentSlot.FEET);
    }

    public static Entity createMob(ItemStack egg, Level level, BlockPos pos, Object argument) {
        MobState state = (MobState) argument;
        EntityType<?> eggType = SpawnEggItem.getType(egg);
        if (eggType == null) return null;
        EntityType<?> displayType = state.parsedStates.contains("giant")
                && (eggType == EntityTypes.ZOMBIE || eggType == EntityTypes.HUSK || eggType == EntityTypes.DROWNED)
                ? EntityTypes.GIANT : eggType;
        Entity[] created = new Entity[1];
        Services.PLATFORM.wrapRegistryAction(() -> created[0] = displayType.create(level, EntitySpawnReason.LOAD));
        Entity entity = created[0];
        if (entity != null) {
            entity.setId(nextDisplayId());
            applyMobState(entity, state);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 1.125, pos.getZ() + 0.5);
            entity.noPhysics = true;
        }
        return entity;
    }

    public static void applyMobState(Entity entity, Object argument) {
        MobState state = (MobState) argument;
        // Retain unrelated saved fields when applying states, as the reference renderer did.
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.level().registryAccess());
        entity.saveWithoutId(output);
        CompoundTag data = output.buildResult();
        data.putBoolean("powered", state.charged || state.powered);
        data.putBoolean("ignited", state.parsedStates.contains("ignite") || state.parsedStates.contains("ignited"));
        data.putBoolean("ChestedHorse", state.parsedStates.contains("chested"));
        data.putBoolean("Sitting", state.sitting);
        data.putBoolean("Sleeping", state.parsedStates.contains("sleep") || state.parsedStates.contains("sleeping"));
        data.putBoolean("Crouching", state.parsedStates.contains("crouch") || state.parsedStates.contains("crouching"));
        data.putBoolean("HasNectar", state.parsedStates.contains("nectar"));
        data.putBoolean("HasStung", state.parsedStates.contains("stung"));
        data.putBoolean("IsScreamingGoat", state.parsedStates.contains("scream") || state.parsedStates.contains("screaming"));
        boolean horns = !state.parsedStates.contains("no_horns") && !state.parsedStates.contains("nohorns");
        data.putBoolean("HasLeftHorn", horns);
        data.putBoolean("HasRightHorn", horns);
        data.putBoolean("Pumpkin", !state.parsedStates.contains("no_pumpkin"));
        data.putByte("Peek", (byte) (state.parsedStates.contains("open") ? 100 : 0));
        data.putInt("PuffState", state.parsedStates.contains("full") || state.parsedStates.contains("puff")
                ? 2 : state.parsedStates.contains("half") ? 1 : 0);
        ClientPillarVariants.saveVariants(entity, state, data);
        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, entity.level().registryAccess(), data));
        entity.setNoGravity(true);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setInvisible(state.invisible);
        entity.setGlowingTag(state.glowing);
        entity.setRemainingFireTicks(state.fire ? 20 : 0);
        entity.setTicksFrozen(state.frozen ? 140 : 0);
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setBaby(state.baby);
            mob.setLeftHanded(state.parsedStates.contains("lefty") || state.parsedStates.contains("left_handed"));
        }
        if (entity instanceof LivingEntity living) {
            living.hurtTime = state.parsedStates.contains("hurt") || state.parsedStates.contains("damage") ? 10 : 0;
            living.deathTime = 0;
            living.hurtDuration = living.hurtTime;
            living.walkAnimation.stop();
            living.setDeltaMovement(0, 0, 0);
            living.setSpeed(0);
        }
        if (entity instanceof TamableAnimal tameable) {
            tameable.setTame(state.tamed, false);
            tameable.setInSittingPose(state.sitting);
        }
        DyeColor color = dyeColor(state);
        if (entity instanceof net.minecraft.world.entity.animal.sheep.Sheep sheep) {
            sheep.setSheared(state.sheared);
            sheep.setColor(color);
            sheep.setCustomName(state.parsedStates.contains("jeb") || state.parsedStates.contains("rainbow")
                    ? Component.literal("jeb_") : null);
        }
        if (entity instanceof net.minecraft.world.entity.animal.wolf.Wolf wolf) {
            wolf.setPersistentAngerEndTime(state.angry ? entity.level().getGameTime() + 999999 : 0);
            wolf.setIsInterested(state.parsedStates.contains("begging"));
        }
        if (entity instanceof net.minecraft.world.entity.animal.bee.Bee bee)
            bee.setPersistentAngerEndTime(state.angry ? entity.level().getGameTime() + 999999 : 0);
        if (entity instanceof net.minecraft.world.entity.ambient.Bat bat)
            bat.setResting(state.parsedStates.contains("hanging") || state.parsedStates.contains("roosting"));
        if (entity instanceof net.minecraft.world.entity.animal.polarbear.PolarBear bear)
            bear.setStanding(state.parsedStates.contains("standing") || state.parsedStates.contains("rearing"));
        if (entity instanceof net.minecraft.world.entity.monster.spider.Spider spider)
            spider.setClimbing(state.parsedStates.contains("climbing"));
        if (entity instanceof net.minecraft.world.entity.monster.Vex vex)
            vex.setIsCharging(state.parsedStates.contains("charging"));
        if (entity instanceof net.minecraft.world.entity.monster.cubemob.Slime slime) slime.setSize(slimeSize(state), true);
        ClientPillarVariants.applyComponents(entity, state);
    }

    public static DyeColor dyeColor(MobState state) {
        for (DyeColor color : DyeColor.values()) if (state.parsedStates.contains(color.getName())) return color;
        return DyeColor.WHITE;
    }

    public static int slimeSize(MobState state) {
        if (state.parsedStates.contains("tiny")) return 1;
        if (state.parsedStates.contains("medium")) return 3;
        if (state.parsedStates.contains("large")) return 5;
        if (state.parsedStates.contains("huge") || state.parsedStates.contains("giant")) return 9;
        return 2;
    }
}

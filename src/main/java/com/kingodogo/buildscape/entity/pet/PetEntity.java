package com.kingodogo.buildscape.entity.pet;

import com.kingodogo.buildscape.network.PetMenu;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PetEntity extends TamableAnimal {

    // Massive networked state machine for extremely distinct, ridiculous animations
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> ANIM_STATE =
            net.minecraft.network.syncher.SynchedEntityData.defineId(PetEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    private final PetInventory inventory = new PetInventory(10); // Update to 10 to match UI slots!
    public int animTimer = 0;
    public LivingEntity grudgeTarget = null;
    // Ticks remaining before the next fancy animation is allowed.
    public int animCooldownTicks = 0;
    public int ownerStillTicks = 0;
    private int pickupCooldown = 0;
    private int lastAnim = 0;
    public PetEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true); // Allow natural routing
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 9999999.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.28D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 30.0D);
    }

    public PetInventory getInventory() {
        return this.inventory;
    }

    public void saveInventoryToPlayer() {
        if (!this.getPersistentData().getBoolean("IsCosmetic")) return;
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        net.minecraft.nbt.ListTag listTag = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                net.minecraft.nbt.CompoundTag itemTag = new net.minecraft.nbt.CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                listTag.add(itemTag);
            }
        }
        owner.getPersistentData().put("BuildscapePetInv", listTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0); // 0 = Infinite smooth procedural noise
    }

    public PetAnimation getCurrentAnimation() {
        return PetAnimation.byId(this.entityData.get(ANIM_STATE));
    }

    public void setAnimation(PetAnimation anim) {
        this.entityData.set(ANIM_STATE, anim.ordinal());
    }

    // Helper Presets as requested
    public boolean isPlayingDead() {
        return getCurrentAnimation() == PetAnimation.PLAY_DEAD;
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Vengeance Protocol Goal — fast pursuit
        this.goalSelector.addGoal(1, new PetRetaliateGoal(this, 1.25D, false));

        // Follow owner at base speed multipliers (1.0D), as we dynamically shift base speed itself
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 3.0F, 6.0F, false));

        // During animation cooldown the pet just wanders around the owner calmly
        this.goalSelector.addGoal(3, new PetCooldownWanderGoal(this, 0.8D));

        // Fancy behavioral animations — only fire when cooldown == 0
        this.goalSelector.addGoal(4, new PetBoredGoal(this));
        this.goalSelector.addGoal(5, new PetAnnoyOwnerGoal(this));
        this.goalSelector.addGoal(6, new PetExamineGoal(this));

        // General wander is very calm (0.7 * 0.28 = slow amble)
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // ─── Client-side random emotion particles ────────────────────────────
        if (this.level.isClientSide) {
            PetAnimation state = this.getCurrentAnimation();
            if (this.random.nextInt(300) == 0 && state == PetAnimation.IDLE) {
                this.level.addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + 1.0D, this.getZ(), 0, 0, 0);
            }
            return;
        }

        // ─── Server-side AI & State Machine ──────────────────────────────────
        if (this.isOrderedToSit()) {
            if (this.getCurrentAnimation() != PetAnimation.IDLE) this.setAnimation(PetAnimation.IDLE);
            return;
        }

        if (!this.level.isClientSide) {
            // Decrement animation cooldown
            if (animCooldownTicks > 0) animCooldownTicks--;

            // Handle Dynamic Movement Speeds
            LivingEntity owner = this.getOwner();
            if (owner != null) {
                // ── 2-Speed Gear System (Walk vs Run) ──
                double distToOwnerSqr = this.distanceToSqr(owner);
                if (distToOwnerSqr > 36.0D) {
                    // Far away? Shift into Sprint gear.
                    this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.45D);
                } else {
                    // Close? Shift into calm Walk gear.
                    this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.28D);
                }
            } else {
                ownerStillTicks = 0;
            }
            // Interactive GUI Animations are controlled externally, keep their timers raw!
            PetAnimation currentAnim = this.getCurrentAnimation();
            boolean isInteractive = currentAnim == PetAnimation.PROJECT_HOLOGRAM || currentAnim == PetAnimation.SWAP_MAINHAND || currentAnim == PetAnimation.SWAP_OFFHAND || currentAnim == PetAnimation.SLAP;

            if (isInteractive) {
                if (this.animTimer > 0) this.animTimer--;
                else this.setAnimation(PetAnimation.IDLE);
            } else {
                // Determine whether pet is walking naturally or standing by checking literal motion delta
                double speed = this.getDeltaMovement().horizontalDistanceSqr();
                if (currentAnim == PetAnimation.IDLE || currentAnim == PetAnimation.WALK || currentAnim == PetAnimation.SPRINT) {
                    if (speed > 0.01D) {
                        if (speed > 0.05D) this.setAnimation(PetAnimation.SPRINT);
                        else this.setAnimation(PetAnimation.WALK);
                    } else {
                        this.setAnimation(PetAnimation.IDLE);
                    }
                    currentAnim = this.getCurrentAnimation(); // Refresh for downstream logic
                }
            }

            // Distant Catchup Override (Forces WAVE animation if falling behind)
            // `owner` was already fetched above for still-tracking — reuse it here.
            if (owner != null && !isInteractive) {
                double distSqr = this.distanceToSqr(owner);
                if (distSqr > 64.0D && currentAnim == PetAnimation.IDLE && this.random.nextInt(30) == 0) {
                    this.setAnimation(PetAnimation.WAVE); // Try to get owner's attention!
                    this.animTimer = 30; // 1.5 seconds of waving
                }

                if (currentAnim == PetAnimation.WAVE) {
                    if (this.animTimer > 0) this.animTimer--;
                    else this.setAnimation(PetAnimation.IDLE);
                }

                // Automatic Distant Catchup & Dimension Follow
                if (distSqr > 144.0D) {
                    this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(), owner.getXRot());
                }

                // Handle Cosmetic Inventory & Clone Prevention
                if (this.getPersistentData().getBoolean("IsCosmetic")) {
                    net.minecraft.nbt.CompoundTag playerData = owner.getPersistentData();
                    java.util.UUID activePet = playerData.hasUUID("ActiveCosmeticPet") ? playerData.getUUID("ActiveCosmeticPet") : null;

                    // If the player unequipped the item (activePet == null) or we are an old clone, destroy ourselves!
                    if (activePet == null || !activePet.equals(this.getUUID())) {
                        this.discard();
                        return;
                    }

                    // If pet is empty, try loading from player
                    if (this.tickCount % 20 == 0 && this.inventory.isEmpty() && playerData.contains("BuildscapePetInv")) {
                        net.minecraft.nbt.ListTag listTag = playerData.getList("BuildscapePetInv", 10);
                        for (int i = 0; i < listTag.size(); i++) {
                            net.minecraft.nbt.CompoundTag itemTag = listTag.getCompound(i);
                            int slot = itemTag.getByte("Slot") & 255;
                            if (slot >= 0 && slot < this.inventory.getContainerSize()) {
                                this.inventory.setItem(slot, net.minecraft.world.item.ItemStack.of(itemTag));
                            }
                        }
                    }

                    // Removed the continuous per-tick loop check! NBT syncing is now fully event-driven natively.
                }
            }
        }

        // Item Pickup Logic
        if (pickupCooldown > 0) {
            pickupCooldown--;
        } else {
            List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.5D));
            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isAlive() || itemEntity.hasPickUpDelay()) continue;
                ItemStack stack = itemEntity.getItem();
                ItemStack backup = stack.copy();
                ItemStack remainder = this.inventory.addItem(stack);

                if (remainder.isEmpty()) {
                    itemEntity.discard();
                    this.saveInventoryToPlayer(); // Event triggered Save!
                } else if (remainder.getCount() != backup.getCount()) {
                    itemEntity.setItem(remainder);
                    this.saveInventoryToPlayer(); // Event triggered Save!
                } else {
                    continue; // Skip items that cannot fit without sending wasteful network sync packets!
                }

                this.playSound(net.minecraft.sounds.SoundEvents.ITEM_PICKUP, 0.4F, 1.5F);
                pickupCooldown = 10;
                break; // One item per tick
            }
        }

        // ── Sync equipment slots to client only when actually changed ───────────
        // This triggers Minecraft's internal dirty-flag → ClientboundSetEquipmentPacket.
        // Calling setItemSlot every tick with the same item causes no packet (vanilla optimises it),
        // but comparing first avoids any unnecessary object allocation on steady state.
        ItemStack newMain = this.inventory.getItem(9).copy();
        if (!ItemStack.matches(newMain, this.getMainHandItem())) {
            this.setItemSlot(EquipmentSlot.MAINHAND, newMain);
        }

        // Slot 8 drives the offhand display
        ItemStack newOff = this.inventory.getItem(8).copy();
        if (!ItemStack.matches(newOff, this.getOffhandItem())) {
            this.setItemSlot(EquipmentSlot.OFFHAND, newOff);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.equals(this.getOwner())) {
            // Set a grudge but don't take damage from the owner's direct melee hits
            this.grudgeTarget = player;
            return false;
        }

        boolean result = super.hurt(source, amount);
        // Pet is immortal — floor health at 1 HP so it plays the damage flash
        // but never actually dies. We call setHealth afterwards so the red flash
        // and knockback still trigger normally via super.hurt().
        if (this.getHealth() < 1.0F) {
            this.setHealth(1.0F);
        }
        return result;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level.isClientSide) return InteractionResult.SUCCESS;

        if (!this.isTame()) {
            this.tame(player);
            if (this.level instanceof ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                        this.getX(), this.getY() + 0.8D, this.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
            }
            return InteractionResult.SUCCESS;
        }

        // Sneak-right-click to open Custom Holographic Pet Menu
        if (player.isSecondaryUseActive()) {
            player.openMenu(new SimpleMenuProvider(
                    (id, playerInv, pl) -> new PetMenu(id, playerInv, this.inventory, this),
                    new TextComponent("Pet Inventory")
            ));
            return InteractionResult.SUCCESS;
        }

        // Standard right click toggles sitting
        this.setOrderedToSit(!this.isOrderedToSit());
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);

        if (this.isOrderedToSit()) {
            this.playSound(net.minecraft.sounds.SoundEvents.VILLAGER_YES, 1.0F, 1.8F);
        } else {
            if (this.level instanceof ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + 0.8D, this.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        net.minecraft.nbt.ListTag listTag = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                net.minecraft.nbt.CompoundTag itemTag = new net.minecraft.nbt.CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                if (stack.getCount() > 127) {
                    itemTag.putInt("LargeCount", stack.getCount());
                }
                listTag.add(itemTag);
            }
        }
        compound.put("PetInventory", listTag);
        compound.putInt("PetAnimTimer", this.animTimer);
        compound.putInt("PetLastAnim", this.lastAnim);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("PetInventory", 9)) {
            net.minecraft.nbt.ListTag listTag = compound.getList("PetInventory", 10);
            for (int i = 0; i < listTag.size(); i++) {
                net.minecraft.nbt.CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < this.inventory.getContainerSize()) {
                    ItemStack stack = ItemStack.of(itemTag);
                    if (itemTag.contains("LargeCount")) {
                        stack.setCount(itemTag.getInt("LargeCount"));
                    }
                    this.inventory.setItem(slot, stack);
                }
            }
        }
        this.animTimer = compound.getInt("PetAnimTimer");
        this.lastAnim = compound.getInt("PetLastAnim");
    }

    // Standard 9-slot chest row with custom 256 stack size!
    public static class PetInventory extends SimpleContainer {
        public PetInventory(int size) {
            super(size);
        }

        @Override
        public int getMaxStackSize() {
            return 256;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Contextual AI Behavior Tree Goals
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Active during animCooldownTicks > 0.
     * Makes the pet wander in random circles near its owner instead of
     * immediately launching another fancy animation.
     */
    public static class PetCooldownWanderGoal extends Goal {

        private final PetEntity pet;
        private final double speedModifier;
        private int nextMoveTimer = 0;

        public PetCooldownWanderGoal(PetEntity pet, double speedModifier) {
            this.pet = pet;
            this.speedModifier = speedModifier;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return pet.animCooldownTicks > 0 && !pet.isOrderedToSit() && pet.getOwner() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return pet.animCooldownTicks > 0 && pet.getOwner() != null;
        }

        @Override
        public void tick() {
            net.minecraft.world.entity.LivingEntity owner = pet.getOwner();
            if (owner == null) return;

            nextMoveTimer--;
            if (nextMoveTimer <= 0 || pet.getNavigation().isDone()) {
                nextMoveTimer = 25 + pet.getRandom().nextInt(35);
                // Pick a random spot orbiting the owner (2–4 blocks away)
                double angle = pet.getRandom().nextDouble() * 2.0D * Math.PI;
                double radius = 2.0D + pet.getRandom().nextDouble() * 2.0D;
                double tx = owner.getX() + Math.cos(angle) * radius;
                double tz = owner.getZ() + Math.sin(angle) * radius;
                pet.getNavigation().moveTo(tx, owner.getY(), tz, this.speedModifier);
            }
        }

        @Override
        public void stop() {
            pet.getNavigation().stop();
        }
    }

    public static class PetBoredGoal extends Goal {
        private final PetEntity pet;
        private final PetAnimation[] boredAnims = {
                PetAnimation.PLAY_DEAD, PetAnimation.SLEEPING, PetAnimation.SIT,
                PetAnimation.CRAWL, PetAnimation.BOW,
                PetAnimation.THINK, PetAnimation.SPIN, PetAnimation.POINT_UP
        };
        private int timer;

        public PetBoredGoal(PetEntity pet) {
            this.pet = pet;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); // Mutex lock so walking safely overrides it!
        }

        @Override
        public boolean canUse() {
            return pet.animCooldownTicks <= 0 &&
                    pet.getCurrentAnimation() == PetAnimation.IDLE &&
                    pet.getDeltaMovement().horizontalDistanceSqr() < 0.001D &&
                    pet.getRandom().nextInt(150) == 0;
        }

        @Override
        public void start() {
            this.timer = 60 + pet.getRandom().nextInt(60);
            pet.setAnimation(boredAnims[pet.getRandom().nextInt(boredAnims.length)]);
            pet.getNavigation().stop();
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer > 0 && pet.getDeltaMovement().horizontalDistanceSqr() < 0.005D;
        }

        @Override
        public void tick() {
            this.timer--;
            // Strictly root it in place while bored
            pet.setDeltaMovement(pet.getDeltaMovement().multiply(0, 1, 0));
        }

        @Override
        public void stop() {
            pet.setAnimation(PetAnimation.IDLE);
            // Random cooldown: 5–15 seconds before next fancy animation
            pet.animCooldownTicks = 100 + pet.getRandom().nextInt(200);
        }
    }

    public static class PetAnnoyOwnerGoal extends Goal {
        private final PetEntity pet;
        private final PetAnimation[] annoyingAnims = {
                PetAnimation.BACKFLIP, PetAnimation.JUMPING, PetAnimation.HEAD_BANG,
                PetAnimation.WAVE, PetAnimation.T_POSE,
                PetAnimation.FLEX, PetAnimation.DANCE
        };
        private int timer;

        public PetAnnoyOwnerGoal(PetEntity pet) {
            this.pet = pet;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return pet.animCooldownTicks <= 0 &&
                    pet.getCurrentAnimation() == PetAnimation.IDLE &&
                    pet.getRandom().nextInt(200) == 0 &&
                    pet.getOwner() != null &&
                    pet.distanceToSqr(pet.getOwner()) < 25.0D;
        }

        @Override
        public void start() {
            this.timer = 40 + pet.getRandom().nextInt(50);
            pet.setAnimation(annoyingAnims[pet.getRandom().nextInt(annoyingAnims.length)]);
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer > 0;
        }

        @Override
        public void tick() {
            this.timer--;
        }

        @Override
        public void stop() {
            pet.setAnimation(PetAnimation.IDLE);
            // Random cooldown: 5–15 seconds before next fancy animation
            pet.animCooldownTicks = 100 + pet.getRandom().nextInt(200);
        }
    }

    public static class PetExamineGoal extends Goal {
        private final PetEntity pet;
        private final PetAnimation[] examineAnims = {
                PetAnimation.EXAMINE_FLOOR, PetAnimation.STARGAZE,
                PetAnimation.NOD, PetAnimation.SHAKE_HEAD
        };
        private int timer;

        public PetExamineGoal(PetEntity pet) {
            this.pet = pet;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return pet.animCooldownTicks <= 0 &&
                    pet.getCurrentAnimation() == PetAnimation.IDLE &&
                    pet.getRandom().nextInt(100) == 0;
        }

        @Override
        public void start() {
            this.timer = 30 + pet.getRandom().nextInt(40);
            pet.setAnimation(examineAnims[pet.getRandom().nextInt(examineAnims.length)]);
            pet.getNavigation().stop();
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer > 0 && pet.getDeltaMovement().horizontalDistanceSqr() < 0.005D;
        }

        @Override
        public void tick() {
            this.timer--;
            pet.setDeltaMovement(pet.getDeltaMovement().multiply(0, 1, 0));
        }

        @Override
        public void stop() {
            pet.setAnimation(PetAnimation.IDLE);
            // Short cooldown after head-gesture animations too
            pet.animCooldownTicks = 60 + pet.getRandom().nextInt(120);
        }
    }

    public static class PetRetaliateGoal extends Goal {
        private final PetEntity pet;

        public PetRetaliateGoal(PetEntity pet, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            this.pet = pet;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return pet.grudgeTarget != null && pet.grudgeTarget.isAlive();
        }

        @Override
        public void start() {
            pet.getNavigation().moveTo(pet.grudgeTarget, 1.4D);
        }

        @Override
        public void tick() {
            if (pet.grudgeTarget == null) return;
            pet.getLookControl().setLookAt(pet.grudgeTarget, 30.0F, 30.0F);

            if (pet.getNavigation().isDone()) {
                pet.getNavigation().moveTo(pet.grudgeTarget, 1.4D);
            }

            double distToTargetSqr = pet.distanceToSqr(pet.grudgeTarget);
            if (distToTargetSqr <= 4.0D) { // Close enough to slap!
                // Strike for 1.0 damage (0.5 hearts) via thorns source (bypasses tamer-check)
                boolean hit = pet.grudgeTarget.hurt(DamageSource.thorns(pet), 1.0F);
                if (!hit) {
                    // Force-subtract health if the hurt() call was blocked (e.g. invulnerability window)
                    float hp = pet.grudgeTarget.getHealth() - 1.0F;
                    pet.grudgeTarget.setHealth(Math.max(hp, 0.5F));
                }

                // Visuals
                pet.playSound(net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 2.0F);
                pet.setAnimation(PetAnimation.SLAP);
                pet.animTimer = 15;

                // Forgive the target immediately after one clean hit
                pet.grudgeTarget = null;
            }
        }
    }
}

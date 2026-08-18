package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public interface DamageTypeTags {
   TagKey DAMAGES_HELMET = create("damages_helmet");
   TagKey BYPASSES_ARMOR = create("bypasses_armor");
   TagKey BYPASSES_SHIELD = create("bypasses_shield");
   TagKey BYPASSES_INVULNERABILITY = create("bypasses_invulnerability");
   TagKey BYPASSES_COOLDOWN = create("bypasses_cooldown");
   TagKey BYPASSES_EFFECTS = create("bypasses_effects");
   TagKey BYPASSES_RESISTANCE = create("bypasses_resistance");
   TagKey BYPASSES_ENCHANTMENTS = create("bypasses_enchantments");
   TagKey IS_FIRE = create("is_fire");
   TagKey IS_PROJECTILE = create("is_projectile");
   TagKey WITCH_RESISTANT_TO = create("witch_resistant_to");
   TagKey IS_EXPLOSION = create("is_explosion");
   TagKey IS_FALL = create("is_fall");
   TagKey IS_DROWNING = create("is_drowning");
   TagKey IS_FREEZING = create("is_freezing");
   TagKey IS_LIGHTNING = create("is_lightning");
   TagKey NO_ANGER = create("no_anger");
   TagKey NO_WOLF_RETALIATION = create("no_wolf_retaliation");
   TagKey NO_IMPACT = create("no_impact");
   TagKey ALWAYS_MOST_SIGNIFICANT_FALL = create("always_most_significant_fall");
   TagKey WITHER_IMMUNE_TO = create("wither_immune_to");
   TagKey SULFUR_CUBE_WITH_BLOCK_IMMUNE_TO = create("sulfur_cube_with_block_immune_to");
   TagKey IGNITES_ARMOR_STANDS = create("ignites_armor_stands");
   TagKey BURNS_ARMOR_STANDS = create("burns_armor_stands");
   TagKey AVOIDS_GUARDIAN_THORNS = create("avoids_guardian_thorns");
   TagKey ALWAYS_TRIGGERS_SILVERFISH = create("always_triggers_silverfish");
   TagKey ALWAYS_HURTS_ENDER_DRAGONS = create("always_hurts_ender_dragons");
   TagKey NO_KNOCKBACK = create("no_knockback");
   TagKey ALWAYS_KILLS_ARMOR_STANDS = create("always_kills_armor_stands");
   TagKey CAN_BREAK_ARMOR_STAND = create("can_break_armor_stand");
   TagKey BYPASSES_WOLF_ARMOR = create("bypasses_wolf_armor");
   TagKey IS_PLAYER_ATTACK = create("is_player_attack");
   TagKey BURN_FROM_STEPPING = create("burn_from_stepping");
   TagKey PANIC_CAUSES = create("panic_causes");
   TagKey PANIC_ENVIRONMENTAL_CAUSES = create("panic_environmental_causes");
   TagKey IS_MACE_SMASH = create("mace_smash");

   private static TagKey create(final String name) {
      return TagKey.create(Registries.DAMAGE_TYPE, Identifier.withDefaultNamespace(name));
   }
}

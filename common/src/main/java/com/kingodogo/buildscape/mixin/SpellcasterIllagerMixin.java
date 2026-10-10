package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.DisplaySpellcaster;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Lets client display code put an illager in its spell-casting pose. SpellcasterIllager.IllagerSpell is
 * protected, so this sets the synched spell id (NONE = 0, SUMMON_VEX = 1) through the data accessor instead.
 */
@Mixin(SpellcasterIllager.class)
public abstract class SpellcasterIllagerMixin implements DisplaySpellcaster {
    @Accessor("DATA_SPELL_CASTING_ID")
    private static EntityDataAccessor<Byte> buildscape$spellCastingId() {
        throw new AssertionError();
    }

    @Override
    public void buildscape$setDisplayCasting(boolean casting) {
        ((SpellcasterIllager) (Object) this).getEntityData().set(buildscape$spellCastingId(), (byte) (casting ? 1 : 0));
    }
}

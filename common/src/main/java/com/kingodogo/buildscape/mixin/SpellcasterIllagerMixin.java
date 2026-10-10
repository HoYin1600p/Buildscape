package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.DisplaySpellcaster;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpellcasterIllager.class)
public abstract class SpellcasterIllagerMixin extends SpellcasterIllager implements DisplaySpellcaster {
    protected SpellcasterIllagerMixin(EntityType<? extends SpellcasterIllager> type, Level level) {
        super(type, level);
    }

    @Override
    public void buildscape$setDisplayCasting(boolean casting) {
        setIsCastingSpell(casting ? SpellcasterIllager.IllagerSpell.SUMMON_VEX : SpellcasterIllager.IllagerSpell.NONE);
    }
}

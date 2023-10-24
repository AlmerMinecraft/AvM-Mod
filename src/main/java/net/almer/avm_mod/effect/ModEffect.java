package net.almer.avm_mod.effect;

import net.almer.avm_mod.AvMMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffect {
    public static StatusEffect MUSICALITY;
    public static StatusEffect AWAKENING;
    public static StatusEffect registerStatusEffect(String id, StatusEffect effect){
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(AvMMod.MOD_ID, id),
                effect);
    }
    public static void registerEffects(){
        MUSICALITY = registerStatusEffect("musicality", new MusicalEffect(StatusEffectCategory.NEUTRAL, 9154528));
        AWAKENING = registerStatusEffect("awakening", new AwakeningEffect(StatusEffectCategory.NEUTRAL, 6487881));
    }
}

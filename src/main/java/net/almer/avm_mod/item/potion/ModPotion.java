package net.almer.avm_mod.item.potion;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.item.ModItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotion {
    public static final Potion POTION_OF_MUSICALITY = register("musicality", ModEffect.MUSICALITY, 3600);
    public static final Potion HAUNTED_POTION = register("haunted", new Potion(new StatusEffectInstance[0]));
    public static final Potion POTION_OF_AWAKENING = register("awakening", ModEffect.AWAKENING, 3600);
    public static Potion register(String id, StatusEffect effect, int duration){
        return Registry.register(Registries.POTION, new Identifier(AvMMod.MOD_ID, id),
                new Potion(new StatusEffectInstance(effect, duration)));
    }
    public static Potion register(String id, Potion potion){
        return Registry.register(Registries.POTION, id, potion);
    }
    public static void registerPotionsRecipes(){
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.NOTE_BLOCK, ModPotion.POTION_OF_MUSICALITY);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Items.SOUL_SAND, ModPotion.HAUNTED_POTION);
        BrewingRecipeRegistry.registerPotionRecipe(ModPotion.HAUNTED_POTION, Items.CARVED_PUMPKIN, ModPotion.POTION_OF_AWAKENING);
    }
}

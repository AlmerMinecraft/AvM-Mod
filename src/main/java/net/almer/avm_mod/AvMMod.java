package net.almer.avm_mod;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.client.screen.PowerfulStaffScreen;
import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.*;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.item.ModItemGroup;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.almer.avm_mod.item.potion.ModPotion;
import net.almer.avm_mod.network.ModMessages;
import net.almer.avm_mod.sound.ModSound;
import net.almer.avm_mod.util.ModBoostrap;
import net.almer.avm_mod.util.ModifyLootTable;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvMMod implements ModInitializer {
	public static final String MOD_ID = "avm_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItem.registerItems();
		ModBlock.registerBlocks();
		ModItemGroup.registerGroups();
		ModEntities.register();
		FabricDefaultAttributeRegistry.register(ModEntities.LIVING_CHEST, LivingChestEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LIVING_BREWING_STAND, LivingBrewingStandEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LIVING_FURNACE, LivingFurnaceEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TITAN_RAVAGER, TitanRavagerEntity.createRavagerAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SUPER_PIG, SuperPigEntity.createAttributes());
		ModifyLootTable.modifyLootTables();
		Registry.register(Registries.SOUND_EVENT, ModSound.FLUTE_MUSIC, ModSound.FLUTE_MUSIC_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.ELECTRIC_GUITAR, ModSound.ELECTRIC_GUITAR_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.GREEN_JAM, ModSound.GREEN_JAM_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.JAZZY_NOTE_BLOCKS, ModSound.JAZZY_NOTE_BLOCKS_EVENT);
		ModEffect.registerEffects();
		ModPotion.registerPotionsRecipes();
		ModBoostrap.initialize();
		ModMessages.registerC2SPackets();
	}
}
package net.almer.avm_mod;

import net.almer.avm_mod.datagen.ModLootTablesProvider;
import net.almer.avm_mod.datagen.ModModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AvMModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModLootTablesProvider::new);
		pack.addProvider(ModModelProvider::new);
	}
}

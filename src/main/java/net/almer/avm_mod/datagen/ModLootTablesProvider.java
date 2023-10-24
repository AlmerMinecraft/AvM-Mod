package net.almer.avm_mod.datagen;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.block.custom.TomatoBottomCropBlock;
import net.almer.avm_mod.item.ModItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;

public class ModLootTablesProvider extends FabricBlockLootTableProvider {
    public ModLootTablesProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        AnyOfLootCondition.Builder builder2 =
                BlockStatePropertyLootCondition.builder(ModBlock.TOMATO_BOTTOM_CROP).properties(StatePredicate.Builder.create()
                                .exactMatch(TomatoBottomCropBlock.AGE, 3))
                        .or(BlockStatePropertyLootCondition.builder(ModBlock.TOMATO_UPPER_CROP).properties(StatePredicate.Builder.create()
                                .exactMatch(TomatoBottomCropBlock.AGE, 2)));
        addDrop(ModBlock.TOMATO_BOTTOM_CROP, cropDrops(ModBlock.TOMATO_BOTTOM_CROP, ModItem.TOMATO, ModItem.TOMATO_SEEDS, builder2));
        addDrop(ModBlock.TOMATO_UPPER_CROP, cropDrops(ModBlock.TOMATO_UPPER_CROP, ModItem.TOMATO, ModItem.TOMATO_SEEDS, builder2));
    }
}

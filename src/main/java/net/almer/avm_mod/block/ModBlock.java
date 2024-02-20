package net.almer.avm_mod.block;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.block.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;

public class ModBlock {
    public static final Block CHARGED_NOTE_BLOCK = registerBlock("charged_note_block",
            new ChargedNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK)));
    public static final Block BROTH_CAULDRON = registerBlock("broth_cauldron",
            new BrothCauldronBlock(FabricBlockSettings.copyOf(Blocks.WATER_CAULDRON)));
    public static final Block OIL_CAULDRON = registerBlock("oil_cauldron",
            new OilCauldronBlock(FabricBlockSettings.copyOf(Blocks.WATER_CAULDRON)));
    public static final Block TOMATO_BOTTOM_CROP = Registry.register(Registries.BLOCK, new Identifier(AvMMod.MOD_ID, "tomato_bottom_crop"),
            new TomatoBottomCropBlock(FabricBlockSettings.copyOf(Blocks.CARROTS)));
    public static final Block TOMATO_UPPER_CROP = Registry.register(Registries.BLOCK, new Identifier(AvMMod.MOD_ID, "tomato_upper_crop"),
            new TomatoUpperCropBlock(FabricBlockSettings.copyOf(Blocks.CARROTS)));
    public static final Block DEACTIVATED_STAFF = Registry.register(Registries.BLOCK, new Identifier(AvMMod.MOD_ID, "deactivated_staff_block"),
            new DeactivatedStaffBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_GRAY).dropsNothing().allowsSpawning(Blocks::never)));
    public static final Block WINNER_STATUE = Registry.register(Registries.BLOCK, new Identifier(AvMMod.MOD_ID, "winner_statue"),
            new WinnerStatueBlock(FabricBlockSettings.copyOf(Blocks.FLOWER_POT).nonOpaque().strength(-1.0f, 3600000.0f)));
    private static Block registerBlock(String id, Block block){
        registerBlockItem(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(AvMMod.MOD_ID, id), block);
    }
    private static Item registerBlockItem(String id, Block block){
        return Registry.register(Registries.ITEM, new Identifier(AvMMod.MOD_ID, id),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerBlocks(){
        AvMMod.LOGGER.info("Registering blocks for: " + AvMMod.MOD_ID);
    }
}

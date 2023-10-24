package net.almer.avm_mod.item;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.potion.ModPotion;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup AVM_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AvMMod.MOD_ID, "avm_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.avm"))
                    .icon(() -> new ItemStack(ModItem.POWERFUL_STAFF))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItem.POWERFUL_STAFF);
                        entries.add(ModItem.DEACTIVATED_STAFF);
                        entries.add(ModItem.STAFF_ROD);
                        entries.add(ModItem.STAFF_HEAD);
                        entries.add(ModItem.GUITAR);
                        entries.add(ModItem.FLUTE);
                        entries.add(ModItem.NOTE_BOOK);
                        entries.add(ModItem.JAZZY_NOTE_BLOCKS_DISC);
                        entries.add(ModItem.LIVING_CHEST_SPAWN_EGG);
                        entries.add(ModItem.LIVING_BREWING_STAND_SPAWN_EGG);
                        entries.add(ModItem.LIVING_FURNACE_SPAWN_EGG);
                        entries.add(ModItem.TITAN_RAVAGER_SPAWN_EGG);
                        entries.add(ModItem.RAMEN_NOODLES);
                        entries.add(ModItem.BROTH_BUCKET);
                        entries.add(ModItem.SUGAR_BUCKET);
                        entries.add(ModItem.SYROP_BUCKET);
                        entries.add(ModItem.CHEESE_BUCKET);
                        entries.add(ModItem.OIL_BUCKET);
                        entries.add(ModItem.BATTER_BUCKET);
                        entries.add(ModItem.CHOCOLATE_BUCKET);
                        entries.add(ModItem.FLOUR);
                        entries.add(ModItem.BATTER);
                        entries.add(ModItem.RAW_NOODLES);
                        entries.add(ModItem.NOODLES);
                        entries.add(ModItem.PANCAKE);
                        entries.add(ModItem.SYROPED_PANCAKES);
                        entries.add(ModItem.TOMATO_SEEDS);
                        entries.add(ModItem.TOMATO);
                        entries.add(ModItem.CHEESE);
                        entries.add(ModItem.PIZZA);
                        entries.add(ModItem.BREADED_CHICKEN);
                        entries.add(ModItem.FRIED_CHICKEN);
                        entries.add(ModItem.DONUT);
                        entries.add(ModItem.GLAZED_DONUT);
                        entries.add(ModItem.BURGER);
                        entries.add(ModItem.TITAN_RAVAGER_HORN);
                        entries.add(ModItem.EXP_ORB);
                        entries.add(ModItem.WINNER_STATUE);
                        entries.add(ModItem.GAME_ICON);
                    })
                    .build());
    public static void registerGroups(){
        AvMMod.LOGGER.info("Registering item groups for: " + AvMMod.MOD_ID);
    }
}

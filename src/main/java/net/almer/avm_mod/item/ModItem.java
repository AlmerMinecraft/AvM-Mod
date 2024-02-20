package net.almer.avm_mod.item;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.item.custom.*;
import net.almer.avm_mod.sound.ModSound;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItem {
    public static final Item POWERFUL_STAFF = register("staff", new PowerfulStaffItem(ToolMaterials.NETHERITE,
            1, -2.4f,
            new FabricItemSettings()));
    public static final Item GUITAR = register("electric_guitar", new GuitarItem(new FabricItemSettings().maxCount(1)));
    public static final Item FLUTE = register("flute", new FluteItem(new FabricItemSettings().maxCount(1).maxDamage(64)));
    public static final Item NOTE_BOOK = register("note_book", new Item(new FabricItemSettings().maxCount(16)));
    public static final Item JAZZY_NOTE_BLOCKS_DISC = register("jazzy_note_blocks_disc", new MusicDiscItem(15, ModSound.JAZZY_NOTE_BLOCKS_EVENT, new FabricItemSettings().maxCount(1), 159));
    public static final Item LIVING_CHEST_SPAWN_EGG = register("living_chest_spawn_egg", new SpawnEggItem(ModEntities.LIVING_CHEST, 0xE99D11, 0xD0d8d8, new FabricItemSettings()));
    public static final Item LIVING_BREWING_STAND_SPAWN_EGG = register("living_brewing_stand_spawn_egg", new SpawnEggItem(ModEntities.LIVING_BREWING_STAND, 0x6b625c, 0xFfe015, new FabricItemSettings()));
    public static final Item LIVING_FURNACE_SPAWN_EGG = register("living_furnace_spawn_egg", new SpawnEggItem(ModEntities.LIVING_FURNACE, 0x5c6979, 0x21272f, new FabricItemSettings()));
    public static final Item TITAN_RAVAGER_SPAWN_EGG = register("titan_ravager_spawn_egg", new SpawnEggItem(ModEntities.TITAN_RAVAGER, 0x213881, 0x211F2C, new FabricItemSettings()));
    public static final Item BEEPER_SPAWN_EGG = register("beeper_spawn_egg", new SpawnEggItem(ModEntities.CREEPER_BEE, 0x1EAF10, 0x244536, new FabricItemSettings()));
    public static final Item RAMEN_NOODLES = register("ramen_noodles", new Item(new FabricItemSettings().food(ModFoodComponent.RAMEN_NOODLES).maxCount(1)));
    public static final Item BROTH_BUCKET = register("broth_bucket", new BrothBucketItem(Fluids.EMPTY, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item FLOUR = register("flour", new Item(new FabricItemSettings()));
    public static final Item BATTER = register("batter", new Item(new FabricItemSettings()));
    public static final Item NOODLES = register("noodles", new Item(new FabricItemSettings()));
    public static final Item RAW_NOODLES = register("raw_noodles", new Item(new FabricItemSettings()));
    public static final Item SUGAR_BUCKET = register("sugar_bucket", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item SYROP_BUCKET = register("syrop_bucket", new Item(new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item PANCAKE = register("pancake", new Item(new FabricItemSettings().food(ModFoodComponent.PANCAKE)));
    public static final Item SYROPED_PANCAKES = register("syroped_pancakes", new Item(new FabricItemSettings().food(ModFoodComponent.SYROPED_PANCAKES)));
    public static final Item TOMATO_SEEDS = register("tomato_seeds", new AliasedBlockItem(ModBlock.TOMATO_BOTTOM_CROP, new FabricItemSettings()));
    public static final Item TOMATO = register("tomato", new Item(new FabricItemSettings().food(ModFoodComponent.TOMATO)));
    public static final Item CHEESE = register("cheese", new Item(new FabricItemSettings().food(ModFoodComponent.CHEESE)));
    public static final Item CHEESE_BUCKET = register("cheese_bucket", new Item(new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item PIZZA = register("pizza", new Item(new FabricItemSettings().food(ModFoodComponent.PIZZA).maxCount(1)));
    public static final Item OIL_BUCKET = register("oil_bucket", new OilBucketItem(Fluids.EMPTY, new FabricItemSettings().maxCount(1)));
    public static final Item CHOCOLATE_BUCKET = register("chocolate_bucket", new Item(new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item BATTER_BUCKET = register("batter_bucket", new BatterBucketItem(Fluids.EMPTY, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item BREADED_CHICKEN = register("breaded_chicken", new Item(new FabricItemSettings()));
    public static final Item FRIED_CHICKEN = register("fried_chicken", new Item(new FabricItemSettings().food(ModFoodComponent.FRIED_CHICKEN)));
    public static final Item DONUT = register("donut", new Item(new FabricItemSettings()));
    public static final Item GLAZED_DONUT = register("glazed_donut", new Item(new FabricItemSettings().food(ModFoodComponent.GLAZED_DONUT).maxCount(16)));
    public static final Item BURGER = register("burger", new Item(new FabricItemSettings().food(ModFoodComponent.BURGER)));
    public static final Item TITAN_RAVAGER_HORN = register("titan_ravager_horn", new Item(new FabricItemSettings()));
    public static final Item EXP_ORB = register("exp_orb", new ExperienceBottleItem(new FabricItemSettings()));
    public static final Item STAFF_ROD = register("staff_rod", new Item(new FabricItemSettings()));
    public static final Item STAFF_HEAD = register("staff_head", new Item(new FabricItemSettings()));
    public static final Item DEACTIVATED_STAFF = register("deactivated_staff", new DeactivatedStaffItem(ModBlock.DEACTIVATED_STAFF, new FabricItemSettings().maxCount(1)));
    public static final Item WINNER_STATUE = register("winner_statue", new BlockItem(ModBlock.WINNER_STATUE, new FabricItemSettings()));
    public static final Item GAME_ICON = register("game_icon", new GameIconItem(new FabricItemSettings()));
    private static Item register(String id, Item item){
        return Registry.register(Registries.ITEM, new Identifier(AvMMod.MOD_ID, id), item);
    }
    public static void registerItems(){
        AvMMod.LOGGER.info("Registering items for: " + AvMMod.MOD_ID);
    }
}

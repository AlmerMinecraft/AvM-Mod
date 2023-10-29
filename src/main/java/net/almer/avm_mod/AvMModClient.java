package net.almer.avm_mod;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.client.*;
import net.almer.avm_mod.entity.client.dark.*;
import net.almer.avm_mod.entity.custom.LivingBrewingStandEntity;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AvMModClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_CHEST_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "living_chest"), "main");
    public static final EntityModelLayer MODEL_BREWING_STAND_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "living_brewing_stand"), "main");
    public static final EntityModelLayer MODEL_FURNACE_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "living_furnace"), "main");
    public static final EntityModelLayer TITAN_RAVAGER_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "titan_ravager"), "main");
    public static final EntityModelLayer SUPER_PIG_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "super_pig"), "main");
    public static final EntityModelLayer DARK_ZOMBIE_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_zombie"), "main");
    public static final EntityModelLayer DARK_SKELETON_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_skeleton"), "main");
    public static final EntityModelLayer DARK_CREEPER_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_creeper"), "main");
    public static final EntityModelLayer DARK_SPIDER_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_spider"), "main");
    public static final EntityModelLayer DARK_ENDERMAN_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_enderman"), "main");
    public static final EntityModelLayer DARK_PHANTOM_LAYER = new EntityModelLayer(new Identifier(AvMMod.MOD_ID, "dark_phantom"), "main");
    public static KeyBinding POWERFUL_STAFF_USE;
    public static KeyBinding POWERFUL_STAFF_USE_1;
    public static KeyBinding POWERFUL_STAFF_USE_2;
    public static KeyBinding POWERFUL_STAFF_USE_3;
    public static KeyBinding POWERFUL_STAFF_USE_4;
    public static KeyBinding POWERFUL_STAFF_USE_5;
    public static KeyBinding POWERFUL_STAFF_USE_6;
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.LIVING_CHEST, (context) -> {
            return new LivingChestRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.LIVING_BREWING_STAND, (context) -> {
            return new LivingBrewingStandRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.LIVING_FURNACE, (context) -> {
            return new LivingFurnaceRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.TITAN_RAVAGER, (context) ->{
            return new TitanRavagerRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.SUPER_PIG, (context) ->{
            return new SuperPigRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_ZOMBIE, (context) ->{
            return new DarkZombieRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_SKELETON, (context) ->{
            return new DarkSkeletonRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_CREEPER, (context) ->{
            return new DarkCreeperRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_SPIDER, (context) ->{
            return new DarkSpiderRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_ENDERMAN, (context) ->{
            return new DarkEndermanRenderer(context);
        });
        EntityRendererRegistry.register(ModEntities.DARK_PHANTOM, (context) ->{
            return new DarkPhantomRenderer(context);
        });
        EntityModelLayerRegistry.registerModelLayer(MODEL_CHEST_LAYER, LivingChestModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BREWING_STAND_LAYER, LivingBrewingStandModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_FURNACE_LAYER, LivingFurnaceModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TITAN_RAVAGER_LAYER, TitanRavagerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUPER_PIG_LAYER, SuperPigModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_ZOMBIE_LAYER, DarkZombieModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_SKELETON_LAYER, DarkSkeletonModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_CREEPER_LAYER, DarkCreeperModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_SPIDER_LAYER, DarkSpiderModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_ENDERMAN_LAYER, DarkEndermanModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DARK_PHANTOM_LAYER, DarkPhantomModel::getTexturedModelData);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlock.TOMATO_BOTTOM_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlock.TOMATO_UPPER_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlock.DEACTIVATED_STAFF, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlock.WINNER_STATUE, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "blocked"), (stack, world, entity, seed) ->
                PowerfulStaffItem.isBlocked(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "copper"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.COPPER_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "iron"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.IRON_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "gold"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.GOLD_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "emerald"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.EMERALD_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "diamond"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.DIAMOND_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "netherite"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.NETHERITE_BLOCK) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItem.POWERFUL_STAFF, new Identifier(AvMMod.MOD_ID, "command"), (stack, world, entity, seed) ->
                PowerfulStaffItem.hasBlocks(stack, Items.COMMAND_BLOCK) ? 1.0f : 0.0f);

        POWERFUL_STAFF_USE = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_1 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_2 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_3 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_4 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_5 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_5", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "category.avm_mod.keybinds"));
        POWERFUL_STAFF_USE_6 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avm_mod.staff_use_6", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, "category.avm_mod.keybinds"));
    }
}

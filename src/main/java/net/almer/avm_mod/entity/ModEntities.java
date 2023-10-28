package net.almer.avm_mod.entity;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.entity.custom.*;
import net.almer.avm_mod.entity.custom.dark.DarkSkeletonEntity;
import net.almer.avm_mod.entity.custom.dark.DarkZombieEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<LivingChestEntity> LIVING_CHEST = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "living_chest"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, LivingChestEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<LivingBrewingStandEntity> LIVING_BREWING_STAND = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "living_brewing_stand"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, LivingBrewingStandEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<LivingFurnaceEntity> LIVING_FURNACE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "living_furnace"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, LivingFurnaceEntity::new)
                    .dimensions(EntityDimensions.fixed(1,1)).build());
    public static final EntityType<TitanRavagerEntity> TITAN_RAVAGER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "titan_ravager"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TitanRavagerEntity::new)
                    .dimensions(EntityDimensions.fixed(8, 8)).build());
    public static final EntityType<SuperPigEntity> SUPER_PIG = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "super_pig"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SuperPigEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.9f)).build());
    public static final EntityType<DarkZombieEntity> DARK_ZOMBIE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "dark_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DarkZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
    public static final EntityType<DarkSkeletonEntity> DARK_SKELETON = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(AvMMod.MOD_ID, "dark_skeleton"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DarkSkeletonEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
    public static void register(){
        AvMMod.LOGGER.info("Registering entities for: " + AvMMod.MOD_ID);
    }
}

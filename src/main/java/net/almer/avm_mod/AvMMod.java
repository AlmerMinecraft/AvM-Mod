package net.almer.avm_mod;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.client.screen.PowerfulStaffScreen;
import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.*;
import net.almer.avm_mod.entity.custom.dark.*;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.item.ModItemGroup;
import net.almer.avm_mod.item.custom.GameIconItem;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.almer.avm_mod.item.potion.ModPotion;
import net.almer.avm_mod.network.ModMessages;
import net.almer.avm_mod.sound.ModSound;
import net.almer.avm_mod.util.ModBoostrap;
import net.almer.avm_mod.util.ModifyLootTable;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;


public class AvMMod implements ModInitializer {
	public static final String MOD_ID = "avm_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final EntityAttributeModifier singleRangeAttributeModifier =
			new EntityAttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier", 10,
					EntityAttributeModifier.Operation.ADDITION);
	public static final Supplier<Multimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() ->
			ImmutableMultimap.of(AvMMod.REACH, singleRangeAttributeModifier));
	public static final EntityAttribute REACH = make("reach", 0.0, -1024.0, 1024.0);
	public static final EntityAttribute ATTACK_RANGE = make("attack_range", 0.0, -1024.0, 1024.0);

	public static double getReachDistance(final LivingEntity entity, final double baseReachDistance) {
		@Nullable final var reachDistance = entity.getAttributeInstance(REACH);
		return (reachDistance != null) ? (baseReachDistance + reachDistance.getValue()) : baseReachDistance;
	}

	public static double getSquaredReachDistance(final LivingEntity entity, final double sqBaseReachDistance) {
		final var reachDistance = getReachDistance(entity, Math.sqrt(sqBaseReachDistance));
		return reachDistance * reachDistance;
	}

	public static double getAttackRange(final LivingEntity entity, final double baseAttackRange) {
		@Nullable final var attackRange = entity.getAttributeInstance(ATTACK_RANGE);
		return (attackRange != null) ? (baseAttackRange + attackRange.getValue()) : baseAttackRange;
	}

	public static double getSquaredAttackRange(final LivingEntity entity, final double sqBaseAttackRange) {
		final var attackRange = getAttackRange(entity, Math.sqrt(sqBaseAttackRange));
		return attackRange * attackRange;
	}

	public static List<PlayerEntity> getPlayersWithinReach(final World world, final int x, final int y, final int z, final double baseReachDistance) {
		return getPlayersWithinReach(player -> true, world, x, y, z, baseReachDistance);
	}

	public static List<PlayerEntity> getPlayersWithinReach(final Predicate<PlayerEntity> viewerPredicate, final World world, final int x, final int y, final int z, final double baseReachDistance) {
		final List<PlayerEntity> playersWithinReach = new ArrayList<>(0);
		for (final PlayerEntity player : world.getPlayers()) {
			if (viewerPredicate.test(player)) {
				final var reach = getReachDistance(player, baseReachDistance);
				final var dx = (x + 0.5) - player.getX();
				final var dy = (y + 0.5) - player.getEyeY();
				final var dz = (z + 0.5) - player.getZ();
				if (((dx * dx) + (dy * dy) + (dz * dz)) <= (reach * reach)) {
					playersWithinReach.add(player);
				}
			}
		}
		return playersWithinReach;
	}

	public static boolean isWithinAttackRange(final PlayerEntity player, final Entity entity) {
		return player.squaredDistanceTo(entity) <= getSquaredAttackRange(player, 64.0);
	}

	private static EntityAttribute make(final String name, final double base, final double min, final double max) {
		return new ClampedEntityAttribute("attribute.name.generic." + MOD_ID + '.' + name, base, min, max).setTracked(true);
	}
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
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_ZOMBIE, DarkZombieEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_SKELETON, DarkSkeletonEntity.createAbstractSkeletonAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_CREEPER, DarkCreeperEntity.createCreeperAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_SPIDER, DarkSpiderEntity.createSpiderAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_ENDERMAN, DarkEndermanEntity.createEndermanAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DARK_PHANTOM, DarkPhantomEntity.createPhantomAttributes());
		ModifyLootTable.modifyLootTables();
		Registry.register(Registries.SOUND_EVENT, ModSound.FLUTE_MUSIC, ModSound.FLUTE_MUSIC_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.ELECTRIC_GUITAR, ModSound.ELECTRIC_GUITAR_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.GREEN_JAM, ModSound.GREEN_JAM_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSound.JAZZY_NOTE_BLOCKS, ModSound.JAZZY_NOTE_BLOCKS_EVENT);
		ModEffect.registerEffects();
		ModPotion.registerPotionsRecipes();
		ModBoostrap.initialize();
		ModMessages.registerC2SPackets();
		Registry.register(Registries.ATTRIBUTE, new Identifier(MOD_ID, "reach"), REACH);
		Registry.register(Registries.ATTRIBUTE, new Identifier(MOD_ID, "attack_range"), ATTACK_RANGE);
	}
}
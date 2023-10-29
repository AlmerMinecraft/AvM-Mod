package net.almer.avm_mod.entity.custom;

import net.almer.avm_mod.entity.LivingBlocks;
import net.almer.avm_mod.entity.ModEntities;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LivingChestEntity extends TameableEntity implements LivingBlocks {
    List playerList;
    double distance = 5;
    @Nullable
    private Identifier lootTableId;
    private final World world;
    private long lootSeed;
    protected SimpleInventory items;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    public LivingChestEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.world = world;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 0.7, 2, 40, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.5));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.LIVING_CHEST.create(world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
            if (player.isSneaking()) {
                if (this.isTamed()) {
                    if (this.isOwner(player)) {
                        if (this.isSitting()) {
                            setSitting(false);
                            return ActionResult.SUCCESS;
                        } else {
                            setSitting(true);
                            this.jumping = false;
                            this.navigation.stop();
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    setOwner(player);
                    return ActionResult.SUCCESS;
                }
            }
            else{
                ActionResult actionResult = this.open(player);
                if (actionResult.isAccepted()) {
                    this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
                    PiglinBrain.onGuardedBlockInteracted(player, true);
                }
                return actionResult;
            }
        return super.interactMob(player, hand);
    }

    @Nullable
    public Identifier getLootTableId() {
        return this.lootTableId;
    }
    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootSeed = lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getInventoryStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.removeInventoryStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeInventoryStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.setInventoryStack(slot, stack);
    }

    @Override
    public void markDirty() {
    }
    @Override
    public World getWorld(){
        return this.world;
    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (this.lootTableId == null || !player.isSpectator()) {
            this.generateInventoryLoot(playerInventory.player);
            return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
        }
        return null;
    }

    @Override
    public void clear() {
        this.clearInventory();
    }
    @Override
    public void onClose(PlayerEntity player) {
        this.getWorld().emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of(player));
    }
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory == null) {
            return;
        }
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.get(i);
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.dropStack(itemStack);
        }
        if (!this.getWorld().isClient) {
            this.dropItem(Blocks.CHEST);
        }
    }
}

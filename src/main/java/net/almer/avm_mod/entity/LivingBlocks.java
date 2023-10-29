package net.almer.avm_mod.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface LivingBlocks extends Inventory, NamedScreenHandlerFactory {
    public Vec3d getPos();

    @Nullable
    public Identifier getLootTableId();

    public void setLootTableId(@Nullable Identifier var1);

    public long getLootTableSeed();

    public void setLootTableSeed(long var1);

    public DefaultedList<ItemStack> getInventory();

    public void resetInventory();

    public World getWorld();

    public boolean isRemoved();

    @Override
    default public boolean isEmpty() {
        return this.isInventoryEmpty();
    }
    default public ActionResult open(PlayerEntity player) {
        player.openHandledScreen(this);
        if (!player.getWorld().isClient) {
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }

    default public void generateInventoryLoot(@Nullable PlayerEntity player) {
        MinecraftServer minecraftServer = this.getWorld().getServer();
        if (this.getLootTableId() != null && minecraftServer != null) {
            LootTable lootTable = minecraftServer.getLootManager().getLootTable(this.getLootTableId());
            if (player != null) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, this.getLootTableId());
            }
            this.setLootTableId(null);
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)this.getWorld()).add(LootContextParameters.ORIGIN, this.getPos());
            if (player != null) {
                builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
            }
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST), this.getLootTableSeed());
        }
    }

    default public void clearInventory() {
        this.generateInventoryLoot(null);
        this.getInventory().clear();
    }

    default public boolean isInventoryEmpty() {
        for (ItemStack itemStack : this.getInventory()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    default public ItemStack removeInventoryStack(int slot) {
        this.generateInventoryLoot(null);
        ItemStack itemStack = this.getInventory().get(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.getInventory().set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    default public ItemStack getInventoryStack(int slot) {
        this.generateInventoryLoot(null);
        return this.getInventory().get(slot);
    }

    default public ItemStack removeInventoryStack(int slot, int amount) {
        this.generateInventoryLoot(null);
        return Inventories.splitStack(this.getInventory(), slot, amount);
    }

    default public void setInventoryStack(int slot, ItemStack stack) {
        this.generateInventoryLoot(null);
        this.getInventory().set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }
}

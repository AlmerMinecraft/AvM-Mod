package net.almer.avm_mod.mixin;

import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.SuperPigEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PigEntity.class)
public class PigEntityMixin extends LivingEntity{
    protected PigEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return ItemStack.EMPTY.getHolder().getArmorItems();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return null;
    }

    @Override
    public void tick() {
        SuperPigEntity entity = new SuperPigEntity(ModEntities.SUPER_PIG, this.getWorld());
        if(this.hasStatusEffect(StatusEffects.STRENGTH) &&
                this.hasStatusEffect(StatusEffects.SPEED) &&
                this.hasStatusEffect(StatusEffects.JUMP_BOOST) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && this.hasStatusEffect(StatusEffects.REGENERATION)){
            entity.setPos(this.getX(), this.getY(), this.getZ());
            this.getWorld().spawnEntity(entity);
            this.discard();
        }
    }
}

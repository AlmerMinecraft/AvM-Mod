package net.almer.avm_mod.mixin;

import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.SuperPigEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PigEntity.class)
public abstract class PigEntityMixin extends AnimalEntity{
    protected PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    public void onStartPathfinding() {
        if (this.hasStatusEffect(StatusEffects.STRENGTH) &&
                this.hasStatusEffect(StatusEffects.SPEED) &&
                this.hasStatusEffect(StatusEffects.JUMP_BOOST) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && this.hasStatusEffect(StatusEffects.REGENERATION)) {
            SuperPigEntity entity = new SuperPigEntity(ModEntities.SUPER_PIG, this.getWorld());
            entity.setPos(this.getX(), this.getY() + 0.2, this.getZ());
            this.getWorld().spawnEntity(entity);
            this.discard();
        }
    }
    @Override
    protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
        if (this.hasStatusEffect(StatusEffects.STRENGTH) &&
                this.hasStatusEffect(StatusEffects.SPEED) &&
                this.hasStatusEffect(StatusEffects.JUMP_BOOST) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && this.hasStatusEffect(StatusEffects.REGENERATION)) {
            SuperPigEntity entity = new SuperPigEntity(ModEntities.SUPER_PIG, this.getWorld());
            entity.setPos(this.getX(), this.getY() + 0.2, this.getZ());
            this.getWorld().spawnEntity(entity);
            this.discard();
        }
    }

    @Override
    public void onFinishPathfinding() {
        if (this.hasStatusEffect(StatusEffects.STRENGTH) &&
                this.hasStatusEffect(StatusEffects.SPEED) &&
                this.hasStatusEffect(StatusEffects.JUMP_BOOST) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && this.hasStatusEffect(StatusEffects.REGENERATION)) {
            SuperPigEntity entity = new SuperPigEntity(ModEntities.SUPER_PIG, this.getWorld());
            entity.setPos(this.getX(), this.getY() + 0.2, this.getZ());
            this.getWorld().spawnEntity(entity);
            this.discard();
        }
    }
}

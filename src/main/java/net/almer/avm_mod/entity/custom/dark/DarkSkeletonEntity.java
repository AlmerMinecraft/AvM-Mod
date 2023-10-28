/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.custom.dark;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;

public class DarkSkeletonEntity extends TameableEntity implements RangedAttackMob {
    private final ModAttackBow<DarkSkeletonEntity> bowAttackGoal = new ModAttackBow<DarkSkeletonEntity>(this, 1.0, 20, 15.0f);
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false);
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.74f;
    }

    @Override
    public double getHeightOffset() {
        return -0.6;
    }
    public DarkSkeletonEntity(EntityType<? extends DarkSkeletonEntity> entityType, World world) {
        super((EntityType<? extends DarkSkeletonEntity>)entityType, world);
        this.updateAttackType();
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
    }
    public static DefaultAttributeContainer.Builder createAbstractSkeletonAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }
    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }
    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(random.nextFloat() < 0.55f * difficulty.getClampedLocalDifficulty());
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25f) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
            }
        }
        return entityData;
    }
    public void updateAttackType() {
        if (this.getWorld() == null || this.getWorld().isClient) {
            return;
        }
        this.goalSelector.remove(this.meleeAttackGoal);
        this.goalSelector.remove(this.bowAttackGoal);
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        if (itemStack.isOf(Items.BOW)) {
            int i = 20;
            if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                i = 40;
            }
            this.bowAttackGoal.setAttackInterval(i);
            this.goalSelector.add(4, this.bowAttackGoal);
        } else {
            this.goalSelector.add(4, this.meleeAttackGoal);
        }
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getProjectileType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * (double)0.2f, f, 1.6f, 14 - this.getWorld().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
    }
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    @Override
    public boolean canFreeze() {
        return false;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
    class ModAttackBow<T extends DarkSkeletonEntity> extends Goal {
        private final T actor;
        private final double speed;
        private int attackInterval;
        private final float squaredRange;
        private int cooldown = -1;
        private int targetSeeingTicker;
        private boolean movingToLeft;
        private boolean backward;
        private int combatTicks = -1;

        public ModAttackBow(T actor, double speed, int attackInterval, float range) {
            this.actor = actor;
            this.speed = speed;
            this.attackInterval = attackInterval;
            this.squaredRange = range * range;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canStart() {
            if (((MobEntity)this.actor).getTarget() == null) {
                return false;
            }
            return this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return ((LivingEntity)this.actor).isHolding(Items.BOW);
        }

        @Override
        public boolean shouldContinue() {
            return (this.canStart() || !((MobEntity)this.actor).getNavigation().isIdle()) && this.isHoldingBow();
        }

        @Override
        public void start() {
            super.start();
            ((MobEntity)this.actor).setAttacking(true);
        }

        @Override
        public void stop() {
            super.stop();
            ((MobEntity)this.actor).setAttacking(false);
            this.targetSeeingTicker = 0;
            this.cooldown = -1;
            ((LivingEntity)this.actor).clearActiveItem();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            boolean bl2;
            LivingEntity livingEntity = ((MobEntity)this.actor).getTarget();
            if (livingEntity == null) {
                return;
            }
            double d = ((Entity)this.actor).squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean bl = ((MobEntity)this.actor).getVisibilityCache().canSee(livingEntity);
            boolean bl3 = bl2 = this.targetSeeingTicker > 0;
            if (bl != bl2) {
                this.targetSeeingTicker = 0;
            }
            this.targetSeeingTicker = bl ? ++this.targetSeeingTicker : --this.targetSeeingTicker;
            if (d > (double)this.squaredRange || this.targetSeeingTicker < 20) {
                ((MobEntity)this.actor).getNavigation().startMovingTo(livingEntity, this.speed);
                this.combatTicks = -1;
            } else {
                ((MobEntity)this.actor).getNavigation().stop();
                ++this.combatTicks;
            }
            if (this.combatTicks >= 20) {
                if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                    boolean bl4 = this.movingToLeft = !this.movingToLeft;
                }
                if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                    this.backward = !this.backward;
                }
                this.combatTicks = 0;
            }
            if (this.combatTicks > -1) {
                if (d > (double)(this.squaredRange * 0.75f)) {
                    this.backward = false;
                } else if (d < (double)(this.squaredRange * 0.25f)) {
                    this.backward = true;
                }
                ((MobEntity)this.actor).getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
                Entity entity = ((Entity)this.actor).getControllingVehicle();
                if (entity instanceof MobEntity) {
                    MobEntity mobEntity = (MobEntity)entity;
                    mobEntity.lookAtEntity(livingEntity, 30.0f, 30.0f);
                }
                ((MobEntity)this.actor).lookAtEntity(livingEntity, 30.0f, 30.0f);
            } else {
                ((MobEntity)this.actor).getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
            }
            if (((LivingEntity)this.actor).isUsingItem()) {
                int i;
                if (!bl && this.targetSeeingTicker < -60) {
                    ((LivingEntity)this.actor).clearActiveItem();
                } else if (bl && (i = ((LivingEntity)this.actor).getItemUseTime()) >= 20) {
                    ((LivingEntity)this.actor).clearActiveItem();
                    ((RangedAttackMob)this.actor).attack(livingEntity, BowItem.getPullProgress(i));
                    this.cooldown = this.attackInterval;
                }
            } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                ((LivingEntity)this.actor).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
            }
        }
    }
}


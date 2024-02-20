package net.almer.avm_mod.entity.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.almer.avm_mod.entity.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AllayBrain;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class TitanRavagerEntity extends PathAwareEntity implements Vibrations {
    private static final Predicate<Entity> IS_NOT_RAVAGER = entity -> entity.isAlive() && !(entity instanceof RavagerEntity);
    private static final Predicate<Entity> RAVAGER_FOOD = entity -> entity instanceof ItemEntity item &&
            (item.getStack().isOf(Items.POTATO) ||
                    item.getStack().isOf(Items.CARROT) ||
                    item.getStack().isOf(Items.MELON_SLICE) ||
                    item.getStack().isOf(Items.BEETROOT) ||
                    item.getStack().isOf(Items.WHEAT) ||
                    item.getStack().isOf(Items.PUMPKIN));
    private static final double field_30480 = 0.3;
    private static final double field_30481 = 0.35;
    private static final int field_30482 = 8356754;
    private static final double STUNNED_PARTICLE_Z_VELOCITY = 0.5725490196078431;
    private static final double STUNNED_PARTICLE_Y_VELOCITY = 0.5137254901960784;
    private static final double STUNNED_PARTICLE_X_VELOCITY = 0.4980392156862745;
    private static final int field_30486 = 10;
    public static final int field_30479 = 40;
    private int attackTick;
    private int stunTick;
    private int roarTick;
    public TitanRavagerEntity(EntityType<? extends TitanRavagerEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0f);
        this.experiencePoints = 20;
        this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0f);
    }
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PickupFoodGoal<TitanRavagerEntity>(this, this));
        this.goalSelector.add(4, new AttackGoal());
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(2, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(3, new ActiveTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<LivingEntity>((MobEntity)this, LivingEntity.class, true));
    }
    @Override
    protected void updateGoalControls() {
        boolean bl = !(this.getControllingPassenger() instanceof MobEntity) || this.getControllingPassenger().getType().isIn(EntityTypeTags.RAIDERS);
        boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
        this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
        this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
        this.goalSelector.setControlEnabled(Goal.Control.TARGET, bl);
    }
    public static DefaultAttributeContainer.Builder createRavagerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AttackTick", this.attackTick);
        nbt.putInt("StunTick", this.stunTick);
        nbt.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.attackTick = nbt.getInt("AttackTick");
        this.stunTick = nbt.getInt("StunTick");
        this.roarTick = nbt.getInt("RoarTick");
    }
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.6f;
    }

    @Override
    public int getMaxHeadRotation() {
        return 45;
    }

    public double getMountedHeightOffset() {
        return 2.1;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        LivingEntity livingEntity;
        Entity entity;
        return !this.isAiDisabled() && (entity = this.getFirstPassenger()) instanceof LivingEntity ? (livingEntity = (LivingEntity)entity) : null;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.isAlive()) {
            return;
        }
        if (this.isImmobile()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        } else {
            double d = this.getTarget() != null ? 0.35 : 0.3;
            double e = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue();
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
        }
        if (this.horizontalCollision && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            boolean bl = false;
            Box box = this.getBoundingBox().expand(0.2);
            for (BlockPos blockPos : BlockPos.iterate(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ))) {
                BlockState blockState = this.getWorld().getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (!(block instanceof LeavesBlock)) continue;
                bl = this.getWorld().breakBlock(blockPos, true, this) || bl;
            }
            if (!bl && this.isOnGround()) {
                this.jump();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        eat();
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunTick > 0) {
            --this.stunTick;
            this.spawnStunnedParticles();
            if (this.stunTick == 0) {
                this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }
    private void spawnStunnedParticles() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double)this.getWidth() * Math.sin(this.bodyYaw * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            double e = this.getY() + (double)this.getHeight() - 0.3;
            double f = this.getZ() + (double)this.getWidth() * Math.cos(this.bodyYaw * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, d, e, f, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean canSee(Entity entity) {
        if (this.stunTick > 0 || this.roarTick > 0) {
            return false;
        }
        return super.canSee(entity);
    }

    @Override
    protected void knockback(LivingEntity target) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunTick = 40;
                this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0f, 1.0f);
                this.getWorld().sendEntityStatus(this, EntityStatuses.STUN_RAVAGER);
                target.pushAwayFrom(this);
            } else {
                this.knockBack(target);
            }
            target.velocityModified = true;
        }
    }
    private void roar() {
        if (this.isAlive()) {
            List<? extends LivingEntity> list = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0), IS_NOT_RAVAGER);
            LivingEntity livingEntity;
            for(Iterator var2 = list.iterator(); var2.hasNext(); this.knockBack(livingEntity)) {
                livingEntity = (LivingEntity)var2.next();
                if (!(livingEntity instanceof IllagerEntity)) {
                    livingEntity.damage(this.getDamageSources().mobAttack(this), 6.0F);
                }
            }
            Vec3d vec3d = this.getBoundingBox().getCenter();
            for(int i = 0; i < 40; ++i) {
                double d = this.random.nextGaussian() * 0.2;
                double e = this.random.nextGaussian() * 0.2;
                double f = this.random.nextGaussian() * 0.2;
                this.getWorld().addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
            }
            this.emitGameEvent(GameEvent.ENTITY_ACTION);
        }
    }
    private void eat(){
        if(this.isAlive()){
            List<ItemEntity> list = this.getWorld().getEntitiesByClass(ItemEntity.class, this.getBoundingBox(), RAVAGER_FOOD);
            for(ItemEntity item : list){
                item.discard();
            }
        }
    }

    private void knockBack(Entity entity) {
        double d = entity.getX() - this.getX();
        double e = entity.getZ() - this.getZ();
        double f = Math.max(d * d + e * e, 0.001);
        entity.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
            this.attackTick = 10;
            this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0f, 1.0f);
        } else if (status == EntityStatuses.STUN_RAVAGER) {
            this.stunTick = 40;
        }
        super.handleStatus(status);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunTick() {
        return this.stunTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.attackTick = 10;
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0f, 1.0f);
        return super.tryAttack(target);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return !world.containsFluid(this.getBoundingBox());
    }

    @Override
    public ListenerData getVibrationListenerData() {
        return null;
    }

    @Override
    public Callback getVibrationCallback() {
        return null;
    }

    class AttackGoal
            extends MeleeAttackGoal {
        public AttackGoal() {
            super(TitanRavagerEntity.this, 1.0, true);
        }
        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            float f = TitanRavagerEntity.this.getWidth() - 0.1f;
            return f * 2.0f * (f * 2.0f) + entity.getWidth();
        }
    }
    public static class PickupFoodGoal<T extends TitanRavagerEntity>
            extends Goal {
        private final T actor;
        final TitanRavagerEntity field_16604;

        public PickupFoodGoal(T actor, TitanRavagerEntity ravager) {
            this.actor = actor;
            this.field_16604 = ravager;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }
        @Override
        public boolean canStart() {
            List<ItemEntity> list;
            if (!((list = ((Entity)this.actor).getWorld().getEntitiesByClass(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0, 8.0, 16.0), RAVAGER_FOOD)).isEmpty())) {
                return ((MobEntity)this.actor).getNavigation().startMovingTo(list.get(0), 1.15f);
            }
            return false;
        }
        @Override
        public void tick() {
            List<ItemEntity> list;
            if (((MobEntity)this.actor).getNavigation().getTargetPos().isWithinDistance(((Entity)this.actor).getPos(), 1.414) && !(list = ((Entity)this.actor).getWorld().getEntitiesByClass(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(4.0, 4.0, 4.0), RAVAGER_FOOD)).isEmpty()) {
                ((TitanRavagerEntity)this.actor).loot(list.get(0));
            }
        }
    }
}

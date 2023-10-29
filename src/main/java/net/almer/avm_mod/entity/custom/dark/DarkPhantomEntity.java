/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.custom.dark;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class DarkPhantomEntity extends TameableEntity {
    public static final int WING_FLAP_TICKS = MathHelper.ceil(24.166098f);
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(DarkPhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    Vec3d targetPosition = Vec3d.ZERO;
    BlockPos circlingCenter = BlockPos.ORIGIN;
    PhantomMovementType movementType = PhantomMovementType.CIRCLE;
    public DarkPhantomEntity(EntityType<? extends DarkPhantomEntity> entityType, World world) {
        super((EntityType<? extends TameableEntity>)entityType, world);
        this.experiencePoints = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }
    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }
    @Override
    public void travel(Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8f));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
            } else {
                float f = 0.91f;
                if (this.isOnGround()) {
                    f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91f;
                }
                float g = 0.16277137f / (f * f * f);
                f = 0.91f;
                if (this.isOnGround()) {
                    f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91f;
                }
                this.updateVelocity(this.isOnGround() ? 0.1f * g : 0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(f));
            }
        }
        this.updateLimbs(false);
    }
    @Override
    public boolean isClimbing() {
        return false;
    }
    @Override
    public boolean isFlappingWings() {
        return (this.getWingFlapTickOffset() + this.age) % WING_FLAP_TICKS == 0;
    }
    @Override
    protected BodyControl createBodyControl() {
        return new PhantomBodyControl(this);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new StartAttackGoal());
        this.goalSelector.add(2, new SwoopMovementGoal());
        this.goalSelector.add(3, new CircleMovementGoal());
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1.0, 10, 2, false));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]));
    }
    public static DefaultAttributeContainer.Builder createPhantomAttributes(){
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6);
    }
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SIZE, 0);
    }
    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35f;
    }
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
    }
    public int getWingFlapTickOffset() {
        return this.getId() * 3;
    }
    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }
    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            float f = MathHelper.cos((float)(this.getWingFlapTickOffset() + this.age) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            float g = MathHelper.cos((float)(this.getWingFlapTickOffset() + this.age + 1) * 7.448451f * ((float)Math.PI / 180) + (float)Math.PI);
            if (f > 0.0f && g <= 0.0f) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            float h = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)0);
            float j = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)0);
            float k = (0.3f + f * 0.45f) * ((float)0 * 0.2f + 1.0f);
            this.getWorld().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)h, this.getY() + (double)k, this.getZ() + (double)j, 0.0, 0.0, 0.0);
            this.getWorld().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)h, this.getY() + (double)k, this.getZ() - (double)j, 0.0, 0.0, 0.0);
        }
    }
    @Override
    protected void mobTick() {
        super.mobTick();
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.circlingCenter = this.getBlockPos().up(5);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("AX")) {
            this.circlingCenter = new BlockPos(nbt.getInt("AX"), nbt.getInt("AY"), nbt.getInt("AZ"));
        }
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AX", this.circlingCenter.getX());
        nbt.putInt("AY", this.circlingCenter.getY());
        nbt.putInt("AZ", this.circlingCenter.getZ());
    }
    @Override
    public boolean shouldRender(double distance) {
        return true;
    }
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }
    @Override
    public boolean canTarget(EntityType<?> type) {
        return true;
    }
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions entityDimensions = super.getDimensions(pose);
        float f = (entityDimensions.width + 0.2f * (float)0) / entityDimensions.width;
        return entityDimensions.scaled(f);
    }
    @Override
    public double getMountedHeightOffset() {
        return this.getStandingEyeHeight();
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isOf(DamageTypes.FALL)){
            return false;
        }
        return super.damage(source, amount);
    }
    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
    static enum PhantomMovementType {
        CIRCLE,
        SWOOP;

    }
    class PhantomMoveControl
    extends MoveControl {
        private float targetSpeed;
        public PhantomMoveControl(MobEntity owner) {
            super(owner);
            this.targetSpeed = 0.1f;
        }
        @Override
        public void tick() {
            if (DarkPhantomEntity.this.horizontalCollision) {
                DarkPhantomEntity.this.setYaw(DarkPhantomEntity.this.getYaw() + 180.0f);
                this.targetSpeed = 0.1f;
            }
            double d = DarkPhantomEntity.this.targetPosition.x - DarkPhantomEntity.this.getX();
            double e = DarkPhantomEntity.this.targetPosition.y - DarkPhantomEntity.this.getY();
            double f = DarkPhantomEntity.this.targetPosition.z - DarkPhantomEntity.this.getZ();
            double g = Math.sqrt(d * d + f * f);
            if (Math.abs(g) > (double)1.0E-5f) {
                double h = 1.0 - Math.abs(e * (double)0.7f) / g;
                g = Math.sqrt((d *= h) * d + (f *= h) * f);
                double i = Math.sqrt(d * d + f * f + e * e);
                float j = DarkPhantomEntity.this.getYaw();
                float k = (float)MathHelper.atan2(f, d);
                float l = MathHelper.wrapDegrees(DarkPhantomEntity.this.getYaw() + 90.0f);
                float m = MathHelper.wrapDegrees(k * 57.295776f);
                DarkPhantomEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0f) - 90.0f);
                DarkPhantomEntity.this.bodyYaw = DarkPhantomEntity.this.getYaw();
                this.targetSpeed = MathHelper.angleBetween(j, DarkPhantomEntity.this.getYaw()) < 3.0f ? MathHelper.stepTowards(this.targetSpeed, 1.8f, 0.005f * (1.8f / this.targetSpeed)) : MathHelper.stepTowards(this.targetSpeed, 0.2f, 0.025f);
                float n = (float)(-(MathHelper.atan2(-e, g) * 57.2957763671875));
                DarkPhantomEntity.this.setPitch(n);
                float o = DarkPhantomEntity.this.getYaw() + 90.0f;
                double p = (double)(this.targetSpeed * MathHelper.cos(o * ((float)Math.PI / 180))) * Math.abs(d / i);
                double q = (double)(this.targetSpeed * MathHelper.sin(o * ((float)Math.PI / 180))) * Math.abs(f / i);
                double r = (double)(this.targetSpeed * MathHelper.sin(n * ((float)Math.PI / 180))) * Math.abs(e / i);
                Vec3d vec3d = DarkPhantomEntity.this.getVelocity();
                DarkPhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
            }
        }
    }
    class PhantomLookControl
    extends LookControl {
        public PhantomLookControl(MobEntity entity) {
            super(entity);
        }
        @Override
        public void tick() {
        }
    }
    class PhantomBodyControl
    extends BodyControl {
        public PhantomBodyControl(MobEntity entity) {
            super(entity);
        }
        @Override
        public void tick() {
            DarkPhantomEntity.this.headYaw = DarkPhantomEntity.this.bodyYaw;
            DarkPhantomEntity.this.bodyYaw = DarkPhantomEntity.this.getYaw();
        }
    }
    class StartAttackGoal
    extends Goal {
        private int cooldown;
        StartAttackGoal() {
        }
        @Override
        public boolean canStart() {
            LivingEntity livingEntity = DarkPhantomEntity.this.getTarget();
            if (livingEntity != null) {
                return DarkPhantomEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT);
            }
            return false;
        }
        @Override
        public void start() {
            this.cooldown = this.getTickCount(10);
            DarkPhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            this.startSwoop();
        }
        @Override
        public void stop() {
            DarkPhantomEntity.this.circlingCenter = DarkPhantomEntity.this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, DarkPhantomEntity.this.circlingCenter).up(10 + DarkPhantomEntity.this.random.nextInt(20));
        }
        @Override
        public void tick() {
            if (DarkPhantomEntity.this.movementType == PhantomMovementType.CIRCLE) {
                --this.cooldown;
                if (this.cooldown <= 0) {
                    DarkPhantomEntity.this.movementType = PhantomMovementType.SWOOP;
                    this.startSwoop();
                    this.cooldown = this.getTickCount((8 + DarkPhantomEntity.this.random.nextInt(4)) * 20);
                    DarkPhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + DarkPhantomEntity.this.random.nextFloat() * 0.1f);
                }
            }
        }
        private void startSwoop() {
            DarkPhantomEntity.this.circlingCenter = DarkPhantomEntity.this.getTarget().getBlockPos().up(20 + DarkPhantomEntity.this.random.nextInt(20));
            if (DarkPhantomEntity.this.circlingCenter.getY() < DarkPhantomEntity.this.getWorld().getSeaLevel()) {
                DarkPhantomEntity.this.circlingCenter = new BlockPos(DarkPhantomEntity.this.circlingCenter.getX(), DarkPhantomEntity.this.getWorld().getSeaLevel() + 1, DarkPhantomEntity.this.circlingCenter.getZ());
            }
        }
    }
    class SwoopMovementGoal
    extends MovementGoal {
        private static final int CAT_CHECK_INTERVAL = 20;
        private boolean catsNearby;
        private int nextCatCheckAge;
        SwoopMovementGoal() {
        }
        @Override
        public boolean canStart() {
            return DarkPhantomEntity.this.getTarget() != null && DarkPhantomEntity.this.movementType == PhantomMovementType.SWOOP;
        }
        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = DarkPhantomEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!livingEntity.isAlive()) {
                return false;
            }
            if (!this.canStart()) {
                return false;
            }
            if (DarkPhantomEntity.this.age > this.nextCatCheckAge) {
                this.nextCatCheckAge = DarkPhantomEntity.this.age + 20;
                List<CatEntity> list = DarkPhantomEntity.this.getWorld().getEntitiesByClass(CatEntity.class, DarkPhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
                for (CatEntity catEntity : list) {
                    catEntity.hiss();
                }
                this.catsNearby = !list.isEmpty();
            }
            return !this.catsNearby;
        }
        @Override
        public void start() {
        }
        @Override
        public void stop() {
            DarkPhantomEntity.this.setTarget(null);
            DarkPhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
        }
        @Override
        public void tick() {
            LivingEntity livingEntity = DarkPhantomEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            DarkPhantomEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
            if (DarkPhantomEntity.this.getBoundingBox().expand(0.2f).intersects(livingEntity.getBoundingBox())) {
                DarkPhantomEntity.this.tryAttack(livingEntity);
                DarkPhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
                if (!DarkPhantomEntity.this.isSilent()) {
                    DarkPhantomEntity.this.getWorld().syncWorldEvent(WorldEvents.PHANTOM_BITES, DarkPhantomEntity.this.getBlockPos(), 0);
                }
            } else if (DarkPhantomEntity.this.horizontalCollision || DarkPhantomEntity.this.hurtTime > 0) {
                DarkPhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            }
        }
    }
    class CircleMovementGoal
    extends MovementGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float circlingDirection;
        CircleMovementGoal() {
        }
        @Override
        public boolean canStart() {
            return DarkPhantomEntity.this.getTarget() == null || DarkPhantomEntity.this.movementType == PhantomMovementType.CIRCLE;
        }
        @Override
        public void start() {
            this.radius = 5.0f + DarkPhantomEntity.this.random.nextFloat() * 10.0f;
            this.yOffset = -4.0f + DarkPhantomEntity.this.random.nextFloat() * 9.0f;
            this.circlingDirection = DarkPhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f;
            this.adjustDirection();
        }
        @Override
        public void tick() {
            if (DarkPhantomEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
                this.yOffset = -4.0f + DarkPhantomEntity.this.random.nextFloat() * 9.0f;
            }
            if (DarkPhantomEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
                this.radius += 1.0f;
                if (this.radius > 15.0f) {
                    this.radius = 5.0f;
                    this.circlingDirection = -this.circlingDirection;
                }
            }
            if (DarkPhantomEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
                this.angle = DarkPhantomEntity.this.random.nextFloat() * 2.0f * (float)Math.PI;
                this.adjustDirection();
            }
            if (this.isNearTarget()) {
                this.adjustDirection();
            }
            if (DarkPhantomEntity.this.targetPosition.y < DarkPhantomEntity.this.getY() && !DarkPhantomEntity.this.getWorld().isAir(DarkPhantomEntity.this.getBlockPos().down(1))) {
                this.yOffset = Math.max(1.0f, this.yOffset);
                this.adjustDirection();
            }
            if (DarkPhantomEntity.this.targetPosition.y > DarkPhantomEntity.this.getY() && !DarkPhantomEntity.this.getWorld().isAir(DarkPhantomEntity.this.getBlockPos().up(1))) {
                this.yOffset = Math.min(-1.0f, this.yOffset);
                this.adjustDirection();
            }
        }
        private void adjustDirection() {
            if (BlockPos.ORIGIN.equals(DarkPhantomEntity.this.circlingCenter)) {
                DarkPhantomEntity.this.circlingCenter = DarkPhantomEntity.this.getBlockPos();
            }
            this.angle += this.circlingDirection * 15.0f * ((float)Math.PI / 180);
            DarkPhantomEntity.this.targetPosition = Vec3d.of(DarkPhantomEntity.this.circlingCenter).add(this.radius * MathHelper.cos(this.angle), -4.0f + this.yOffset, this.radius * MathHelper.sin(this.angle));
        }
    }
    abstract class MovementGoal
    extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }
        protected boolean isNearTarget() {
            return DarkPhantomEntity.this.targetPosition.squaredDistanceTo(DarkPhantomEntity.this.getX(), DarkPhantomEntity.this.getY(), DarkPhantomEntity.this.getZ()) < 4.0;
        }
    }
}


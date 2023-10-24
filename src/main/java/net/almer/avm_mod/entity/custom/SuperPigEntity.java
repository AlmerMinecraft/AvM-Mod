package net.almer.avm_mod.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;
import java.util.UUID;

public class SuperPigEntity extends PathAwareEntity implements Angerable{
    private int movementCooldownTicks;
    private static LivingEntity targeted;
    private static int jumpTick;
    Random rand = new Random();
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private int angerTime;
    @Nullable
    private UUID angryAt;
    public SuperPigEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AttackGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
    }
    public static DefaultAttributeContainer createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_ARMOR, 2).build();
    }
    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.isAlive()) {
            return;
        }
        if (this.horizontalCollision && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            Box box = this.getBoundingBox().expand(0.2);
            boolean bl = false;
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
        if(targeted != null) {
            if (targeted.getY() >= this.getY() + 5 && this.isOnGround()) {
                Vec3d vec3d = this.getVelocity();
                this.setVelocity(vec3d.x, this.getJumpVelocity() * 5, vec3d.z);
                if (this.isSprinting()) {
                    float f = this.getYaw() * ((float) Math.PI / 180);
                    this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
                }
                this.velocityDirty = true;
            }
        }
    }
    @Override
    public boolean tryAttack(Entity target) {
        if (!(target instanceof LivingEntity)) {
            return false;
        }
        this.targeted = (LivingEntity)target;
        if(target.getY() >= this.getY() + 5 && this.isOnGround()){
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, this.getJumpVelocity() * 5, vec3d.z);
            if (this.isSprinting()) {
                float f = this.getYaw() * ((float)Math.PI / 180);
                this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            }
            this.velocityDirty = true;
        }
        this.movementCooldownTicks = 10;
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        return this.tryAttack(this, (LivingEntity)target);
    }
    public boolean tryAttack(LivingEntity attacker, LivingEntity target) {
        targeted = target;
        float f;
        if(!attacker.isOnGround()){
            f = 1f;
        }
        else{
            f = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        }
        float g = !attacker.isBaby() && (int)f > 0 ? f / 2.0f + (float)attacker.getWorld().random.nextInt((int)f) : f;
        boolean bl = target.damage(attacker.getDamageSources().mobAttack(attacker), g);
        if (bl) {
            attacker.applyDamageEffects(attacker, target);
            if (!attacker.isBaby()) {
                this.knockback(attacker, target);
            }
        }
        return bl;
    }
    public void knockback(LivingEntity attacker, LivingEntity target) {
        if(target.getY() >= this.getY() + 5 && this.isOnGround()){
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, this.getJumpVelocity() * 5, vec3d.z);
            if (this.isSprinting()) {
                float f = this.getYaw() * ((float)Math.PI / 180);
                this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            }
            this.velocityDirty = true;
        }
        double e;
        double d = attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        double f = d - (e = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        if (f <= 0.0) {
            return;
        }
        double g = target.getX() - attacker.getX();
        double h = target.getZ() - attacker.getZ();
        float i = attacker.getWorld().random.nextInt(21) - 10;
        double j = f * (double) (attacker.getWorld().random.nextFloat() * 0.5f + 0.2f);
        Vec3d vec3d = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
        double k = f * (double) attacker.getWorld().random.nextFloat() * 0.8;
        target.addVelocity(vec3d.x, k, vec3d.z);
        target.velocityModified = true;
    }
    @Override
    public void tick() {
        this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + rand.nextFloat(), this.getY() + rand.nextFloat(), this.getZ() + rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        super.tick();
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isOf(DamageTypes.FALL)){
            return false;
        }
        if(source.isOf(DamageTypes.IN_FIRE)){
            return false;
        }
        return super.damage(source, amount);
    }
}

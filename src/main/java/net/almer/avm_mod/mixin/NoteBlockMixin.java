package net.almer.avm_mod.mixin;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LightningEntity.class)
public abstract class NoteBlockMixin extends Entity {
    public NoteBlockMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    private BlockPos getAffectedBlockPos() {
        Vec3d vec3d = this.getPos();
        return BlockPos.ofFloored(vec3d.x, vec3d.y - 1.0E-6, vec3d.z);
    }
    private void cooldown(int duration, BlockPos pos){
        double ticking = this.getWorld().getTime();
        while((this.getWorld().getTime() - ticking) == duration){
            ItemEntity entity = new ItemEntity(this.getWorld(), pos.getX(), pos.getY(), pos.getZ(), ModItem.GUITAR.getDefaultStack());
            entity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
            this.getWorld().spawnEntity(entity);
        }
    }
    private void powerNoteBlock() {
        BlockPos blockPos = BlockPos.ofFloored(this.getAffectedBlockPos().getX(), this.getAffectedBlockPos().getY() - 1, this.getAffectedBlockPos().getZ());
        BlockPos blockPos1 = this.getAffectedBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        BlockState blockState1 = this.getWorld().getBlockState(blockPos1);
        if (blockState1.isOf(Blocks.NOTE_BLOCK)) {
            this.getWorld().setBlockState(blockPos1, ModBlock.CHARGED_NOTE_BLOCK.getDefaultState());
            cooldown(10, blockPos);
        }
        else if(blockState.isOf(Blocks.NOTE_BLOCK)){
            this.getWorld().setBlockState(blockPos, ModBlock.CHARGED_NOTE_BLOCK.getDefaultState());
        }
    }
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;powerLightningRod()V"))
    public void tick(CallbackInfo ci){
        this.powerNoteBlock();
    }
}

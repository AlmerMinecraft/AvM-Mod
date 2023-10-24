package net.almer.avm_mod.mixin;

import net.almer.avm_mod.block.ModBlock;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends AbstractBlock {
    public NoteBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos;
        if (world.isThundering() && projectile instanceof TridentEntity && ((TridentEntity) projectile).hasChanneling() && world.isSkyVisible(blockPos = hit.getBlockPos())) {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            if (lightningEntity != null) {
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos.up()));
                Entity entity = projectile.getOwner();
                lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null);
                world.spawnEntity(lightningEntity);
            }
            world.playSound(null, blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0f, 1.0f);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(entity instanceof LightningEntity){
            world.setBlockState(pos, ModBlock.CHARGED_NOTE_BLOCK.getDefaultState());
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}

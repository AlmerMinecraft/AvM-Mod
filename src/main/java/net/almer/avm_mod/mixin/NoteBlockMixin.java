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

import java.util.List;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block {
    public NoteBlockMixin(Settings settings) {
        super(settings);
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Box box = new Box(pos).expand(0.5);
        List projectiles = world.getEntitiesByClass(ProjectileEntity.class, box, e->e.isAlive());
        List entityList = world.getEntitiesByClass(LightningEntity.class, box, e->e.isAlive());
        if(!entityList.isEmpty()){
            world.setBlockState(pos, ModBlock.CHARGED_NOTE_BLOCK.getDefaultState());
        }
        if(!projectiles.isEmpty()) {
            for (int j = 0; j < projectiles.size(); j++) {
                ProjectileEntity projectile = (ProjectileEntity)projectiles.get(j);
                if (world.isThundering() && projectile instanceof TridentEntity && ((TridentEntity) projectile).hasChanneling() && world.isSkyVisible(pos)) {
                    LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                    if (lightningEntity != null) {
                        lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos.up()));
                        Entity entity = projectile.getOwner();
                        lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null);
                        world.spawnEntity(lightningEntity);
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0f, 1.0f);
                }
            }
        }
    }
}

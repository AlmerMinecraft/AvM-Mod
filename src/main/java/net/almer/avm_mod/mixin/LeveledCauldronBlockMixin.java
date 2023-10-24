package net.almer.avm_mod.mixin;

import net.almer.avm_mod.block.ModBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeveledCauldronBlock.class)
public class LeveledCauldronBlockMixin {
    private static int chick = 0;
    private static int mush = 0;
    private static int kelp = 0;
    private static int count;
    @Inject(method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info){
        if(!world.isClient){
            if(entity instanceof ItemEntity itemEntity && itemEntity.getStack().isOf(Items.CHICKEN) && !(chick > 0)){
                LeveledCauldronBlockMixin.count = itemEntity.getStack().getCount();
                LeveledCauldronBlockMixin.chick += count;
                entity.discard();
            }
            if(entity instanceof ItemEntity itemEntity && itemEntity.getStack().isOf(Items.BROWN_MUSHROOM) && !(mush > 3)){
                LeveledCauldronBlockMixin.count = itemEntity.getStack().getCount();
                LeveledCauldronBlockMixin.mush += count;
                entity.discard();
            }
            if(entity instanceof ItemEntity itemEntity && itemEntity.getStack().isOf(Items.KELP) && !(kelp > 2)){
                LeveledCauldronBlockMixin.count = itemEntity.getStack().getCount();
                LeveledCauldronBlockMixin.kelp += count;
                entity.discard();
            }
            if(chick >= 1 && mush >= 3 && kelp >= 2){
                world.setBlockState(pos, ModBlock.BROTH_CAULDRON.getDefaultState());
                LeveledCauldronBlockMixin.chick = 0;
                LeveledCauldronBlockMixin.mush = 0;
                LeveledCauldronBlockMixin.kelp = 0;
            }
        }
    }
}

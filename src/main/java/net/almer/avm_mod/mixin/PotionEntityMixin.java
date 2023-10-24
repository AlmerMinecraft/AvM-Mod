package net.almer.avm_mod.mixin;

import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.LivingBrewingStandEntity;
import net.almer.avm_mod.entity.custom.LivingChestEntity;
import net.almer.avm_mod.entity.custom.LivingFurnaceEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    List<BlockPos> listBlock = new ArrayList<>(150);
    @Inject(method = "onCollision(Lnet/minecraft/util/hit/HitResult;)V", at = @At("TAIL"))
    protected void onCollision(HitResult hit, CallbackInfo ci){
        BlockPos pos = ((PotionEntity)(Object)this).getBlockPos();
        World world = ((PotionEntity)(Object)this).getWorld();
        ItemStack itemStack = ((PotionEntity)(Object)this).getStack();
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        for(StatusEffectInstance instance : list) {
            if (instance.getEffectType() == ModEffect.AWAKENING) {
                for (int i = -2; i < 2; i++) {
                    int posX = pos.getX() + i;
                    for (int j = -2; j < 2; j++) {
                        int posZ = pos.getZ() + j;
                        for (int k = -1; k < 1; k++) {
                            int posY = pos.getY() + k;
                            listBlock.add(BlockPos.ofFloored(posX, posY, posZ));
                            for (int b = 0; b < listBlock.size(); b++) {
                                if (world.getBlockState(listBlock.get(b)).getBlock() == Blocks.CHEST) {
                                    world.setBlockState(listBlock.get(b), Blocks.AIR.getDefaultState());
                                    LivingChestEntity entity = new LivingChestEntity(ModEntities.LIVING_CHEST, world);
                                    entity.setPos(posX, posY, posZ);
                                    world.spawnEntity(entity);
                                }
                                if (world.getBlockState(listBlock.get(b)).getBlock() == Blocks.BREWING_STAND) {
                                    world.setBlockState(listBlock.get(b), Blocks.AIR.getDefaultState());
                                    LivingBrewingStandEntity entity = new LivingBrewingStandEntity(ModEntities.LIVING_BREWING_STAND, world);
                                    entity.setPos(posX, posY, posZ);
                                    world.spawnEntity(entity);
                                }
                                if (world.getBlockState(listBlock.get(b)).getBlock() == Blocks.FURNACE) {
                                    world.setBlockState(listBlock.get(b), Blocks.AIR.getDefaultState());
                                    LivingFurnaceEntity entity = new LivingFurnaceEntity(ModEntities.LIVING_FURNACE, world);
                                    entity.setPos(posX, posY, posZ);
                                    world.spawnEntity(entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

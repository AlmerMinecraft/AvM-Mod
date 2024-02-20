package net.almer.avm_mod.block.custom;

import com.mojang.serialization.MapCodec;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.mixin.LeveledCauldronBlockMixin;
import net.almer.avm_mod.util.BrothCauldronBehaviour;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OilCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<OilCauldronBlock> CODEC = OilCauldronBlock.createCodec(OilCauldronBlock::new);
    public OilCauldronBlock(Settings settings) {
        super(settings, BrothCauldronBehaviour.OIL_CAULDRON_BEHAVIOR);
    }
    @Override
    public boolean isFull(BlockState state) {
        return true;
    }
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            if (world.getBlockState(pos.down(1)) == Blocks.FIRE.getDefaultState()) {
                if (entity instanceof ItemEntity itemEntity && itemEntity.getStack().isOf(ModItem.BREADED_CHICKEN)) {
                    itemEntity.setStack(ModItem.FRIED_CHICKEN.getDefaultStack());
                }
            }
        }
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.getStackInHand(hand).isOf(ModItem.BATTER)){
            player.giveItemStack(ModItem.DONUT.getDefaultStack());
            player.getStackInHand(hand).decrement(1);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}

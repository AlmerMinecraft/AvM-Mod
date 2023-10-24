package net.almer.avm_mod.item.custom;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BatterBucketItem extends Item {
    private final Fluid fluid;
    public BatterBucketItem(Fluid fluid, Settings settings) {
        super(settings);
        this.fluid = fluid;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = BucketItem.raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }
        if(blockHitResult.getType() == HitResult.Type.BLOCK){
            BlockPos pos = blockHitResult.getBlockPos();
            if(world.getBlockState(pos) == Blocks.CAULDRON.getDefaultState()){
                ItemStack itemStack1 = Items.BUCKET.getDefaultStack();
                user.setStackInHand(hand, itemStack1);
                user.giveItemStack(ModItem.DONUT.getDefaultStack());
                if(user.getAbilities().creativeMode){
                    user.setStackInHand(hand, ModItem.BATTER_BUCKET.getDefaultStack());
                }
                return TypedActionResult.success(itemStack1, world.isClient());
            }
        }
        return TypedActionResult.pass(itemStack);
    }
}

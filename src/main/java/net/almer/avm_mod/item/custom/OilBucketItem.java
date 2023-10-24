package net.almer.avm_mod.item.custom;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class OilBucketItem extends Item {
    private final Fluid fluid;
    public OilBucketItem(Fluid fluid, Item.Settings settings) {
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
                if(user.getAbilities().creativeMode){
                    user.setStackInHand(hand, ModItem.OIL_BUCKET.getDefaultStack());
                }
                world.setBlockState(pos, ModBlock.OIL_CAULDRON.getDefaultState());
                return TypedActionResult.success(itemStack1, world.isClient());
            }
        }
        return TypedActionResult.pass(itemStack);
    }
}

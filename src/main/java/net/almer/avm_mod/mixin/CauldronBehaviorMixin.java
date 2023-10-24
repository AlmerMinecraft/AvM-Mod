package net.almer.avm_mod.mixin;

import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(method = "registerBehavior()V", at = @At("HEAD"))
    private static void registerBehavior(CallbackInfo info){
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ModItem.RAW_NOODLES, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                ItemStack item = new ItemStack(ModItem.NOODLES);
                player.setStackInHand(hand, item);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }
}

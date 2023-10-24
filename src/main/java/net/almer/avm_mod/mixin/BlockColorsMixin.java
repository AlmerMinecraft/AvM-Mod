package net.almer.avm_mod.mixin;

import net.almer.avm_mod.block.ModBlock;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "create", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void cauldron_dyeing$modifyCreate(CallbackInfoReturnable<BlockColors> cir, BlockColors blockColors) {
        blockColors.registerColorProvider((state, world, pos, tintIndex) ->
                world != null && pos != null ? 0xBe5d25 : -1, ModBlock.BROTH_CAULDRON);
        blockColors.registerColorProvider((state, world, pos, tintIndex) ->
                world != null && pos != null ? 0xC7a91a : -1, ModBlock.OIL_CAULDRON);
    }
}

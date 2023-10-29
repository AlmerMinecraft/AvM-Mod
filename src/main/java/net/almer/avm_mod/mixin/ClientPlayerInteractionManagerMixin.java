package net.almer.avm_mod.mixin;

import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.item.custom.GameIconItem;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(at = @At("HEAD"), method = "getReachDistance()F", cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> ci)
    {
        if((GameIconItem.MODE_INDEX == 0 && (GameIconItem.getPlayer().getMainHandStack().isOf(ModItem.GAME_ICON) || GameIconItem.getPlayer().getOffHandStack().isOf(ModItem.GAME_ICON))) ||
                (PowerfulStaffItem.currentMode == 0 && PowerfulStaffItem.getPlayer().getMainHandStack().isOf(ModItem.POWERFUL_STAFF))) {
            ci.setReturnValue(15.0f);
        }
        else{
            ci.setReturnValue(4.5f);
        }
    }
}

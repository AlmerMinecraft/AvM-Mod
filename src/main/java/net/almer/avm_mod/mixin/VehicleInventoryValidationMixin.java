package net.almer.avm_mod.mixin;

import net.almer.avm_mod.AvMMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VehicleInventory.class)
interface VehicleInventoryValidationMixin {
    @ModifyConstant(
        method = "canPlayerAccess(Lnet/minecraft/entity/player/PlayerEntity;)Z",
        require = 1, allow = 1, constant = @Constant(doubleValue = 8.0))
    private static double getActualReachDistance(final double reachDistance, final PlayerEntity player) {
        return AvMMod.getReachDistance(player, reachDistance);
    }
}

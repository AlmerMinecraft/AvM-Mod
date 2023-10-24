package net.almer.avm_mod.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = StructureBlockBlockEntity.class, priority = 999)
public class StructureBlockUnlimit {

    @ModifyConstant(method = "readNbt", constant = @Constant(intValue = 48), require = 0)
    public int readNbtUpper(int value) {
        return 512;
    }

    @ModifyConstant(method = "readNbt", constant = @Constant(intValue = -48), require = 0)
    public int readNbtLower(int value) {
        return -512;
    }

    @ModifyConstant(method = "detectStructureSize", constant = @Constant(intValue = 80), require = 0)
    public int detectSize(int value) {
        return 256;
    }
}

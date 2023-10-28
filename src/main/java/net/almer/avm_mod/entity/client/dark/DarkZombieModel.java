package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkZombieEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.entity.mob.MobEntity;

public class DarkZombieModel<T extends DarkZombieEntity> extends BipedEntityModel<T> {
    protected DarkZombieModel(ModelPart modelPart) {
        super(modelPart);
    }
    public static TexturedModelData getTexturedModelData(){
        return TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0f), 64, 64);
    }
    @Override
    public void setAngles(T hostileEntity, float f, float g, float h, float i, float j) {
        super.setAngles(hostileEntity, f, g, h, i, j);
        CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, this.isAttacking(hostileEntity), this.handSwingProgress, h);
    }
    public boolean isAttacking(T zombieEntity) {
        return ((MobEntity)zombieEntity).isAttacking();
    }
}

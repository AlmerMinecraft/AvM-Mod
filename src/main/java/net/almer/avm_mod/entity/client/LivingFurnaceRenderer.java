package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.entity.custom.LivingFurnaceEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class LivingFurnaceRenderer extends MobEntityRenderer<LivingFurnaceEntity, LivingFurnaceModel> {
    public LivingFurnaceRenderer(EntityRendererFactory.Context context) {
        super(context, new LivingFurnaceModel(context.getPart(AvMModClient.MODEL_FURNACE_LAYER)), 1f);
    }

    @Override
    public Identifier getTexture(LivingFurnaceEntity entity) {
        return new Identifier("avm_mod", "textures/entity/living_furnace.png");
    }
}

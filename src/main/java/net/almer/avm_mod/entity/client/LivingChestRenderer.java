package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.entity.custom.LivingChestEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class LivingChestRenderer extends MobEntityRenderer<LivingChestEntity, LivingChestModel> {
    public LivingChestRenderer(EntityRendererFactory.Context context) {
        super(context, new LivingChestModel(context.getPart(AvMModClient.MODEL_CHEST_LAYER)), 1f);
    }

    @Override
    public Identifier getTexture(LivingChestEntity entity) {
        return new Identifier("avm_mod", "textures/entity/living_chest.png");
    }
}

package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.entity.custom.LivingBrewingStandEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class LivingBrewingStandRenderer extends MobEntityRenderer<LivingBrewingStandEntity, LivingBrewingStandModel> {
    public LivingBrewingStandRenderer(EntityRendererFactory.Context context) {
        super(context, new LivingBrewingStandModel(context.getPart(AvMModClient.MODEL_BREWING_STAND_LAYER)), 1f);
    }

    @Override
    public Identifier getTexture(LivingBrewingStandEntity entity) {
        return new Identifier("avm_mod", "textures/entity/living_brewing.png");
    }
}

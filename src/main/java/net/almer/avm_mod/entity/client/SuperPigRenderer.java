package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.entity.custom.SuperPigEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class SuperPigRenderer extends MobEntityRenderer<SuperPigEntity, PigEntityModel<SuperPigEntity>>{
        private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

    public SuperPigRenderer(EntityRendererFactory.Context context) {
        super(context, new PigEntityModel(context.getPart(EntityModelLayers.PIG)), 0.7f);
    }

    @Override
    public Identifier getTexture(SuperPigEntity entity) {
        return TEXTURE;
    }
}

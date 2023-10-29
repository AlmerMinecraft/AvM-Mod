/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkPhantomEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class DarkPhantomRenderer extends MobEntityRenderer<DarkPhantomEntity, DarkPhantomModel<DarkPhantomEntity>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod","textures/entity/dark_phantom.png");
    public DarkPhantomRenderer(EntityRendererFactory.Context context) {
        super(context, new DarkPhantomModel(context.getPart(EntityModelLayers.PHANTOM)), 0.75f);
    }
    @Override
    public Identifier getTexture(DarkPhantomEntity phantomEntity) {
        return TEXTURE;
    }
    @Override
    protected void scale(DarkPhantomEntity phantomEntity, MatrixStack matrixStack, float f) {
        float g = 1.0f + 0.15f * (float)0;
        matrixStack.scale(g, g, g);
        matrixStack.translate(0.0f, 1.3125f, 0.1875f);
    }
    @Override
    protected void setupTransforms(DarkPhantomEntity phantomEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(phantomEntity, matrixStack, f, g, h);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(phantomEntity.getPitch()));
    }
}


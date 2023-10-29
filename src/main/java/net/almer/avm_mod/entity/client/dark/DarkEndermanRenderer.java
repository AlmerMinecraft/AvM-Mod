/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkEndermanEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class DarkEndermanRenderer extends MobEntityRenderer<DarkEndermanEntity, DarkEndermanModel<DarkEndermanEntity>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod","textures/entity/dark_enderman.png");
    private final Random random = Random.create();
    public DarkEndermanRenderer(EntityRendererFactory.Context context) {
        super(context, new DarkEndermanModel(context.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
    }
    @Override
    public void render(DarkEndermanEntity endermanEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        DarkEndermanModel endermanEntityModel = (DarkEndermanModel)this.getModel();
        endermanEntityModel.angry = endermanEntity.isAngry();
        super.render(endermanEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
    @Override
    public Vec3d getPositionOffset(DarkEndermanEntity endermanEntity, float f) {
        if (endermanEntity.isAngry()) {
            double d = 0.02;
            return new Vec3d(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
        }
        return super.getPositionOffset(endermanEntity, f);
    }
    @Override
    public Identifier getTexture(DarkEndermanEntity endermanEntity) {
        return TEXTURE;
    }
}


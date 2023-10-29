/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkCreeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DarkCreeperRenderer extends MobEntityRenderer<DarkCreeperEntity, DarkCreeperModel<DarkCreeperEntity>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod","textures/entity/dark_creeper.png");

    public DarkCreeperRenderer(EntityRendererFactory.Context context) {
        super(context, new DarkCreeperModel(context.getPart(EntityModelLayers.CREEPER)), 0.5f);
        this.addFeature(new ModCreeperChargeFeatureRenderer(this, context.getModelLoader()));
    }
    @Override
    public Identifier getTexture(DarkCreeperEntity entity) {
        return TEXTURE;
    }
    @Override
    protected void scale(DarkCreeperEntity creeperEntity, MatrixStack matrixStack, float f) {
        float g = creeperEntity.getClientFuseTime(f);
        float h = 1.0f + MathHelper.sin(g * 100.0f) * g * 0.01f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g *= g;
        g *= g;
        float i = (1.0f + g * 0.4f) * h;
        float j = (1.0f + g * 0.1f) / h;
        matrixStack.scale(i, j, i);
    }
    @Override
    protected float getAnimationCounter(DarkCreeperEntity creeperEntity, float f) {
        float g = creeperEntity.getClientFuseTime(f);
        if ((int)(g * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return MathHelper.clamp(g, 0.5f, 1.0f);
    }
    class ModCreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<DarkCreeperEntity, DarkCreeperModel<DarkCreeperEntity>> {
        private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
        private final DarkCreeperModel<DarkCreeperEntity> model;
        public ModCreeperChargeFeatureRenderer(FeatureRendererContext<DarkCreeperEntity, DarkCreeperModel<DarkCreeperEntity>> context, EntityModelLoader loader) {
            super(context);
            this.model = new DarkCreeperModel(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
        }
        @Override
        protected float getEnergySwirlX(float partialAge) {
            return partialAge * 0.01f;
        }
        @Override
        protected Identifier getEnergySwirlTexture() {
            return SKIN;
        }
        @Override
        protected EntityModel<DarkCreeperEntity> getEnergySwirlModel() {
            return this.model;
        }
    }
}


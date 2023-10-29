/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkSpiderEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DarkSpiderRenderer<T extends DarkSpiderEntity> extends MobEntityRenderer<T, DarkSpiderModel<T>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod","textures/entity/dark_spider.png");
    public DarkSpiderRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SPIDER);
    }
    public DarkSpiderRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new DarkSpiderModel(ctx.getPart(layer)), 0.8f);
    }
    @Override
    protected float getLyingAngle(T spiderEntity) {
        return 180.0f;
    }
    @Override
    public Identifier getTexture(T spiderEntity) {
        return TEXTURE;
    }
}


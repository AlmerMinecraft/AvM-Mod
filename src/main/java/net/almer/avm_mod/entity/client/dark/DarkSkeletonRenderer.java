package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkSkeletonEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class DarkSkeletonRenderer extends BipedEntityRenderer<DarkSkeletonEntity, DarkSkeletonModel<DarkSkeletonEntity>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod", "textures/entity/dark_skeleton.png");
    public DarkSkeletonRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DarkSkeletonModel(ctx.getPart(EntityModelLayers.SKELETON)), 0.5f);
    }
    @Override
    public Identifier getTexture(DarkSkeletonEntity entity) {
        return TEXTURE;
    }
}

package net.almer.avm_mod.entity.client.dark;

import net.almer.avm_mod.entity.custom.dark.DarkZombieEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class DarkZombieRenderer extends BipedEntityRenderer<DarkZombieEntity, DarkZombieModel<DarkZombieEntity>> {
    private static final Identifier TEXTURE = new Identifier("avm_mod", "textures/entity/dark_zombie.png");
    public DarkZombieRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DarkZombieModel(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
    }
    @Override
    public Identifier getTexture(DarkZombieEntity zombieEntity) {
        return TEXTURE;
    }
}

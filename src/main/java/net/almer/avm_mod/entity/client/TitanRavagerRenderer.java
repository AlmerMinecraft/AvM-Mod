package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.entity.custom.LivingBrewingStandEntity;
import net.almer.avm_mod.entity.custom.TitanRavagerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class TitanRavagerRenderer extends MobEntityRenderer<TitanRavagerEntity, TitanRavagerModel> {
    public TitanRavagerRenderer(EntityRendererFactory.Context context) {
        super(context, new TitanRavagerModel(context.getPart(AvMModClient.TITAN_RAVAGER_LAYER)), 1f);
    }

    @Override
    public Identifier getTexture(TitanRavagerEntity entity) {
        return new Identifier("avm_mod", "textures/entity/titan_ravager.png");
    }
}

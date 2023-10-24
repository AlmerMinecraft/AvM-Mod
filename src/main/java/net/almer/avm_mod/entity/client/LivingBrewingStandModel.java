package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.entity.custom.LivingBrewingStandEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class LivingBrewingStandModel extends EntityModel<LivingBrewingStandEntity> {
	private final ModelPart bb_main;
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;
	public LivingBrewingStandModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
		this.cube_r1 = root.getChild("bb_main");
		this.cube_r2 = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F))
		.uv(8, 0).cuboid(1.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F))
		.uv(8, 0).cuboid(-7.0F, -2.0F, -7.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F))
		.uv(8, 0).cuboid(-7.0F, -2.0F, 1.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F))
		.uv(0, 18).cuboid(1.0F, -16.0F, 0.0F, 5.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(5, 18).cuboid(-6.4F, -16.0F, 0.0F, 5.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(5, 18).cuboid(-6.4F, -16.0F, 0.0F, 5.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(LivingBrewingStandEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
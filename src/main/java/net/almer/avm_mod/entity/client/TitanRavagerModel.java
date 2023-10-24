package net.almer.avm_mod.entity.client;

import net.almer.avm_mod.entity.custom.TitanRavagerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class TitanRavagerModel extends EntityModel<TitanRavagerEntity> {
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart horns;
	private final ModelPart leg0;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	public TitanRavagerModel(ModelPart root) {
		this.body = root.getChild("body");
		this.neck = root.getChild("neck");
		this.leg0 = root.getChild("leg0");
		this.leg1 = root.getChild("leg1");
		this.leg2 = root.getChild("leg2");
		this.leg3 = root.getChild("leg3");
		this.head = neck.getChild("head");
		this.mouth = head.getChild("mouth");
		this.horns = head.getChild("horns");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 220).cuboid(-28.0F, -28.0F, -16.0F, 56.0F, 64.0F, 80.0F, new Dilation(0.0F))
		.uv(0, 392).cuboid(-24.0F, 36.0F, -16.0F, 48.0F, 52.0F, 54.4F, new Dilation(0.0F)), ModelTransform.of(0.0F, -52.0F, 8.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData neck = modelPartData.addChild("neck", ModelPartBuilder.create().uv(269, 284).cuboid(-20.0F, -44.0F, 40.0F, 40.0F, 40.0F, 72.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -56.0F, -80.0F));

		ModelPartData head = neck.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-32.0F, -24.0F, -56.0F, 64.0F, 80.0F, 64.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, 22.4F, -72.0F, 16.0F, 32.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -32.0F, 40.0F));

		ModelPartData mouth = head.addChild("mouth", ModelPartBuilder.create().uv(0, 144).cuboid(-32.0F, -4.0F, -56.0F, 64.0F, 12.0F, 64.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 52.0F, 0.0F));

		ModelPartData horns = head.addChild("horns", ModelPartBuilder.create().uv(288, 212).cuboid(-20.0F, -56.0F, -4.0F, 8.0F, 56.0F, 16.0F, new Dilation(0.0F))
		.uv(288, 212).cuboid(52.0F, -56.0F, -4.0F, 8.0F, 56.0F, 16.0F, new Dilation(0.0F))
		.uv(291, 214).cuboid(-18.8F, -82.0F, -2.8F, 5.6F, 61.6F, 13.6F, new Dilation(-0.4F))
		.uv(291, 215).cuboid(53.2F, -82.0F, -2.8F, 5.6F, 61.6F, 13.6F, new Dilation(-0.4F)), ModelTransform.of(-20.0F, 15.2F, -10.4F, 1.2217F, 0.0F, 0.0F));

		ModelPartData leg0 = modelPartData.addChild("leg0", ModelPartBuilder.create().uv(372, 0).cuboid(0.0F, -7.2F, -20.0F, 32.0F, 60.0F, 32.0F, new Dilation(0.0F))
		.uv(352, 67).cuboid(-2.4F, 50.0F, -22.4F, 36.8F, 72.4F, 36.8F, new Dilation(0.8F)), ModelTransform.pivot(-48.0F, -96.0F, 88.0F));

		ModelPartData leg1 = modelPartData.addChild("leg1", ModelPartBuilder.create().uv(253, 0).cuboid(0.0F, -7.2F, -20.0F, 32.0F, 60.0F, 32.0F, new Dilation(0.0F))
		.uv(253, 67).cuboid(-2.4F, 50.0F, -22.4F, 36.8F, 72.4F, 36.8F, new Dilation(0.8F)), ModelTransform.pivot(16.0F, -96.0F, 88.0F));

		ModelPartData leg2 = modelPartData.addChild("leg2", ModelPartBuilder.create().uv(253, 0).cuboid(-32.0F, -44.0F, -16.0F, 32.0F, 72.8F, 32.0F, new Dilation(0.0F))
		.uv(353, 59).cuboid(-34.4F, 26.4F, -18.4F, 36.8F, 80.0F, 36.8F, new Dilation(0.8F)), ModelTransform.pivot(-16.0F, -80.0F, -16.0F));

		ModelPartData leg3 = modelPartData.addChild("leg3", ModelPartBuilder.create().uv(372, 0).cuboid(32.0F, -44.0F, -16.0F, 32.0F, 72.8F, 32.0F, new Dilation(0.0F))
		.uv(256, 59).cuboid(29.6F, 26.4F, -18.4F, 36.8F, 80.0F, 36.8F, new Dilation(0.8F)), ModelTransform.pivot(-16.0F, -80.0F, -16.0F));
		return TexturedModelData.of(modelData, 500, 500);
	}
	@Override
	public void setAngles(TitanRavagerEntity entity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * ((float)Math.PI / 180);
		this.head.yaw = i * ((float)Math.PI / 180);
		float k = 0.4f * g;
		this.leg0.pitch = MathHelper.cos(f * 1f) * k;
		this.leg1.pitch = MathHelper.cos(f * 1f + (float)Math.PI) * k;
		this.leg2.pitch = MathHelper.cos(f * 1f + (float)Math.PI) * k;
		this.leg3.pitch = MathHelper.cos(f * 1f) * k;
	}
	@Override
	public void animateModel(TitanRavagerEntity ravagerEntity, float f, float g, float h) {
		super.animateModel(ravagerEntity, f, g, h);
		int i = ravagerEntity.getStunTick();
		int j = ravagerEntity.getRoarTick();
		int k = 20;
		int l = ravagerEntity.getAttackTick();
		int m = 10;
		if (l > 0) {
			float n = MathHelper.wrap((float)l - h, 10.0f);
			float o = (1.0f + n) * 0.5f;
			float p = o * o * o * 12.0f;
			float q = p * MathHelper.sin(this.neck.pitch);
			this.neck.pivotZ = -80.0f + p;
			this.neck.pivotY = -56.0f - q;
			float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
			this.mouth.pitch = 1.5707964f * r;
			this.mouth.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
		} else {
			float n = -1.0f;
			float o = -1.0f * MathHelper.sin(this.neck.pitch);
			this.neck.pivotX = 0.0f;
			this.neck.pivotY = -56.0f - o;
			this.neck.pivotZ = -80.0f;
			boolean bl = i > 0;
			this.neck.pitch = bl ? 0.21991149f : 0.0f;
			this.mouth.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
			if (bl) {
				double d = (double)i / 40.0;
				this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0f;
			} else if (j > 0) {
				float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
				this.mouth.pitch = 1.5707964f * q;
			}
		}
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		neck.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg0.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
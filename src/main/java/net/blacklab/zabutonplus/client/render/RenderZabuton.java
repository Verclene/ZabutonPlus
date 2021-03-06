package net.blacklab.zabutonplus.client.render;

import net.blacklab.zabutonplus.ZabutonPlus;
import net.blacklab.zabutonplus.client.model.ModelZabuton;
import net.blacklab.zabutonplus.entity.EntityZabuton;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderZabuton extends Render {

	protected ModelBase baseZabuton;
	protected static final ResourceLocation[] textures = new ResourceLocation[] {
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_f.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_e.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_d.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_c.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_b.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_a.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_9.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_8.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_7.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_6.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_5.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_4.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_3.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_2.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_1.png"),
		new ResourceLocation(ZabutonPlus.DOMAIN, "textures/entity/zabuton_0.png")
	};

	public RenderZabuton(RenderManager renderManager) {
		super(renderManager);
		shadowSize = 0.0F;
		baseZabuton = new ModelZabuton();
	}

	public void doRenderZabuton(EntityZabuton entityzabuton, double d, double d1, double d2, float f, float f1) {
		if(entityzabuton.color >= 0 && entityzabuton.color < 16)
		{
			shadowSize = 0.5F;
			// レンダリング実装
			// レンダリング
			GL11.glPushMatrix();
			GL11.glTranslatef((float)d, (float)d1, (float)d2);
			GL11.glRotatef(180F - f, 0.0F, 1.0F, 0.0F);

			bindEntityTexture(entityzabuton);
			GL11.glScalef(-1F, -1F, 1.0F);
			baseZabuton.render(entityzabuton, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
		else
		{
			// Entityがスポーン後、サーバから色情報を取得するまで描画しない。どの色で描画すればいいかわからないため
			shadowSize = 0.0F;
		}
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		doRenderZabuton((EntityZabuton)entity, d, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity var1) {
		return textures[((EntityZabuton)var1).color & 0x0F];
	}

}
package net.blacklab.zabutonplus.proxy;

import net.blacklab.zabutonplus.client.render.RenderZabuton;
import net.blacklab.zabutonplus.entity.EntityZabuton;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
	public void RegistRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityZabuton.class, new RenderZabuton(Minecraft.getMinecraft().getRenderManager()));
	}
}

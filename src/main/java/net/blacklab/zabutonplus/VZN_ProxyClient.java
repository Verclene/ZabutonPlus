package net.blacklab.zabutonplus;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class VZN_ProxyClient extends VZN_ProxyCommon
{
	public void RegistRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(VZN_EntityZabuton.class, new VZN_RenderZabuton(Minecraft.getMinecraft().getRenderManager()));
	}
}

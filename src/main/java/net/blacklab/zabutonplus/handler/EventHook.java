package net.blacklab.zabutonplus.handler;

import net.blacklab.zabutonplus.ZabutonPlus;
import net.blacklab.zabutonplus.entity.EntityZabuton;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventHook {

	public EventHook() {
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if (event.player.worldObj.isRemote &&
				ZabutonPlus.currentVersion.compareVersion(ZabutonPlus.latestVersion) > 0) {
			try {
				event.player.addChatComponentMessage(new ChatComponentText(
						String.format("[%s]New Version Avaliable: %s", ZabutonPlus.NAME, ZabutonPlus.latestVersion.shownName)));
				event.player.addChatComponentMessage(new ChatComponentText(
						String.format("[%s]Go to: http://el-blacklab.net/", ZabutonPlus.NAME)));
			} catch (Exception e) {
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		if (event.target instanceof EntityLivingBase && event.target.ridingEntity instanceof EntityZabuton) {
			ZabutonPlus.Debug("Interact Zabuton Event %s", event.target.ridingEntity);
			event.target.ridingEntity.attackEntityFrom(DamageSource.generic, 20f);
		}
	}

}

package net.blacklab.zabutonplus;

import net.blacklab.lib.version.Version;
import net.blacklab.lib.version.Version.VersionData;
import net.blacklab.zabutonplus.entity.EntityZabuton;
import net.blacklab.zabutonplus.handler.BehaviorZabutonDispense;
import net.blacklab.zabutonplus.handler.EventHook;
import net.blacklab.zabutonplus.item.ItemZabuton;
import net.blacklab.zabutonplus.proxy.ProxyCommon;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


@Mod(	modid = ZabutonPlus.DOMAIN,
		name  = ZabutonPlus.NAME,
		version = ZabutonPlus.VERSION)
public class ZabutonPlus
{
//	@MLProp
	public static boolean isDebugMessage = true;

	public static Item zabuton;
	public static final String DOMAIN = "zabutonplus";
	
	public static final String NAME = "ZabutonPlus";
	public static final String VERSION = "1.0.6";
	
	public static final VersionData currentVersion = new VersionData(1, VERSION, VERSION);
	public static VersionData latestVersion = new VersionData(1, "1.0.1", "1.0.1");
	
	@SidedProxy(
			clientSide = "net.blacklab.zabutonplus.proxy.ProxyClient",
			serverSide = "net.blacklab.zabutonplus.proxy.ProxyCommon")
	public static ProxyCommon proxy;


	public static void Debug(String pText, Object... pData) {
		// デバッグメッセージ
		if (isDebugMessage) {
			System.out.println(String.format("Zabuton-" + pText, pData));
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		zabuton = new ItemZabuton();
		zabuton.setUnlocalizedName(DOMAIN+":zabuton");
//		zabuton.setTextureName(DOMAIN+":zabuton");
		zabuton.setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerItem(zabuton, "zabuton");

		for (int i=0; i<16; i++) {
			if (event.getSide()==Side.CLIENT) {
				ModelLoader.setCustomModelResourceLocation(zabuton, i,
						new ModelResourceLocation(ZabutonPlus.DOMAIN+":zabuton", "inventory"));
			}
		}
		
		latestVersion = Version.getLatestVersion("http://mc.el-blacklab.net/versions.cgi?id=net.blacklab.zabutonplus", 5);
		FMLCommonHandler.instance().bus().register(new EventHook());
	}

	@EventHandler	// 1.6.2
	public void Init(FMLInitializationEvent evt)
	{
		// MMMLibのRevisionチェック
//		MMM_Helper.checkRevision("3");

		for (int i = 0; i < 16; i++) {
			/* langファイルに移動
			ModLoader.addLocalization(
					(new StringBuilder()).append(zabuton.getUnlocalizedName()).append(".").append(ItemDye.dyeColorNames[15 - i]).append(".name").toString(),
					(new StringBuilder()).append("Zabuton ").append(ItemDye.field_150923_a[15 - i]).toString()
				);
			ModLoader.addLocalization(
					(new StringBuilder()).append(zabuton.getUnlocalizedName()).append(".").append(ItemDye.dyeColorNames[15 - i]).append(".name").toString(),
					"ja_JP",
					(new StringBuilder()).append("座布団 ").append(VZN_ItemZabuton.colorNamesJP[15 - i]).toString()
				);
			*/
			GameRegistry.addRecipe(new ItemStack(zabuton, 1, 15 - i), new Object[] {
				"s ", "##",
				Character.valueOf('s'), Items.string,
				Character.valueOf('#'), new ItemStack(Blocks.wool, 1, i)
			});
		}

		EntityRegistry.registerModEntity(EntityZabuton.class, "zabuton", 0, this, 80, 3, true);
		proxy.RegistRenderer();

//		BlockDispenser.dispenseBehaviorRegistry.putObject(zabuton, new BehaviorZabutonDispense());
	}
}

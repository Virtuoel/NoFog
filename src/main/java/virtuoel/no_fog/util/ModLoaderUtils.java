package virtuoel.no_fog.util;

import net.minecraftforge.fml.loading.FMLLoader;

public class ModLoaderUtils
{
	public static boolean isModLoaded(final String modId)
	{
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}
}

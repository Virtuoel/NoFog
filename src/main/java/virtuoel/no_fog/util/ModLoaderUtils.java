package virtuoel.no_fog.util;

import net.fabricmc.loader.api.FabricLoader;

public class ModLoaderUtils
{
	public static boolean isModLoaded(final String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}

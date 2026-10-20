package virtuoel.no_fog.legacy;

import net.fabricmc.api.ClientModInitializer;
import virtuoel.no_fog.legacy.util.VersionUtils;

public final class LegacyBootstrap implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		if (VersionUtils.MAJOR == 1 && VersionUtils.MINOR >= 16 && VersionUtils.MINOR <= 21
			&& (VersionUtils.MINOR != 16 || VersionUtils.PATCH >= 5))
		{
			try
			{
				final Class<?> clientClass = Class.forName("virtuoel.no_fog.legacy.NoFogClient", true, getClass().getClassLoader());
				((ClientModInitializer) clientClass.getDeclaredConstructor().newInstance()).onInitializeClient();
			}
			catch (ReflectiveOperationException e)
			{
				throw new IllegalStateException("Unable to initialize the legacy No Fog implementation", e);
			}
		}
	}
}

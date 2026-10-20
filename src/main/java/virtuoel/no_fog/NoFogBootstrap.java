package virtuoel.no_fog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

public final class NoFogBootstrap implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		if (isModernMinecraft())
		{
			try
			{
				final Class<?> clientClass = Class.forName("virtuoel.no_fog.NoFogClient", true, getClass().getClassLoader());
				((ClientModInitializer) clientClass.getDeclaredConstructor().newInstance()).onInitializeClient();
			}
			catch (ReflectiveOperationException e)
			{
				throw new IllegalStateException("Unable to initialize the 26.3 No Fog implementation", e);
			}
		}
	}

	public static boolean isModernMinecraft()
	{
		final Version version = FabricLoader.getInstance().getModContainer("minecraft")
			.orElseThrow(() -> new IllegalStateException("Minecraft version metadata is unavailable"))
			.getMetadata().getVersion();
		if (!(version instanceof SemanticVersion))
		{
			throw new IllegalStateException("Minecraft version is not semantic: " + version.getFriendlyString());
		}

		final SemanticVersion semanticVersion = (SemanticVersion) version;
		final int major = semanticVersion.getVersionComponent(0);
		final int minor = semanticVersion.getVersionComponent(1);
		return major > 26 || (major == 26 && minor >= 3);
	}
}

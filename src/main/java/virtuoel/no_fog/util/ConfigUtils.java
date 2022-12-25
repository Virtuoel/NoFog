package virtuoel.no_fog.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import virtuoel.no_fog.NoFogClient;

public class ConfigUtils
{
	private static Object dimensionTypeRegistryWrapper = null;
	
	public static Map<String, FogToggles> populateDimensionToggles(final Map<String, FogToggles> data)
	{
		final List<String> ids = new LinkedList<>();
		
		try
		{
			final MinecraftClient client = MinecraftClient.getInstance();
			if (client != null && client.world != null)
			{
				final Registry<?> registry = ReflectionUtils.getDynamicRegistry(client.world, ReflectionUtils.DIMENSION_TYPE_KEY);
				ReflectionUtils.getIds(registry).stream().map(Identifier::toString).sorted().forEach(ids::add);
			}
		}
		catch (Throwable e)
		{
			NoFogClient.LOGGER.catching(e);
		}
		
		if (ids.isEmpty())
		{
			try
			{
				if (VersionUtils.MINOR < 19 || (VersionUtils.MINOR == 19 && VersionUtils.PATCH <= 2))
				{
					ids.add(World.OVERWORLD.getValue().toString());
					ids.add(World.NETHER.getValue().toString());
					ids.add(World.END.getValue().toString());
				}
				else
				{
					/* // TODO 1.19.3
					if (dimensionTypeRegistryWrapper == null)
					{
						dimensionTypeRegistryWrapper = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(ReflectionUtils.DIMENSION_TYPE_KEY);
					}
					
					((RegistryWrapper<?>) dimensionTypeRegistryWrapper).streamKeys().map(RegistryKey::getValue).map(Identifier::toString).sorted().forEach(ids::add);
					*/
				}
			}
			catch (Throwable e)
			{
				NoFogClient.LOGGER.catching(e);
			}
		}
		
		for (final String id : ids)
		{
			data.computeIfAbsent(id, FogToggles::new);
		}
		
		return data;
	}
	
	private static Object biomeRegistryWrapper = null;
	
	public static Map<String, FogToggles> populateBiomeToggles(final Map<String, FogToggles> data)
	{
		final List<String> ids = new LinkedList<>();
		
		try
		{
			final MinecraftClient client = MinecraftClient.getInstance();
			if (client != null && client.world != null)
			{
				final Registry<Biome> registry = ReflectionUtils.getDynamicRegistry(client.world, ReflectionUtils.BIOME_KEY);
				ids.addAll(ReflectionUtils.getIds(registry).stream().map(Identifier::toString).sorted().collect(Collectors.toList()));
			}
		}
		catch (Throwable e)
		{
			NoFogClient.LOGGER.catching(e);
		}
		
		if (ids.isEmpty())
		{
			try
			{
				if (ReflectionUtils.BUILTIN_BIOME_REGISTRY != null)
				{
					ReflectionUtils.getIds(ReflectionUtils.BUILTIN_BIOME_REGISTRY).stream().map(Identifier::toString).sorted().forEach(ids::add);
				}
				else
				{
					/* // TODO 1.19.3
					if (biomeRegistryWrapper == null)
					{
						biomeRegistryWrapper = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(ReflectionUtils.BIOME_KEY);
					}
					
					((RegistryWrapper<?>) biomeRegistryWrapper).streamKeys().map(RegistryKey::getValue).map(Identifier::toString).sorted().forEach(ids::add);
					*/
				}
			}
			catch (Throwable e)
			{
				NoFogClient.LOGGER.catching(e);
			}
		}
		
		for (final String id : ids)
		{
			data.computeIfAbsent(id, FogToggles::new);
		}
		
		return data;
	}
}

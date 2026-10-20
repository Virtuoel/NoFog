package virtuoel.no_fog.util;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class ConfigUtils
{
	public static Map<String, FogToggles> populateDimensionToggles(Map<String, FogToggles> data)
	{
		final Set<String> ids = new LinkedHashSet<>();
		ids.add(Level.OVERWORLD.identifier().toString());
		ids.add(Level.NETHER.identifier().toString());
		ids.add(Level.END.identifier().toString());
		
		final Minecraft client = Minecraft.getInstance();
		if (client != null && client.level != null)
		{
			ids.add(client.level.dimension().identifier().toString());
		}
		
		ids.stream().sorted().forEach(id -> data.computeIfAbsent(id, FogToggles::new));
		return data;
	}

	public static Map<String, FogToggles> populateBiomeToggles(Map<String, FogToggles> data)
	{
		final Minecraft client = Minecraft.getInstance();
		if (client != null && client.level != null)
		{
			final Registry<Biome> registry = client.level.registryAccess().lookupOrThrow(Registries.BIOME);
			registry.keySet().stream()
				.map(Identifier::toString)
				.sorted()
				.forEach(id -> data.computeIfAbsent(id, FogToggles::new));
		}
		
		return data;
	}

	private ConfigUtils()
	{

	}
}

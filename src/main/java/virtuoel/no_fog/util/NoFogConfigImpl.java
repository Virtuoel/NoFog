package virtuoel.no_fog.util;

import java.util.LinkedHashMap;
import java.util.Map;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.api.NoFogConfig;

@Config(name = NoFogClient.MOD_ID)
public class NoFogConfigImpl implements NoFogConfig, ConfigData
{
	public Map<String, FogToggles> biomeToggles = new LinkedHashMap<>();
	
	@Override
	public Map<String, FogToggles> getBiomeToggles()
	{
		return biomeToggles;
	}
}

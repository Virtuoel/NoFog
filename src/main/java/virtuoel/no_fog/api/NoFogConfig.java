package virtuoel.no_fog.api;

import java.util.Map;

import virtuoel.no_fog.util.FogToggles;

@FunctionalInterface
public interface NoFogConfig
{
	public Map<String, FogToggles> getBiomeToggles();
}

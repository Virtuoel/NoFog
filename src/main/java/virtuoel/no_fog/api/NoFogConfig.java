package virtuoel.no_fog.api;

import java.util.Map;

import virtuoel.no_fog.util.FogToggles;

public interface NoFogConfig
{
	public FogToggles getGlobalToggles();
	public Map<String, FogToggles> getDimensionToggles();
	public Map<String, FogToggles> getBiomeToggles();
}

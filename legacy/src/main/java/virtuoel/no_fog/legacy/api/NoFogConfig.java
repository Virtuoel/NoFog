package virtuoel.no_fog.legacy.api;

import java.util.Map;

import virtuoel.no_fog.legacy.util.FogToggles;

public interface NoFogConfig
{
	public FogToggles getGlobalToggles();
	
	public Map<String, FogToggles> getDimensionToggles();
	
	public Map<String, FogToggles> getBiomeToggles();
}

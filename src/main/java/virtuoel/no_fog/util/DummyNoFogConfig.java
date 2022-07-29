package virtuoel.no_fog.util;

import java.util.LinkedHashMap;
import java.util.Map;

import virtuoel.no_fog.api.NoFogConfig;

public class DummyNoFogConfig implements NoFogConfig
{
	public FogToggles globalToggles = new FogToggles();
	public Map<String, FogToggles> biomeToggles = new LinkedHashMap<>();
	public Map<String, FogToggles> dimensionToggles = new LinkedHashMap<>();
	
	@Override
	public Map<String, FogToggles> getBiomeToggles()
	{
		return biomeToggles;
	}
	
	@Override
	public Map<String, FogToggles> getDimensionToggles()
	{
		return dimensionToggles;
	}
	
	@Override
	public FogToggles getGlobalToggles()
	{
		return globalToggles;
	}
}

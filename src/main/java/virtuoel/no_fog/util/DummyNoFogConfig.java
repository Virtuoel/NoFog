package virtuoel.no_fog.util;

import java.util.LinkedHashMap;
import java.util.Map;

import virtuoel.no_fog.api.NoFogConfig;

public class DummyNoFogConfig implements NoFogConfig
{
	public FogToggles globalToggles = new FogToggles();
	public Map<String, FogToggles> dimensionToggles = ConfigUtils.populateDimensionToggles(new LinkedHashMap<>());
	public Map<String, FogToggles> biomeToggles = ConfigUtils.populateBiomeToggles(new LinkedHashMap<>());
	
	@Override
	public FogToggles getGlobalToggles()
	{
		return globalToggles;
	}
	
	@Override
	public Map<String, FogToggles> getDimensionToggles()
	{
		return dimensionToggles;
	}
	
	@Override
	public Map<String, FogToggles> getBiomeToggles()
	{
		return biomeToggles;
	}
}

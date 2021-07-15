package virtuoel.no_fog.util;

import net.fabricmc.fabric.api.util.TriState;

public class FogToggles
{
	public FogToggles(Object... noop)
	{
		
	}
	
	public TriState skyFog = TriState.DEFAULT;
	public TriState terrainFog = TriState.DEFAULT;
	public TriState thickFog = TriState.DEFAULT;
	public TriState waterFog = TriState.DEFAULT;
	public TriState lavaFog = TriState.DEFAULT;
	public TriState powderSnowFog = TriState.DEFAULT;
	public TriState blindnessFog = TriState.DEFAULT;
}

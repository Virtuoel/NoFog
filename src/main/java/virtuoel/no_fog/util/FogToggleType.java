package virtuoel.no_fog.util;

import java.util.function.Function;

public enum FogToggleType implements Function<FogToggles, TriState>
{
	WATER(t -> t.waterFog),
	LAVA(t -> t.lavaFog),
	POWDER_SNOW(t -> t.powderSnowFog),
	BLINDNESS(t -> t.blindnessFog, true),
	DARKNESS(t -> t.darknessFog, true),
	THICK(t -> t.thickFog),
	SKY(t -> t.skyFog, true),
	TERRAIN(t -> t.terrainFog),
	;
	
	private final Function<FogToggles, TriState> delegate;
	public final boolean defaultToggle;
	
	FogToggleType(Function<FogToggles, TriState> configFunc)
	{
		this(configFunc, false);
	}
	
	FogToggleType(Function<FogToggles, TriState> configFunc, boolean defaultToggle)
	{
		this.delegate = configFunc;
		this.defaultToggle = defaultToggle;
	}
	
	@Override
	public TriState apply(final FogToggles t)
	{
		final TriState ret = delegate.apply(t);
		return ret == null ? TriState.DEFAULT : ret;
	}
}

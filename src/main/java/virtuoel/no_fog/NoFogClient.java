package virtuoel.no_fog;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;
import virtuoel.no_fog.api.NoFogConfig;
import virtuoel.no_fog.util.AutoConfigUtils;
import virtuoel.no_fog.util.DummyNoFogConfig;
import virtuoel.no_fog.util.FogToggleType;
import virtuoel.no_fog.util.FogToggles;
import virtuoel.no_fog.util.ModLoaderUtils;
import virtuoel.no_fog.util.NoFogDynamicRegistryManagerExtensions;

@Mod(NoFogClient.MOD_ID)
public class NoFogClient
{
	public static final String MOD_ID = "no_fog";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public static final boolean CONFIGS_LOADED = ModLoaderUtils.isModLoaded("cloth_config") || ModLoaderUtils.isModLoaded("cloth-config") || ModLoaderUtils.isModLoaded("cloth-config2");
	
	private static final NoFogConfig FALLBACK = new DummyNoFogConfig();
	
	public static final Supplier<NoFogConfig> CONFIG = !CONFIGS_LOADED ? () -> FALLBACK : AutoConfigUtils.CONFIG;
	
	public NoFogClient()
	{
		if (CONFIGS_LOADED)
		{
			AutoConfigUtils.initialize();
		}
	}
	
	public static final float FOG_START = -8.0F;
	public static final float FOG_END = 1_000_000.0F;
	
	public static float getFogDistance(FogToggleType type, Entity entity, float fogDistance, boolean start)
	{
		return isToggleEnabled(type, entity) ? fogDistance : start ? FOG_START : FOG_END;
	}
	
	public static boolean isToggleEnabled(FogToggleType type, Entity entity)
	{
		final String biome = ((NoFogDynamicRegistryManagerExtensions) entity.world.getRegistryManager()).no_fog_get(Registry.BIOME_KEY).getId(entity.world.getBiome(new BlockPos(entity.getPos()))).toString();
		final String dimension = entity.world.getRegistryKey().getValue().toString();
		
		final NoFogConfig config = NoFogClient.CONFIG.get();
		final FogToggles globalToggles = config.getGlobalToggles();
		final FogToggles biomeToggles = config.getBiomeToggles().computeIfAbsent(biome, FogToggles::new);
		final FogToggles dimensionToggles = config.getDimensionToggles().computeIfAbsent(dimension, FogToggles::new);
		
		return type.apply(biomeToggles).orElse(
			type.apply(dimensionToggles).orElse(
			type.apply(globalToggles).orElse(type.defaultToggle)));
	}
	
	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}
}

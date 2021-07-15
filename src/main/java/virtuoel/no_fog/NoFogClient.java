package virtuoel.no_fog;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import virtuoel.no_fog.api.NoFogConfig;
import virtuoel.no_fog.util.AutoConfigUtils;
import virtuoel.no_fog.util.FogToggles;
import virtuoel.no_fog.util.DummyNoFogConfig;

public class NoFogClient implements ClientModInitializer
{
	public static final String MOD_ID = "no_fog";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public static final boolean CONFIGS_LOADED = FabricLoader.getInstance().isModLoaded("cloth-config2");
	
	private static final NoFogConfig FALLBACK = new DummyNoFogConfig();
	
	public static final Supplier<NoFogConfig> CONFIG = !CONFIGS_LOADED ? () -> FALLBACK : AutoConfigUtils.CONFIG;
	
	public NoFogClient()
	{
		
	}
	
	@Override
	public void onInitializeClient()
	{
		if (CONFIGS_LOADED)
		{
			AutoConfigUtils.initialize();
		}
	}
	
	public static final float FOG_START = -8.0F;
	public static final float FOG_END = 1_000_000.0F;
	
	public static float getFogDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity, float value, boolean start)
	{
		final String biome = entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(entity.world.getBiome(entity.getBlockPos())).toString();
		final String dimension = entity.world.getRegistryKey().getValue().toString();
		
		final NoFogConfig config = NoFogClient.CONFIG.get();
		final FogToggles globalToggles = config.getGlobalToggles();
		final FogToggles biomeToggles = config.getBiomeToggles().computeIfAbsent(biome, FogToggles::new);
		final FogToggles dimensionToggles = config.getDimensionToggles().computeIfAbsent(dimension, FogToggles::new);
		
		if (cameraSubmersionType == CameraSubmersionType.WATER)
		{
			return biomeToggles.waterFog.orElse(
				dimensionToggles.waterFog.orElse(
					globalToggles.waterFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
		else if (cameraSubmersionType == CameraSubmersionType.LAVA)
		{
			return biomeToggles.lavaFog.orElse(
				dimensionToggles.lavaFog.orElse(
					globalToggles.lavaFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
		else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
		{
			return biomeToggles.powderSnowFog.orElse(
				dimensionToggles.powderSnowFog.orElse(
					globalToggles.powderSnowFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
		else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS))
		{
			return biomeToggles.blindnessFog.orElse(
				dimensionToggles.blindnessFog.orElse(
					globalToggles.blindnessFog.orElse(true)))
				? value : start ? FOG_START : FOG_END;
		}
		else if (thickFog)
		{
			return biomeToggles.thickFog.orElse(
				dimensionToggles.thickFog.orElse(
					globalToggles.thickFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
		else if (fogType == FogType.FOG_SKY)
		{
			return biomeToggles.skyFog.orElse(
				dimensionToggles.skyFog.orElse(
					globalToggles.skyFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
		else
		{
			return biomeToggles.terrainFog.orElse(
				dimensionToggles.terrainFog.orElse(
					globalToggles.terrainFog.orElse(false)))
				? value : start ? FOG_START : FOG_END;
		}
	}
	
	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}
}

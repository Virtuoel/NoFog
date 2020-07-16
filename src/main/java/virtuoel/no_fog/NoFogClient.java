package virtuoel.no_fog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import virtuoel.no_fog.api.NoFogConfig;
import virtuoel.no_fog.util.AutoConfigUtils;
import virtuoel.no_fog.util.FogToggles;

public class NoFogClient implements ClientModInitializer
{
	public static final String MOD_ID = "no_fog";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public static final boolean CONFIGS_LOADED = FabricLoader.getInstance().isModLoaded("cloth-config2") && FabricLoader.getInstance().isModLoaded("autoconfig1u");
	
	private static final Map<String, FogToggles> FALLBACK = new HashMap<>();
	
	public static final Supplier<NoFogConfig> CONFIG = !CONFIGS_LOADED ? () -> () -> FALLBACK : AutoConfigUtils.CONFIG;
	
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
	
	public static final float FOG_START = 0.0F;
	public static final float FOG_END = 1_000_000.0F;
	
	public static float getFogDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, FogToggles toggles, float value, boolean start)
	{
		final Entity entity = camera.getFocusedEntity();
		
		final boolean blind = entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS);
		
		if (blind)
		{
			return toggles.blindnessFog ? value : start ? FOG_START : FOG_END;
		}
		else if (thickFog)
		{
			return toggles.thickFog ? value : start ? FOG_START : FOG_END;
		}
		else if (fogType == FogType.FOG_SKY)
		{
			return toggles.skyFog ? value : start ? FOG_START : FOG_END;
		}
		else
		{
			return toggles.terrainFog ? value : start ? FOG_START : FOG_END;
		}
	}
	
	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}
}

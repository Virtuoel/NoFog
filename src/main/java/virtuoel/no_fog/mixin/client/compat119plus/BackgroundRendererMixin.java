package virtuoel.no_fog.mixin.client.compat119plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggleType;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", at = @At("RETURN"))
	private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info)
	{
		final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		final Entity entity = camera.getFocusedEntity();
		
		setFogDistance(fogType, thickFog, cameraSubmersionType, entity, true);
		setFogDistance(fogType, thickFog, cameraSubmersionType, entity, false);
	}
	
	@Unique
	private static void setFogDistance(BackgroundRenderer.FogType fogType, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity, boolean start)
	{
		final FogToggleType type;
		
		if (cameraSubmersionType == CameraSubmersionType.LAVA)
		{
			type = FogToggleType.LAVA;
		}
		else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
		{
			type = FogToggleType.POWDER_SNOW;
		}
		else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS))
		{
			type = FogToggleType.BLINDNESS;
		}
		else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.DARKNESS))
		{
			type = FogToggleType.DARKNESS;
		}
		else if (cameraSubmersionType == CameraSubmersionType.WATER)
		{
			type = FogToggleType.WATER;
		}
		else if (thickFog)
		{
			type = FogToggleType.THICK;
		}
		else if (fogType == BackgroundRenderer.FogType.FOG_SKY)
		{
			type = FogToggleType.SKY;
		}
		else
		{
			type = FogToggleType.TERRAIN;
		}
		
		if (NoFogClient.isToggleEnabled(type, entity))
		{
			if (start)
			{
				RenderSystem.setShaderFogStart(NoFogClient.FOG_START);
			}
			else
			{
				RenderSystem.setShaderFogEnd(NoFogClient.FOG_END);
			}
		}
	}
}

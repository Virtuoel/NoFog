package virtuoel.no_fog.legacy.mixin.client.compat119plus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import virtuoel.no_fog.legacy.NoFogClient;
import virtuoel.no_fog.legacy.util.FogToggleType;
import virtuoel.no_fog.legacy.util.ReflectionUtils;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", at = @At("RETURN"))
	private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info)
	{
		final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		final Entity entity = camera.getFocusedEntity();
		
		if (!NoFogClient.isToggleEnabled(getFogType(fogType, thickFog, cameraSubmersionType, entity), entity))
		{
			RenderSystem.setShaderFogStart(NoFogClient.FOG_START);
			RenderSystem.setShaderFogEnd(NoFogClient.FOG_END);
			RenderSystem.setShaderFogShape(FogShape.CYLINDER);
		}
	}
	
	@Unique
	private static FogToggleType getFogType(BackgroundRenderer.FogType fogType, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity)
	{
		if (cameraSubmersionType == CameraSubmersionType.LAVA)
		{
			return FogToggleType.LAVA;
		}
		
		if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
		{
			return FogToggleType.POWDER_SNOW;
		}
		
		if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.BLINDNESS))
		{
			return FogToggleType.BLINDNESS;
		}
		
		if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.DARKNESS))
		{
			return FogToggleType.DARKNESS;
		}
		
		if (cameraSubmersionType == CameraSubmersionType.WATER)
		{
			return FogToggleType.WATER;
		}
		
		if (thickFog)
		{
			return FogToggleType.THICK;
		}
		
		if (fogType == BackgroundRenderer.FogType.FOG_SKY)
		{
			return FogToggleType.SKY;
		}
		
		return FogToggleType.TERRAIN;
	}
}

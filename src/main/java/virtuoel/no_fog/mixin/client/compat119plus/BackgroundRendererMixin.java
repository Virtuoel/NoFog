package virtuoel.no_fog.mixin.client.compat119plus;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggleType;
import virtuoel.no_fog.util.ReflectionUtils;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Dynamic
	@Inject(method = "applyFog", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/render/Fog;newV"))
	private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info)
	{
		final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		final Entity entity = camera.getFocusedEntity();
		
		if (!NoFogClient.isToggleEnabled(getFogType(fogType, thickFog, cameraSubmersionType, entity), entity))
		{
			ReflectionUtils.setFogStart(NoFogClient.FOG_START);
			ReflectionUtils.setFogEnd(NoFogClient.FOG_END);
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

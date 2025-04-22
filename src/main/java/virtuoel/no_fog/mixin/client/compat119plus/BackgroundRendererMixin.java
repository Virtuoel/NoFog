package virtuoel.no_fog.mixin.client.compat119plus;

import net.minecraft.client.render.Fog;
import org.joml.Vector4f;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggleType;
import virtuoel.no_fog.util.ReflectionUtils;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", at = @At("RETURN"))
	private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickProgress, CallbackInfoReturnable<Fog> cir)
	{
		final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		final Entity entity = camera.getFocusedEntity();
		
		if (!NoFogClient.isToggleEnabled(getFogType(fogType, thickenFog, cameraSubmersionType, entity), entity))
		{
			Fog old = RenderSystem.getShaderFog();
			RenderSystem.setShaderFog(new Fog(NoFogClient.FOG_START, NoFogClient.FOG_END, FogShape.CYLINDER, old.red(), old.green(), old.blue(), old.alpha()));
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

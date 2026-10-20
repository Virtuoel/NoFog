package virtuoel.no_fog.legacy.mixin.client.compat117plus.compat1181minus;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import virtuoel.no_fog.legacy.NoFogClient;
import virtuoel.no_fog.legacy.util.FogToggleType;
import virtuoel.no_fog.legacy.util.ReflectionUtils;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", remap = false))
	private static void applyFogModifyWaterEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float end)
	{
		final float modified = getFogDistance(fogType, viewDistance, thickFog, cameraSubmersionType, entity, end, false);
		
		if (modified != end)
		{
			RenderSystem.setShaderFogEnd(modified);
		}
	}
	
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V", remap = false))
	private static void applyFogModifyStart(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float start)
	{
		final float modified = getFogDistance(fogType, viewDistance, thickFog, cameraSubmersionType, entity, start, true);
		
		if (modified != start)
		{
			RenderSystem.setShaderFogStart(modified);
		}
	}
	
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", remap = false))
	private static void applyFogModifyEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float start, float end)
	{
		final float modified = getFogDistance(fogType, viewDistance, thickFog, cameraSubmersionType, entity, end, false);
		
		if (modified != end)
		{
			RenderSystem.setShaderFogEnd(modified);
		}
	}
	
	@Unique
	private static float getFogDistance(BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity, float fogDistance, boolean start)
	{
		final FogToggleType type;
		
		if (cameraSubmersionType == CameraSubmersionType.WATER)
		{
			type = FogToggleType.WATER;
		}
		else if (cameraSubmersionType == CameraSubmersionType.LAVA)
		{
			type = FogToggleType.LAVA;
		}
		else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
		{
			type = FogToggleType.POWDER_SNOW;
		}
		else if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.BLINDNESS))
		{
			type = FogToggleType.BLINDNESS;
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
		
		return NoFogClient.getFogDistance(type, entity, fogDistance, start);
	}
}

package virtuoel.no_fog.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import virtuoel.no_fog.NoFogClient;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", remap = false))
	private static void applyFogModifyWaterEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float end)
	{
		final float modified = NoFogClient.getFogDistance(camera, fogType, viewDistance, thickFog, cameraSubmersionType, entity, end, false);
		
		if (modified != end)
		{
			RenderSystem.setShaderFogEnd(modified);
		}
	}
	
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V", remap = false))
	private static void applyFogModifyStart(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float start)
	{
		final float modified = NoFogClient.getFogDistance(camera, fogType, viewDistance, thickFog, cameraSubmersionType, entity, start, true);
		
		if (modified != start)
		{
			RenderSystem.setShaderFogStart(modified);
		}
	}
	
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", remap = false))
	private static void applyFogModifyEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, float start, float end)
	{
		final float modified = NoFogClient.getFogDistance(camera, fogType, viewDistance, thickFog, cameraSubmersionType, entity, end, false);
		
		if (modified != end)
		{
			RenderSystem.setShaderFogEnd(modified);
		}
	}
}

package virtuoel.no_fog.mixin.client.compat1182;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggleType;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
	private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, CameraSubmersionType cameraSubmersionType, Entity entity, FogShape fogShape, float start, float end)
	{
		final float modifiedStart = getFogDistance(fogType, viewDistance, thickFog, cameraSubmersionType, entity, start, true);
		final float modifiedEnd = getFogDistance(fogType, viewDistance, thickFog, cameraSubmersionType, entity, end, false);
		
		if (modifiedStart != start)
		{
			RenderSystem.setShaderFogStart(modifiedStart);
		}
		
		if (modifiedEnd != end)
		{
			RenderSystem.setShaderFogEnd(modifiedEnd);
		}
	}
	
	@Unique
	private static float getFogDistance(BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity, float fogDistance, boolean start)
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
		
		return NoFogClient.getFogDistance(type, entity, fogDistance, start);
	}
}

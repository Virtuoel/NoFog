package virtuoel.no_fog.legacy.mixin.client.compat116;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import virtuoel.no_fog.legacy.NoFogClient;
import virtuoel.no_fog.legacy.util.FogToggleType;
import virtuoel.no_fog.legacy.util.ReflectionUtils;
import virtuoel.no_fog.legacy.util.TagCompatibility;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogDensity(F)V", remap = false))
	private static void applyFogModifyDensity(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float density)
	{
		if (
			(TagCompatibility.isIn(fluidState, TagCompatibility.FluidTags.WATER) && !NoFogClient.isToggleEnabled(FogToggleType.WATER, entity)) ||
			(TagCompatibility.isIn(fluidState, TagCompatibility.FluidTags.LAVA) && !NoFogClient.isToggleEnabled(FogToggleType.LAVA, entity))
		)
		{
			ReflectionUtils.setFogDensity(0);
		}
	}
	
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogStart(F)V", remap = false))
	private static void applyFogModifyStart(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start)
	{
		final float modified = getFogDistance(fogType, viewDistance, thickFog, entity, start, true);
		
		if (modified != start)
		{
			ReflectionUtils.setFogStart(modified);
		}
	}
	
	@Dynamic
	@Inject(method = "method_3211", remap = false, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogEnd(F)V", remap = false))
	private static void applyFogModifyEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start, float end)
	{
		final float modified = getFogDistance(fogType, viewDistance, thickFog, entity, end, false);
		
		if (modified != end)
		{
			ReflectionUtils.setFogEnd(modified);
		}
	}
	
	@Unique
	private static float getFogDistance(BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, Entity entity, float fogDistance, boolean start)
	{
		final FogToggleType type;
		
		if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.BLINDNESS))
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

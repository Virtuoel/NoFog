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
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.registry.Registry;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggles;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogDensity(F)V"))
	private static void applyFogModifyDensity(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, boolean notEmpty, float density)
	{
		final String biome = Registry.BIOME.getId(entity.world.getBiome(entity.getBlockPos())).toString();
		final FogToggles toggles = NoFogClient.CONFIG.get().getBiomeToggles().computeIfAbsent(biome, FogToggles::new);
		
		if (
			(fluidState.isIn(FluidTags.WATER) && !toggles.waterFog) ||
			(fluidState.isIn(FluidTags.LAVA) && !toggles.lavaFog)
		)
		{
			RenderSystem.fogDensity(0.0F);
		}
	}
	
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogStart(F)V"))
	private static void applyFogModifyStart(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start)
	{
		final String biome = Registry.BIOME.getId(entity.world.getBiome(entity.getBlockPos())).toString();
		final FogToggles toggles = NoFogClient.CONFIG.get().getBiomeToggles().computeIfAbsent(biome, FogToggles::new);
		
		final float modified = NoFogClient.getFogDistance(camera, fogType, viewDistance, thickFog, toggles, start, true);
		
		if (modified != start)
		{
			RenderSystem.fogStart(modified);
		}
	}
	
	@Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogEnd(F)V"))
	private static void applyFogModifyEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start, float end)
	{
		final String biome = Registry.BIOME.getId(entity.world.getBiome(entity.getBlockPos())).toString();
		final FogToggles toggles = NoFogClient.CONFIG.get().getBiomeToggles().computeIfAbsent(biome, FogToggles::new);
		
		final float modified = NoFogClient.getFogDistance(camera, fogType, viewDistance, thickFog, toggles, end, false);
		
		if (modified != end)
		{
			RenderSystem.fogEnd(modified);
		}
	}
}

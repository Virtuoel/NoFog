package virtuoel.no_fog.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import virtuoel.no_fog.NoFogClient;

@Mixin(value = FogRenderer.class, priority = 900)
public abstract class FogRendererMixin
{
	// Run before Sodium snapshots these values for fog occlusion.
	@Inject(method = "setupFog", at = @At("RETURN"))
	private void noFogAdjustDistance(Camera camera, int renderDistance, DeltaTracker deltaTracker, float partialTick, ClientLevel level, CallbackInfoReturnable<?> cir)
	{
		final Object fog = cir.getReturnValue();
		if (fog instanceof FogData)
		{
			disableFog((FogData) fog);
		}
	}

	@Inject(method = "updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V", at = @At("HEAD"))
	private void noFogAdjustBuffer(FogData fog, CallbackInfo ci)
	{
		disableFog(fog);
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float noFogAdjustEnvironmentalStart(float value)
	{
		return NoFogClient.FOG_START;
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private float noFogAdjustEnvironmentalEnd(float value)
	{
		return NoFogClient.FOG_END;
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private float noFogAdjustRenderStart(float value)
	{
		return NoFogClient.FOG_START;
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 3)
	private float noFogAdjustRenderEnd(float value)
	{
		return NoFogClient.FOG_END;
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 4)
	private float noFogAdjustSkyEnd(float value)
	{
		return NoFogClient.FOG_END;
	}

	@ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 5)
	private float noFogAdjustCloudEnd(float value)
	{
		return NoFogClient.FOG_END;
	}
	
	private static void disableFog(FogData fog)
	{
		fog.environmentalStart = NoFogClient.FOG_START;
		fog.environmentalEnd = NoFogClient.FOG_END;
		fog.renderDistanceStart = NoFogClient.FOG_START;
		fog.renderDistanceEnd = NoFogClient.FOG_END;
		fog.skyEnd = NoFogClient.FOG_END;
		fog.cloudEnd = NoFogClient.FOG_END;
	}
}

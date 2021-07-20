package virtuoel.no_fog.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import virtuoel.no_fog.NoFogClient;

public class ReflectionUtils
{
	public static final MethodHandle FOG_DENSITY, FOG_START, FOG_END;
	
	static
	{
		MethodHandle d, s, e;
		
		try
		{
			final boolean is117plus = VersionUtils.MINOR >= 17;
			final Class<?> rs = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
			Method m;
			m = is117plus ? null : rs.getMethod("fogDensity", float.class);
			d = is117plus ? null : MethodHandles.lookup().unreflect(m);
			m = rs.getMethod(is117plus ? "setShaderFogStart" : "fogStart", float.class);
			s = MethodHandles.lookup().unreflect(m);
			m = rs.getMethod(is117plus ? "setShaderFogEnd" : "fogEnd", float.class);
			e = MethodHandles.lookup().unreflect(m);
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException e1)
		{
			d = null;
			s = null;
			e = null;
			NoFogClient.LOGGER.throwing(e1);
		}
		
		FOG_DENSITY = d;
		FOG_START = s;
		FOG_END = e;
	}
	
	public static void setFogDensity(float f) throws Throwable
	{
		if (FOG_DENSITY != null)
		{
			FOG_DENSITY.invokeExact(f);
		}
	}
	
	public static void setFogStart(float f) throws Throwable
	{
		if (FOG_START != null)
		{
			FOG_START.invokeExact(f);
		}
	}
	
	public static void setFogEnd(float f) throws Throwable
	{
		if (FOG_END != null)
		{
			FOG_END.invokeExact(f);
		}
	}
}

package virtuoel.no_fog.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import virtuoel.no_fog.NoFogClient;

public class ReflectionUtils
{
	public static final MethodHandle FOG_DENSITY, FOG_START, FOG_END;
	
	static
	{
		final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<MethodHandle>();
		
		final Lookup lookup = MethodHandles.lookup();
		Class<?> clazz;
		String mapped = "unset";
		Method m;
		
		try
		{
			final boolean is116 = VersionUtils.MINOR == 16;
			
			if (is116)
			{
				clazz = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
				m = clazz.getMethod("fogDensity", float.class);
				h.put(0, lookup.unreflect(m));
				m = clazz.getMethod("fogStart", float.class);
				h.put(1, lookup.unreflect(m));
				m = clazz.getMethod("fogEnd", float.class);
				h.put(2, lookup.unreflect(m));
			}
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException e1)
		{
			NoFogClient.LOGGER.error("Last method lookup: {}", mapped);
			NoFogClient.LOGGER.catching(e1);
		}
		
		FOG_DENSITY = h.get(0);
		FOG_START = h.get(1);
		FOG_END = h.get(2);
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
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object object, Supplier<T> defaultValue)
	{
		try
		{
			return (T) field.get(object);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			return defaultValue.get();
		}
	}
	
	public static final ReflectionUtils INSTANCE = new ReflectionUtils();
	
	private ReflectionUtils()
	{
		
	}
}

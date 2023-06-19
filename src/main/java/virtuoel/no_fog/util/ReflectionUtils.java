package virtuoel.no_fog.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import virtuoel.no_fog.NoFogClient;

public class ReflectionUtils
{
	public static final MethodHandle FOG_DENSITY, FOG_START, FOG_END;
	public static final RegistryKey<Registry<Fluid>> FLUID_KEY;
	public static final RegistryKey<Registry<Biome>> BIOME_KEY;
	public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_KEY;
	public static final Registry<Biome> BUILTIN_BIOME_REGISTRY;
	
	static
	{
		final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<MethodHandle>();
		
		final Lookup lookup = MethodHandles.lookup();
		String mapped = "unset";
		Class<?> clazz;
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
			NoFogClient.LOGGER.error("Current name lookup: {}", mapped);
			NoFogClient.LOGGER.catching(e1);
		}
		
		FOG_DENSITY = h.get(0);
		FOG_START = h.get(1);
		FOG_END = h.get(2);
		FLUID_KEY = RegistryKeys.FLUID;
		BIOME_KEY = RegistryKeys.BIOME;
		DIMENSION_TYPE_KEY = RegistryKeys.DIMENSION_TYPE;
		BUILTIN_BIOME_REGISTRY = null;
	}
	
	public static <E> Registry<E> getDynamicRegistry(RegistryWorldView w, RegistryKey<? extends Registry<E>> key)
	{
		return w.getRegistryManager().get(key);
	}
	
	public static String getBiomeId(Entity entity)
	{
		final World world = entity.getEntityWorld();
		final Vec3d pos = entity.getPos();
		final Biome biome = world.getBiome(new BlockPos(MathHelper.floor(pos.getX()), MathHelper.floor(pos.getY()), MathHelper.floor(pos.getZ()))).value();
		return getId(getDynamicRegistry(world, BIOME_KEY), biome).toString();
	}
	
	public static Set<Identifier> getIds(Registry<?> registry)
	{
		return registry.getIds();
	}
	
	public static <V> Identifier getId(Registry<V> registry, V entry)
	{
		return registry.getId(entry);
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

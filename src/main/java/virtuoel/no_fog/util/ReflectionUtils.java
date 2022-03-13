package virtuoel.no_fog.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import virtuoel.no_fog.NoFogClient;

public class ReflectionUtils
{
	public static final MethodHandle FOG_DENSITY, FOG_START, FOG_END, GET_REGISTRY_MANAGER, GET_DYNAMIC_REGISTRY, GET_BIOME;
	
	static
	{
		final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<MethodHandle>();
		
		final Lookup lookup = MethodHandles.lookup();
		Class<?> clazz;
		String mapped = "unset";
		Method m;
		
		try
		{
			final boolean is116 = VersionUtils.MINOR == 16;
			final boolean is1182Plus = VersionUtils.MINOR > 18 || (VersionUtils.MINOR == 18 && VersionUtils.PATCH >= 2);
			
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
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_5423", "method_30349", "()Lnet/minecraft/class_5455;");
			m = RegistryWorldView.class.getMethod(mapped);
			h.put(3, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_5455", "method_30530", "(Lnet/minecraft/class_5321;)Lnet/minecraft/class_" + (is116 ? "2385;" : "2378;"));
			m = DynamicRegistryManager.class.getMethod(mapped, RegistryKey.class);
			h.put(4, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_4538", "method_23753", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_" + (is1182Plus ? "6880;" : "1959;"));
			m = WorldView.class.getMethod(mapped, BlockPos.class);
			h.put(5, lookup.unreflect(m));
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException e1)
		{
			NoFogClient.LOGGER.error("Last method lookup: {}", mapped);
			NoFogClient.LOGGER.catching(e1);
		}
		
		FOG_DENSITY = h.get(0);
		FOG_START = h.get(1);
		FOG_END = h.get(2);
		GET_REGISTRY_MANAGER = h.get(3);
		GET_DYNAMIC_REGISTRY = h.get(4);
		GET_BIOME = h.get(5);
	}
	
	public static <E> Registry<E> getDynamicRegistry(RegistryWorldView w, RegistryKey<? extends Registry<? extends E>> key) throws Throwable
	{
		if (GET_REGISTRY_MANAGER != null && GET_DYNAMIC_REGISTRY != null)
		{
			final DynamicRegistryManager m = (DynamicRegistryManager) GET_REGISTRY_MANAGER.invokeExact(w);
			return VersionUtils.MINOR == 16 ? (MutableRegistry<E>) GET_DYNAMIC_REGISTRY.invokeExact(m, key) : (Registry<E>) GET_DYNAMIC_REGISTRY.invokeExact(m, key);
		}
		
		return null;
	}
	
	public static Biome getBiome(Entity entity) throws Throwable
	{
		if (GET_BIOME != null)
		{
			if (VersionUtils.MINOR > 18 || (VersionUtils.MINOR == 18 && VersionUtils.PATCH >= 2))
			{
				return ((RegistryEntry<Biome>) GET_BIOME.invokeExact((WorldView) entity.world, new BlockPos(entity.getPos()))).value();
			}
			
			return (Biome) GET_BIOME.invokeExact((WorldView) entity.world, new BlockPos(entity.getPos()));
		}
		
		return null;
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
	
	public static void init()
	{
		
	}
}

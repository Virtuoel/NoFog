package virtuoel.no_fog.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RegistryWorldView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import virtuoel.no_fog.NoFogClient;

public class ReflectionUtils
{
	public static final Class<?> LITERAL_TEXT;
	public static final MethodHandle FOG_DENSITY, FOG_START, FOG_END, GET_REGISTRY_MANAGER, GET_DYNAMIC_REGISTRY, GET_BIOME, GET_IDS, GET_ID;
	public static final RegistryKey<Registry<Fluid>> FLUID_KEY;
	public static final RegistryKey<Registry<Biome>> BIOME_KEY;
	public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_KEY;
	public static final Registry<Biome> BUILTIN_BIOME_REGISTRY;
	
	static
	{
		final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		final Int2ObjectMap<MethodHandle> h = new Int2ObjectArrayMap<MethodHandle>();
		final Int2ObjectMap<Class<?>> c = new Int2ObjectArrayMap<Class<?>>();
		Object kF, kB, rB, kD = kB = rB = kF = null;
		
		final Lookup lookup = MethodHandles.lookup();
		String mapped = "unset";
		Class<?> clazz;
		Method m;
		Field f;
		
		try
		{
			final boolean is116 = VersionUtils.MINOR == 16;
			final boolean is118Minus = VersionUtils.MINOR <= 18;
			final boolean is1182Plus = VersionUtils.MINOR > 18 || (VersionUtils.MINOR == 18 && VersionUtils.PATCH >= 2);
			final boolean is1192Minus = VersionUtils.MINOR < 19 || (VersionUtils.MINOR == 19 && VersionUtils.PATCH <= 2);
			
			if (is118Minus)
			{
				mapped = mappingResolver.mapClassName("intermediary", "net.minecraft.class_2585");
				c.put(0, Class.forName(mapped));
			}
			
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
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_" + (is1192Minus ? "5423" : "4538"), "method_30349", "()Lnet/minecraft/class_5455;");
			m = (is1192Minus ? RegistryWorldView.class : WorldView.class).getMethod(mapped);
			h.put(3, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_5455", "method_30530", "(Lnet/minecraft/class_5321;)Lnet/minecraft/class_" + (is116 ? "2385;" : "2378;"));
			m = DynamicRegistryManager.class.getMethod(mapped, RegistryKey.class);
			h.put(4, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_4538", "method_23753", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_" + (is1182Plus ? "6880;" : "1959;"));
			m = WorldView.class.getMethod(mapped, BlockPos.class);
			h.put(5, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_10235", "()Ljava/util/Set;");
			m = Registry.class.getMethod(mapped);
			h.put(6, lookup.unreflect(m));
			
			mapped = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_2378", "method_10221", "(Ljava/lang/Object;)Lnet/minecraft/class_2960;");
			m = Registry.class.getMethod(mapped, Object.class);
			h.put(7, lookup.unreflect(m));
			
			final String registrar = is1192Minus ? "net.minecraft.class_2378" : "net.minecraft.class_7924";
			
			mapped = mappingResolver.mapClassName("intermediary", registrar);
			clazz = Class.forName(mapped);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "25103" : "41270"), "Lnet/minecraft/class_5321;");
			f = clazz.getField(mapped);
			kF = f.get(null);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "25114" : "41236"), "Lnet/minecraft/class_5321;");
			f = clazz.getField(mapped);
			kB = f.get(null);
			
			mapped = mappingResolver.mapFieldName("intermediary", registrar, "field_" + (is1192Minus ? "25095" : "41241"), "Lnet/minecraft/class_5321;");
			f = clazz.getField(mapped);
			kD = f.get(null);
			
			if (is1192Minus)
			{
				mapped = mappingResolver.mapClassName("intermediary", "net.minecraft.class_5458");
				clazz = Class.forName(mapped);
				
				mapped = mappingResolver.mapFieldName("intermediary", "net.minecraft.class_5458", "field_25933", "Lnet/minecraft/2378;");
				f = clazz.getField(mapped);
				rB = f.get(null);
			}
		}
		catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | NoSuchFieldException e1)
		{
			NoFogClient.LOGGER.error("Current name lookup: {}", mapped);
			NoFogClient.LOGGER.catching(e1);
		}
		
		FOG_DENSITY = h.get(0);
		FOG_START = h.get(1);
		FOG_END = h.get(2);
		GET_REGISTRY_MANAGER = h.get(3);
		GET_DYNAMIC_REGISTRY = h.get(4);
		GET_BIOME = h.get(5);
		GET_IDS = h.get(6);
		GET_ID = h.get(7);
		LITERAL_TEXT = c.get(0);
		FLUID_KEY = castRegistryKey(kF);
		BIOME_KEY = castRegistryKey(kB);
		DIMENSION_TYPE_KEY = castRegistryKey(kD);
		BUILTIN_BIOME_REGISTRY = castRegistry(rB);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Registry<T> castRegistry(Object obj)
	{
		return (Registry<T>) obj;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> RegistryKey<T> castRegistryKey(Object obj)
	{
		return (RegistryKey<T>) obj;
	}
	
	public static <E> Registry<E> getDynamicRegistry(RegistryWorldView w, RegistryKey<? extends Registry<? extends E>> key) throws Throwable
	{
		if (GET_REGISTRY_MANAGER != null && GET_DYNAMIC_REGISTRY != null)
		{
			final DynamicRegistryManager m;
			if (VersionUtils.MINOR < 19 || (VersionUtils.MINOR == 19 && VersionUtils.PATCH <= 2))
			{
				m = (DynamicRegistryManager) GET_REGISTRY_MANAGER.invokeExact(w);
			}
			else
			{
				m = (DynamicRegistryManager) GET_REGISTRY_MANAGER.invokeExact((WorldView) w);
			}
			
			return VersionUtils.MINOR == 16 ? (MutableRegistry<E>) GET_DYNAMIC_REGISTRY.invokeExact(m, key) : (Registry<E>) GET_DYNAMIC_REGISTRY.invokeExact(m, key);
		}
		
		return null;
	}
	
	public static String getBiomeId(Entity entity) throws Throwable
	{
		if (GET_BIOME != null)
		{
			final World world = entity.getEntityWorld();
			final Vec3d pos = entity.getPos();
			final BlockPos blockPos = new BlockPos(MathHelper.floor(pos.getX()), MathHelper.floor(pos.getY()), MathHelper.floor(pos.getZ()));
			
			if (VersionUtils.MINOR > 18 || (VersionUtils.MINOR == 18 && VersionUtils.PATCH >= 2))
			{
				return ((RegistryEntry<Biome>) GET_BIOME.invokeExact((WorldView) world, blockPos)).getKey().map(RegistryKey::getValue).map(Identifier::toString).orElse(null);
			}
			
			final Biome biome = (Biome) GET_BIOME.invokeExact((WorldView) world, blockPos);
			return getId(getDynamicRegistry(world, BIOME_KEY), biome).toString();
		}
		
		return null;
	}
	
	public static Set<Identifier> getIds(Registry<?> registry) throws Throwable
	{
		return (Set<Identifier>) GET_IDS.invoke(registry);
	}
	
	public static <V> Identifier getId(Registry<V> registry, V entry) throws Throwable
	{
		return (Identifier) GET_ID.invoke(registry, entry);
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
	
	public static Optional<Field> getField(final Optional<Class<?>> classObj, final String fieldName)
	{
		return classObj.map(c ->
		{
			try
			{
				final Field f = c.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f;
			}
			catch (SecurityException | NoSuchFieldException e)
			{
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(final Optional<Class<?>> classObj, final String fieldName, final Object object, final T defaultValue)
	{
		return getField(classObj, fieldName).map(f ->
		{
			try
			{
				return (T) f.get(object);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				return defaultValue;
			}
		}).orElse(defaultValue);
	}
	
	public static void setField(final Optional<Class<?>> classObj, final String fieldName, final Object object, final Object value)
	{
		getField(classObj, fieldName).ifPresent(f ->
		{
			try
			{
				f.set(object, value);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				
			}
		});
	}
	
	public static Optional<Method> getMethod(final Optional<Class<?>> classObj, final String methodName, final Class<?>... args)
	{
		return classObj.map(c ->
		{
			try
			{
				final Method m = c.getMethod(methodName, args);
				m.setAccessible(true);
				return m;
			}
			catch (SecurityException | NoSuchMethodException e)
			{
				return null;
			}
		});
	}
	
	public static <T> Optional<Constructor<T>> getConstructor(final Optional<Class<T>> clazz, final Class<?>... params)
	{
		return clazz.map(c ->
		{
			try
			{
				return c.getConstructor(params);
			}
			catch (NoSuchMethodException | SecurityException e)
			{
				return null;
			}
		});
	}
	
	public static Optional<Class<?>> getClass(final String className, final String... classNames)
	{
		Optional<Class<?>> ret = getClass(className);
		
		for (final String name : classNames)
		{
			if (ret.isPresent())
			{
				return ret;
			}
			
			ret = getClass(name);
		}
		
		return ret;
	}
	
	public static Optional<Class<?>> getClass(final String className)
	{
		try
		{
			return Optional.of(Class.forName(className));
		}
		catch (ClassNotFoundException e)
		{
			return Optional.empty();
		}
	}
	
	private ReflectionUtils()
	{
		
	}
}

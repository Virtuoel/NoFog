package virtuoel.no_fog.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class TagCompatibility
{
	private static final Optional<Class<?>> TAG_REGISTRY = ReflectionUtils.getClass("net.fabricmc.fabric.api.tag.TagRegistry");
	private static final Optional<Method> TAG_REGISTRY_FLUID = ReflectionUtils.getMethod(TAG_REGISTRY, "fluid", Identifier.class);
	
	private static final Optional<Class<?>> TAG_FACTORY = ReflectionUtils.getClass("net.fabricmc.fabric.api.tag.TagFactory");
	private static final Object TAG_FACTORY_FLUID = ReflectionUtils.getFieldValue(TAG_FACTORY, "FLUID", null, null);
	private static final Optional<Method> TAG_FACTORY_CREATE = ReflectionUtils.getMethod(TAG_FACTORY, "create", Identifier.class);
	
	private static final String TAG_CLASS = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_3494");
	private static final String CONTAINS_METHOD = FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary", TAG_CLASS, "method_15141", "(Ljava/lang/Object;)Z");
	private static final Optional<Class<?>> TAG = ReflectionUtils.getClass(TAG_CLASS);
	private static final Optional<Method> TAG_CONTAINS = ReflectionUtils.getMethod(TAG, CONTAINS_METHOD, Object.class);
	
	private static final String FLUID_TAGS_CLASS = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_3486");
	private static final Optional<Class<?>> FLUID_TAGS = ReflectionUtils.getClass(FLUID_TAGS_CLASS);
	private static final String FLUID_TAGS_WATER = FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_3486", "field_15517", VersionUtils.MINOR >= 18 ? "Lnet/minecraft/class_6862;" : "Lnet/minecraft/class_3486$class_5123;");
	private static final String FLUID_TAGS_LAVA = FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_3486", "field_15518", VersionUtils.MINOR >= 18 ? "Lnet/minecraft/class_6862;" : "Lnet/minecraft/class_3486$class_5123;");
	
	public static final class FluidTags
	{
		public static final Optional<Object> WATER = getVanillaFluidTag(true);
		public static final Optional<Object> LAVA = getVanillaFluidTag(false);
	}
	
	private static Optional<Object> getVanillaFluidTag(final boolean water)
	{
		return Optional.of(getFluidTag(new Identifier("minecraft", water ? "water" : "lava")).orElseGet(() -> ReflectionUtils.getFieldValue(FLUID_TAGS, water ? FLUID_TAGS_WATER : FLUID_TAGS_LAVA, null, null)));
	}
	
	public static Optional<Object> getFluidTag(Identifier id)
	{
		return Optional.ofNullable(invoke(() -> () -> TagKey.of(Registry.FLUID_KEY, id), null, varArgs(id), f -> f.apply(TAG_FACTORY_CREATE, TAG_FACTORY_FLUID), f -> f.apply(TAG_REGISTRY_FLUID, null)));
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isIn(FluidState state, Optional<Object> tag)
	{
		return invoke(() -> () -> state.isIn((TagKey<Fluid>) tag.orElseThrow(NullPointerException::new)), false, varArgs(state.getFluid()), f -> f.apply(TAG_CONTAINS, tag.orElseThrow(NullPointerException::new)));
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isIn(Fluid fluid, Optional<Object> tag)
	{
		return invoke(() -> () -> fluid.isIn((TagKey<Fluid>) tag.orElseThrow(NullPointerException::new)), false, varArgs(fluid), f -> f.apply(TAG_CONTAINS, tag.orElseThrow(NullPointerException::new)));
	}
	
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> T invoke(Supplier<Supplier<T>> defaultInvocation, T orElse, Object[] args, Function<BiFunction<Optional<Method>, Object, Optional<T>>, Optional<T>>... fallbacks)
	{
		try
		{
			return defaultInvocation.get().get();
		}
		catch (Throwable t)
		{
			final BiFunction<Optional<Method>, Object, Optional<T>> func = (me, o) -> me.map(m ->
			{
				try
				{
					return (T) (Object) m.invoke(o, args);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
				{
					return null;
				}
			});
			
			Optional<T> ret = Optional.empty();
			
			for (final Function<BiFunction<Optional<Method>, Object, Optional<T>>, Optional<T>> fallback : fallbacks)
			{
				ret = fallback.apply(func);
				
				if (ret.isPresent())
				{
					break;
				}
			}
			
			return ret.orElse(orElse);
		}
	}
	
	@SafeVarargs
	private static <T> T[] varArgs(T... args)
	{
		return args;
	}
	
	public static final TagCompatibility INSTANCE = new TagCompatibility();
	
	private TagCompatibility()
	{
		
	}
}

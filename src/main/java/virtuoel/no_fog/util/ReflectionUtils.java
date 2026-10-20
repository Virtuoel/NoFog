package virtuoel.no_fog.util;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import virtuoel.no_fog.NoFogClient;

public final class ReflectionUtils
{
	public static Identifier constructIdentifier(String namespace, String path)
	{
		return Identifier.fromNamespaceAndPath(namespace, path);
	}
	
	public static String getBiomeId(Entity entity)
	{
		final Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
		
		return biome.unwrapKey()
			.map(ResourceKey::identifier)
			.map(Identifier::toString)
			.orElseThrow(() -> new IllegalStateException("Biome has no registry key"));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object object, Supplier<T> defaultValue)
	{
		try
		{
			final T value = (T) field.get(object);
			return value != null ? value : defaultValue.get();
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalStateException("Unable to read config field " + field.getName(), e);
		}
	}
	
	private ReflectionUtils()
	{
		
	}
}

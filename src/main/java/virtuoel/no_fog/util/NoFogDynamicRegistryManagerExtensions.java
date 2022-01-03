package virtuoel.no_fog.util;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public interface NoFogDynamicRegistryManagerExtensions
{
	<E> SimpleRegistry<E> no_fog_get(RegistryKey<? extends Registry<? extends E>> key);
}

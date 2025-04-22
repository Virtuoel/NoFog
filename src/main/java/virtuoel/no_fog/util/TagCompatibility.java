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
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class TagCompatibility
{
	public static final TagKey<Fluid> WATER = net.minecraft.registry.tag.FluidTags.WATER;
	public static final TagKey<Fluid> LAVA = net.minecraft.registry.tag.FluidTags.LAVA;


	public static boolean isIn(FluidState fluidState, TagKey<Fluid> tag)
	{
		return fluidState.isIn(tag);
	}
	
	private TagCompatibility() {}
}

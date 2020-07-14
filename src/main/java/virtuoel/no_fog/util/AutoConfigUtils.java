package virtuoel.no_fog.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import virtuoel.no_fog.api.NoFogConfig;

public class AutoConfigUtils
{
	public static void initialize()
	{
		AutoConfig.register(NoFogConfigImpl.class, GsonConfigSerializer::new);
		GuiRegistry registry = AutoConfig.getGuiRegistry(NoFogConfigImpl.class);
		registry.registerPredicateProvider(AutoConfigUtils::toggleMapEntries, f -> f.getName().equals("biomeToggles"));
	}
	
	public static final Supplier<NoFogConfig> CONFIG = () -> AutoConfig.getConfigHolder(NoFogConfigImpl.class).getConfig();
	
	private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
	
	@SuppressWarnings("unchecked")
	private static <T> T getFieldValue(Field field, Object object, Supplier<T> defaultValue)
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
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> toggleMapEntries(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry)
	{
		final Map<String, FogToggles> data = getFieldValue(field, config, HashMap::new);
		
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		
		final List<String> ids = Registry.BIOME.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
		Collections.sort(ids);
		
		for (final String id : ids)
		{
			data.computeIfAbsent(id, FogToggles::new);
			
			entries.add(ENTRY_BUILDER
				.startSubCategory(
					new TranslatableText(Util.createTranslationKey("biome", new Identifier(id))),
					addToggleEntries(data, id)
				).build()
			);
		}
		
		return entries;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> addToggleEntries(Map<String, FogToggles> toggles, String id)
	{
		final FogToggles data = toggles.get(id);
		
		List<AbstractConfigListEntry> entries = new LinkedList<>();
		
		entries.add(booleanEntry(
			"text.no_fog.config.sky_fog",
			data.skyFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.skyFog = newValue
		));
		
		entries.add(booleanEntry(
			"text.no_fog.config.terrain_fog",
			data.terrainFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.terrainFog = newValue
		));
		
		entries.add(booleanEntry(
			"text.no_fog.config.thick_fog",
			data.thickFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.thickFog = newValue
		));
		
		entries.add(booleanEntry(
			"text.no_fog.config.water_fog",
			data.waterFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.waterFog = newValue
		));
		
		entries.add(booleanEntry(
			"text.no_fog.config.lava_fog",
			data.lavaFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.lavaFog = newValue
		));
		
		entries.add(booleanEntry(
			"text.no_fog.config.blindness_fog",
			data.blindnessFog,
			Boolean.FALSE::booleanValue,
			newValue -> data.blindnessFog = newValue
		));
		
		return entries;
	}
	
	private static BooleanListEntry booleanEntry(String key, boolean value, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer)
	{
		return ENTRY_BUILDER.startBooleanToggle(new TranslatableText(key), value)
			.setDefaultValue(defaultValue)
			.setSaveConsumer(saveConsumer)
			.setYesNoTextSupplier(
				bool -> new TranslatableText("text.cloth-config.boolean.value." + bool)
			)
			.build();
	}
}

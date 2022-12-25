package virtuoel.no_fog.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import virtuoel.no_fog.api.NoFogConfig;

public class AutoConfigUtils
{
	public static void initialize()
	{
		AutoConfig.register(NoFogConfigImpl.class, GsonConfigSerializer::new);
		final GuiRegistry registry = AutoConfig.getGuiRegistry(NoFogConfigImpl.class);
		registry.registerPredicateProvider(AutoConfigUtils::globalToggleEntry, f -> f.getName().equals("globalToggles"));
		registry.registerPredicateProvider(AutoConfigUtils::dimensionToggleMapEntries, f -> f.getName().equals("dimensionToggles"));
		registry.registerPredicateProvider(AutoConfigUtils::biomeToggleMapEntries, f -> f.getName().equals("biomeToggles"));
		
		ModLoadingContext.get().registerExtensionPoint(
			ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> AutoConfig.getConfigScreen(NoFogConfigImpl.class, screen).get())
		);
	}
	
	public static final Supplier<NoFogConfig> CONFIG = () -> AutoConfig.getConfigHolder(NoFogConfigImpl.class).getConfig();
	
	private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> globalToggleEntry(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry)
	{
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		
		entries.add(ENTRY_BUILDER
			.startSubCategory(
				I18nUtils.translate("text.no_fog.config.category.global", "Global settings"),
				addToggleEntries(ReflectionUtils.getFieldValue(field, config, FogToggles::new), Collections.emptyList())
			).build()
		);
		
		return entries;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> dimensionToggleMapEntries(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registryAccess)
	{
		final Map<String, FogToggles> data = ReflectionUtils.getFieldValue(field, config, HashMap::new);
		
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		final List<AbstractConfigListEntry> dimensionEntries = new LinkedList<>();
		
		ConfigUtils.populateDimensionToggles(data);
		
		final List<String> ids = data.keySet().stream().sorted((l, r) -> l.compareTo(r)).collect(Collectors.toList());
		
		for (final String id : ids)
		{
			dimensionEntries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.literal(id),
					addToggleEntries(data.get(id), Collections.singletonList(id))
				).build()
			);
		}
		
		entries.add(ENTRY_BUILDER
			.startSubCategory(
				I18nUtils.translate("text.no_fog.config.category.dimensions", "Dimension Type settings"),
				dimensionEntries
			).build()
		);
		
		return entries;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> biomeToggleMapEntries(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registryAccess)
	{
		final Map<String, FogToggles> data = ReflectionUtils.getFieldValue(field, config, HashMap::new);
		
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		final List<AbstractConfigListEntry> biomeEntries = new LinkedList<>();
		
		ConfigUtils.populateBiomeToggles(data);
		
		final List<Triple<String, String, String>> idData = data.keySet()
			.stream().map(i ->
			{
				final String key = Util.createTranslationKey("biome", new Identifier(i));
				return Triple.of(i, key, I18n.translate(key));
			})
			.sorted((l, r) -> l.getRight().compareTo(r.getRight()))
			.collect(Collectors.toList());
		
		String idStr, translationKey;
		for (final Triple<String, String, String> id : idData)
		{
			idStr = id.getLeft();
			translationKey = id.getMiddle();
			
			biomeEntries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.translate(translationKey, idStr),
					addToggleEntries(data.get(idStr), Arrays.asList(idStr, translationKey, id.getRight()))
				)
				.setTooltip(I18nUtils.literal(idStr))
				.build()
			);
		}
		
		if (!biomeEntries.isEmpty())
		{
			entries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.translate("text.no_fog.config.category.biomes", "Biome settings"),
					biomeEntries
				)
				.build()
			);
		}
		
		return entries;
	}
	
	private static boolean tagsFailed = false;
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> addToggleEntries(final FogToggles data, final Collection<String> tags)
	{
		List<AbstractConfigListEntry> entries = new LinkedList<>();
		
		final String enabledKey = "text.no_fog.config.default.enabled";
		final String enabledDefault = "Default: enabled";
		final String disabledKey = "text.no_fog.config.default.disabled";
		final String disabledDefault = "Default: disabled";
		
		entries.add(triStateEntry(
			"text.no_fog.config.sky_fog",
			data.skyFog,
			newValue -> data.skyFog = newValue,
			I18nUtils.translate(enabledKey, enabledDefault)
		));
		
		entries.add(triStateEntry(
			"text.no_fog.config.terrain_fog",
			data.terrainFog,
			newValue -> data.terrainFog = newValue,
			I18nUtils.translate(disabledKey, disabledDefault)
		));
		
		entries.add(triStateEntry(
			"text.no_fog.config.thick_fog",
			data.thickFog,
			newValue -> data.thickFog = newValue,
			I18nUtils.translate("text.no_fog.config.thick_fog.tooltip", "Enable thick fog"),
			I18nUtils.translate(disabledKey, disabledDefault)
		));
		
		entries.add(triStateEntry(
			"text.no_fog.config.water_fog",
			data.waterFog,
			newValue -> data.waterFog = newValue,
			I18nUtils.translate(disabledKey, disabledDefault)
		));
		
		entries.add(triStateEntry(
			"text.no_fog.config.lava_fog",
			data.lavaFog,
			newValue -> data.lavaFog = newValue,
			I18nUtils.translate(disabledKey, disabledDefault)
		));
		
		if (VersionUtils.MINOR >= 17)
		{
			entries.add(triStateEntry(
				"text.no_fog.config.powder_snow_fog",
				data.powderSnowFog,
				newValue -> data.powderSnowFog = newValue,
				I18nUtils.translate(disabledKey, disabledDefault)
			));
		}
		
		entries.add(triStateEntry(
			"text.no_fog.config.blindness_fog",
			data.blindnessFog,
			newValue -> data.blindnessFog = newValue,
			I18nUtils.translate(enabledKey, enabledDefault)
		));
		
		if (VersionUtils.MINOR >= 19)
		{
			entries.add(triStateEntry(
				"text.no_fog.config.darkness_fog",
				data.darknessFog,
				newValue -> data.darknessFog = newValue,
				I18nUtils.translate(enabledKey, enabledDefault)
			));
		}
		
		if (!tagsFailed && tags != null && !tags.isEmpty())
		{
			try
			{
				for (final AbstractConfigListEntry<?> entry : entries)
				{
					entry.appendSearchTags(tags);
				}
			}
			catch (Throwable e)
			{
				tagsFailed = true;
			}
			
		}
		
		return entries;
	}
	
	private static EnumListEntry<TriState> triStateEntry(String key, TriState value, Consumer<TriState> saveConsumer, Text... tooltip)
	{
		return ENTRY_BUILDER.startEnumSelector(I18nUtils.translate(key, key), TriState.class, value != null ? value : TriState.DEFAULT)
			.setDefaultValue(TriState.DEFAULT)
			.setSaveConsumer(saveConsumer)
			.setTooltip(tooltip)
			.build();
	}
}

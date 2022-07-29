package virtuoel.no_fog.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import virtuoel.no_fog.api.NoFogConfig;

public class AutoConfigUtils
{
	public static void initialize()
	{
		AutoConfig.register(NoFogConfigImpl.class, GsonConfigSerializer::new);
		GuiRegistry registry = AutoConfig.getGuiRegistry(NoFogConfigImpl.class);
		registry.registerPredicateProvider(AutoConfigUtils::globalToggleEntry, f -> f.getName().equals("globalToggles"));
		registry.registerPredicateProvider(AutoConfigUtils::dimensionToggleMapEntries, f -> f.getName().equals("dimensionToggles"));
		registry.registerPredicateProvider(AutoConfigUtils::biomeToggleMapEntries, f -> f.getName().equals("biomeToggles"));
		
		ModLoadingContext.get().registerExtensionPoint(
			ExtensionPoint.CONFIGGUIFACTORY,
			() -> (mc, screen) -> AutoConfig.getConfigScreen(NoFogConfigImpl.class, screen).get()
		);
	}
	
	public static final Supplier<NoFogConfig> CONFIG = () -> AutoConfig.getConfigHolder(NoFogConfigImpl.class).getConfig();
	
	private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> globalToggleEntry(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry)
	{
		try
		{
			final List<AbstractConfigListEntry> entries = new LinkedList<>();
			
			entries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.translate("text.no_fog.config.category.global", "Global settings"),
					addToggleEntries((FogToggles) field.get(config))
				).build()
			);
			
			return entries;
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			return new LinkedList<>();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> dimensionToggleMapEntries(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry)
	{
		final Map<String, FogToggles> data = ReflectionUtils.getFieldValue(field, config, HashMap::new);
		
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		final List<AbstractConfigListEntry> dimensionEntries = new LinkedList<>();
		
		final MinecraftClient client = MinecraftClient.getInstance();
		List<String> ids = Arrays.asList(World.OVERWORLD.getValue().toString(), World.NETHER.getValue().toString(), World.END.getValue().toString());
		if (client != null && client.world != null)
		{
			final Registry<DimensionType> dimensionRegistry = client.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY);
			ids = dimensionRegistry.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
		}
		
		for (final String id : ids)
		{
			data.computeIfAbsent(id, FogToggles::new);
			
			dimensionEntries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.literal(id),
					addToggleEntries(data.get(id))
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
	private static List<AbstractConfigListEntry> biomeToggleMapEntries(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry)
	{
		final Map<String, FogToggles> data = ReflectionUtils.getFieldValue(field, config, HashMap::new);
		
		final List<AbstractConfigListEntry> entries = new LinkedList<>();
		final List<AbstractConfigListEntry> biomeEntries = new LinkedList<>();
		
		final MinecraftClient client = MinecraftClient.getInstance();
		Registry<Biome> biomeRegistry = BuiltinRegistries.BIOME;
		if (client != null && client.world != null)
		{
			biomeRegistry = client.world.getRegistryManager().get(Registry.BIOME_KEY);
		}
		
		final List<Identifier> ids = biomeRegistry.getIds().stream().collect(Collectors.toList());
		Collections.sort(ids, (l, r) -> I18n.translate(Util.createTranslationKey("biome", l)).compareTo(I18n.translate(Util.createTranslationKey("biome", r))));
		
		for (final Identifier id : ids)
		{
			final String idStr = id.toString();
			data.computeIfAbsent(idStr, FogToggles::new);
			
			biomeEntries.add(ENTRY_BUILDER
				.startSubCategory(
					I18nUtils.translate(Util.createTranslationKey("biome", id), Util.createTranslationKey("biome", id)),
					addToggleEntries(data.get(idStr))
				)
				.setTooltip(I18nUtils.literal(idStr))
				.build()
			);
		}
		
		entries.add(ENTRY_BUILDER
			.startSubCategory(
				I18nUtils.translate("text.no_fog.config.category.biomes", "Biome settings"),
				biomeEntries
			)
			.build()
		);
		
		return entries;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<AbstractConfigListEntry> addToggleEntries(final FogToggles data)
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

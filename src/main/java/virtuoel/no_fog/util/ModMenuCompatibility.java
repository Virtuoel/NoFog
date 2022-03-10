package virtuoel.no_fog.util;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import virtuoel.no_fog.NoFogClient;

public class ModMenuCompatibility implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return screen -> !NoFogClient.CONFIGS_LOADED ? null : AutoConfig.getConfigScreen(NoFogConfigImpl.class, screen).get();
	}
}

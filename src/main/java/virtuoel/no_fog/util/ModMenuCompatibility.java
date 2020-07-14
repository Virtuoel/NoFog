package virtuoel.no_fog.util;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import virtuoel.no_fog.NoFogClient;

public class ModMenuCompatibility implements ModMenuApi
{
	@Override
	public String getModId()
	{
		return NoFogClient.MOD_ID;
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return screen -> !NoFogClient.CONFIGS_LOADED ? null : AutoConfig.getConfigScreen(NoFogConfigImpl.class, screen).get();
	}
}

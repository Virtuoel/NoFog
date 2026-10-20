package virtuoel.no_fog.util;

import net.minecraft.network.chat.Component;

public class I18nUtils
{
	public static final Object[] EMPTY_VARARGS = new Object[0];
	
	public static Component translate(final String unlocalized, final String defaultLocalized)
	{
		return translate(unlocalized, defaultLocalized, EMPTY_VARARGS);
	}
	
	public static Component translate(final String unlocalized, final String defaultLocalized, final Object... args)
	{
		return Component.translatableWithFallback(unlocalized, defaultLocalized, args);
	}
	
	public static Component literal(final String text, final Object... args)
	{
		return Component.literal(String.format(text, args));
	}
}

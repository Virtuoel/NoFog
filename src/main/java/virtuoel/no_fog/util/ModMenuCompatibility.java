package virtuoel.no_fog.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import virtuoel.no_fog.NoFogBootstrap;

public class ModMenuCompatibility implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		final InvocationHandler handler = (proxy, method, args) ->
		{
			if ("create".equals(method.getName()) && NoFogBootstrap.isModernMinecraft()
				&& (ModLoaderUtils.isModLoaded("cloth_config") || ModLoaderUtils.isModLoaded("cloth-config") || ModLoaderUtils.isModLoaded("cloth-config2")))
			{
				return createConfigScreen(args[0]);
			}

			if ("toString".equals(method.getName()))
			{
				return "No Fog config screen factory";
			}
			if ("hashCode".equals(method.getName()))
			{
				return System.identityHashCode(proxy);
			}
			if ("equals".equals(method.getName()))
			{
				return proxy == args[0];
			}

			return null;
		};

		return (ConfigScreenFactory<?>) Proxy.newProxyInstance(
			ConfigScreenFactory.class.getClassLoader(),
			new Class<?>[] { ConfigScreenFactory.class },
			handler
		);
	}

	private static Object createConfigScreen(Object parent)
	{
		try
		{
			final Class<?> autoConfigClient = Class.forName("me.shedaniel.autoconfig.AutoConfigClient");
			final Class<?> configClass = Class.forName("virtuoel.no_fog.util.NoFogConfigImpl");
			for (final Method method : autoConfigClient.getMethods())
			{
				final Class<?>[] parameterTypes = method.getParameterTypes();
				if (method.getName().equals("getConfigScreen") && parameterTypes.length == 2
					&& parameterTypes[0] == Class.class && parameterTypes[1].isInstance(parent))
				{
					final Object result = method.invoke(null, configClass, parent);
					return result instanceof Optional ? ((Optional<?>) result).orElse(null) : result;
				}
			}
			throw new NoSuchMethodException("AutoConfigClient.getConfigScreen(Class, Screen)");
		}
		catch (InvocationTargetException e)
		{
			throw new IllegalStateException("Unable to create the No Fog config screen", e.getCause());
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalStateException("Unable to create the No Fog config screen", e);
		}
	}
}

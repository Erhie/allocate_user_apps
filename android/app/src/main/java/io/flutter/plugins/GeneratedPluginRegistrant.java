package android.app.src.main.java.io.flutter.plugins;

import io.flutter.plugin.common.PluginRegistry;
import org.erhy.allocateuserapps.AllocateUserAppsPlugin;

/**
 * Generated file. Do not edit.
 */
public final class GeneratedPluginRegistrant {
  public static void registerWith(PluginRegistry registry) {
    if (alreadyRegisteredWith(registry)) {
      return;
    }
    AllocateUserAppsPlugin.registerWith(registry.registrarFor("org.erhy.allocateuserapps.AllocateUserAppsPlugin"));
  }

  private static boolean alreadyRegisteredWith(PluginRegistry registry) {
    final String key = GeneratedPluginRegistrant.class.getCanonicalName();
    if (registry.hasPlugin(key)) {
      return true;
    }
    registry.registrarFor(key);
    return false;
  }
}

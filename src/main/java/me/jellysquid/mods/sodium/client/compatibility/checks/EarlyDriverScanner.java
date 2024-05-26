package me.jellysquid.mods.sodium.client.compatibility.checks;

import me.jellysquid.mods.sodium.client.compatibility.environment.OSInfo;
import me.jellysquid.mods.sodium.client.platform.MessageBox;
import me.jellysquid.mods.sodium.client.platform.windows.WindowsDriverStoreVersion;
import me.jellysquid.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterInfo;
import me.jellysquid.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterProbe;
import me.jellysquid.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs OpenGL driver validation before the game creates an OpenGL context. This runs during the earliest possible
 * opportunity at game startup, and uses a custom hardware prober to search for problematic drivers.
 */
public class EarlyDriverScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger("Xenon-EarlyDriverScanner");

    private static final String CONSOLE_MESSAGE_TEMPLATE = """ 
            ###ERROR_DESCRIPTION###
            
            For more information, please see: ###HELP_URL###
            """;

    private static final String INTEL_GEN7_DRIVER_MESSAGE = """
            The game failed to start because the currently installed Intel Graphics Driver is not compatible.
                                        
            Installed version: ###CURRENT_DRIVER###
            Required version: 15.33.53.5161 (or newer)
                                        
            You must update your graphics card driver in order to continue.""";

    private static final String INTEL_GEN7_DRIVER_HELP_URL = "https://github.com/CaffeineMC/sodium-fabric/wiki/Driver-Compatibility#windows-intel-gen7";

    public static void scanDrivers() {
        if (Configuration.WIN32_DRIVER_INTEL_GEN7) {
            var installedVersion = findBrokenIntelGen7GraphicsDriver();

            if (installedVersion != null) {
                showUnsupportedDriverMessageBox(
                        INTEL_GEN7_DRIVER_MESSAGE
                                .replace("###CURRENT_DRIVER###", installedVersion.getFriendlyString()),
                        INTEL_GEN7_DRIVER_HELP_URL);
            }
        }
    }

    private static void showUnsupportedDriverMessageBox(String message, String url) {
        // Always print the information to the log file first, just in case we can't show the message box.
        LOGGER.error(CONSOLE_MESSAGE_TEMPLATE
                .replace("###ERROR_DESCRIPTION###", message)
                .replace("###HELP_URL###", url));

        // Try to show a graphical message box (if the platform supports it) and shut down the game.
        MessageBox.showMessageBox(null, MessageBox.IconType.ERROR, "Xenon Renderer - Unsupported Driver", message, url);
        System.exit(1 /* failure code */);
    }

    // https://github.com/CaffeineMC/sodium-fabric/issues/899
    private static @Nullable WindowsDriverStoreVersion findBrokenIntelGen7GraphicsDriver() {
        if (OSInfo.getOS() != OSInfo.OS.WINDOWS) {
            return null;
        }

        for (var adapter : GraphicsAdapterProbe.getAdapters()) {
            if (adapter.vendor() != GraphicsAdapterVendor.INTEL) {
                continue;
            }

            try {
                var version = WindowsDriverStoreVersion.parse(adapter.version());

                if (version.driverModel() == 10 && version.featureLevel() == 18 && version.major() == 10) {
                    if (version.minor() >= 5161) {
                        // On https://www.intel.com/content/www/us/en/support/articles/000005654/graphics.html, we are told that
                        // there are two versioning schemes that can be used - one from Intel and one from Windows. The one
                        // from Windows will label the new driver (15.33.53.5161) as 10.18.10.5161. This causes us to falsely
                        // flag it as incompatible. To solve this problem, if we see the 10.18.10 prefix, we check if the build
                        // number is at least 5161, and if so, we assume the driver is new enough.
                        return null;
                    } else {
                        return version;
                    }
                }
            } catch (WindowsDriverStoreVersion.ParseException ignored) { }
        }

        return null;
    }
}

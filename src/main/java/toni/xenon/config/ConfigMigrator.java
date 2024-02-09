package toni.xenon.config;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigMigrator {
    public static Logger LOGGER = LogManager.getLogger("Xenon");

    /**
     * Tries to migrate the equivalent config file from Rubidium to the Xenon name if possible.
     */
    public static Path handleConfigMigration(String fileName) {
        Path mainPath = FMLPaths.CONFIGDIR.get().resolve(fileName);
        try {
            if(Files.notExists(mainPath)) {
                String legacyName = fileName.replace("xenon", "rubidium");
                Path legacyPath = FMLPaths.CONFIGDIR.get().resolve(legacyName);
                if(Files.exists(legacyPath)) {
                    Files.move(legacyPath, mainPath);
                    LOGGER.warn("Migrated {} config file to {}", legacyName, fileName);
                }
            }
        } catch(IOException | RuntimeException e) {
            LOGGER.error("Exception encountered while attempting config migration", e);
        }

        return mainPath;
    }
}

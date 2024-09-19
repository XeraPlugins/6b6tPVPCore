package me.ian.io;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import lombok.SneakyThrows;
import me.ian.PVPHelper;

import java.io.File;
import java.io.FileReader;

/**
 * @author SevJ6
 */
public class Config {

    private final File configFile;
    // No need to add getters/setters for these

    @Getter
    private Toml toml;

    // Constructor to create or load a new TOML file
    public Config(String name) {
        File dataFolder = PVPHelper.INSTANCE.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        this.configFile = new File(dataFolder, name);
        try {
            if (configFile.exists()) {
                loadConfig();  // Load the existing TOML file
            } else {
                // Load and save the default config
                this.toml = new Toml().read(PVPHelper.class.getResourceAsStream("/config.toml"));
                saveConfig();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Method to load an existing TOML file
    @SneakyThrows
    public void loadConfig() {
        FileReader fileReader = new FileReader(configFile);
        this.toml = new Toml().read(fileReader);
    }

    // Save the config back to the TOML file
    @SneakyThrows
    public void saveConfig() {
        TomlWriter tomlWriter = new TomlWriter();
        tomlWriter.write(toml.toMap(), configFile);
    }
}

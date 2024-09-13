package me.ian.io;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import me.ian.PVPHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author SevJ6
 */
public class Config {

    private final File configFile;
    // No need to add getters/setters for these

    @Getter
    private Toml toml;
    private Map<String, Object> configData;

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
                this.configData = toml.toMap();
                saveConfig();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Method to load an existing TOML file
    public void loadConfig() throws FileNotFoundException {
        FileReader fileReader = new FileReader(configFile);
        this.toml = new Toml().read(fileReader);
        this.configData = toml.toMap();
    }

    // Save the config back to the TOML file
    public void saveConfig() throws IOException {
        TomlWriter tomlWriter = new TomlWriter();
        tomlWriter.write(configData, configFile);
    }

    // Print the current running config, for debugging purposes
    public void printConfig() {
        configData.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}

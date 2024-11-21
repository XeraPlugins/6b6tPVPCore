package me.ian;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import lombok.SneakyThrows;
import me.ian.PVPHelper;

import java.io.*;
import java.nio.file.Files;

/**
 * @author SevJ6
 */
public class Config {

    private final File configFile;
    // No need to add getters/setters for these

    @Getter
    private Toml toml;

    // Constructor to create or load a new TOML config
    public Config(String name) {
        File dataFolder = PVPHelper.INSTANCE.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        this.configFile = new File(dataFolder, name);
        try {
            if (configFile.exists()) {
                loadConfig();
            } else {
                // Load and save the default config
                InputStream resourceStream = PVPHelper.class.getResourceAsStream("/" + name);
                if (resourceStream == null) throw new FileNotFoundException("Default config resource not found!");

                Files.copy(resourceStream, configFile.toPath());
                loadConfig(); // Load the copied file
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Method to load an existing TOML config
    @SneakyThrows
    public void loadConfig() {
        try (FileReader fileReader = new FileReader(configFile)) {
            this.toml = new Toml().read(fileReader);
        }
    }

    // Save the config back to the TOML config
    @SneakyThrows
    public void saveConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            // Read original file into a string
            StringBuilder originalContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                originalContent.append(line).append(System.lineSeparator());
            }

            // Parse the TOML as a map and write back using TomlWriter
            TomlWriter tomlWriter = new TomlWriter();
            StringWriter writer = new StringWriter();
            tomlWriter.write(toml.toMap(), writer);

            // Replace original file content while keeping original formatting
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(configFile))) {
                fileWriter.write(originalContent.toString());
                fileWriter.append(writer.toString());
            }
        }
    }
}

package com.github.mrgeotech.dynamicbungee.config;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final DynamicBungee main;
    private Configuration config;

    public ConfigHandler(DynamicBungee main) {
        this.main = main;
    }

    /**
     * Used to load the saved config to memory
     */
    public void load() {
        try {
            File file = new File(main.getDataFolder().getAbsolutePath(), "config.yml");
            file.getParentFile().mkdirs();
            System.out.print(file.getAbsolutePath());
            if (file.createNewFile()) {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                saveDefaults();
            } else {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to save the config that is stored in memory to the disk
     */
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(main.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to save the default config with all the default values
     */
    public void saveDefaults() {
        config.set("defaults.template-name", "default");
        config.set("defaults.template-server-type", "paper");
        config.set("defaults.template-server-version", "1.16.5");
        config.set("defaults.server-port", 25566);

        save();
    }

    /**
     * Gets the current config that is stored in memory
     *
     * @return The configuration
     */
    public Configuration getConfig() {
        return config;
    }

}

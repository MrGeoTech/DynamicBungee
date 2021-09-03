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
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(main.getDataFolder(), "/DynamicBungee/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to save the config that is stored in memory to the disk
     */
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(main.getDataFolder(), "/DynamicBungee/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to save the default config with all the default values
     */
    public void saveDefaults() {
        if (new File(main.getDataFolder(), "/DynamicBungee/config.yml").exists()) return;

        config.set("default-template-name", "default");
        config.set("", "");
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

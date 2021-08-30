package com.github.mrgeotech.dynamicbungee.servers;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;
import com.github.mrgeotech.dynamicbungee.loader.DynamicLoader;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;

public class ServerTemplate {

    public static ServerTemplate DEFAULT_TEMPLATE;

    private String name;
    private File parentDirectory;
    private File templateDirectory;

    public ServerTemplate(String name, File parentDirectory, File templateDirectory) {
        this.name = name;
        this.parentDirectory = parentDirectory;
        this.templateDirectory = templateDirectory;
    }

    // Used to initialise the default server template
    public static void init(DynamicBungee main) {
        DEFAULT_TEMPLATE = main.getDynamicLoader()
                .createBlankServerTemplate(
                        ServerTemplate.createTemplate("default")
                );
    }

    public static ServerTemplate createTemplate(String name) {
        return new ServerTemplate(name,
                new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/" + name),
                new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/" + name + "/" + name + "-template")
                );
    }

    public String getName() {
        return name;
    }

    public File getParentDirectory() {
        return parentDirectory;
    }

    public File getTemplateDirectory() {
        return templateDirectory;
    }

    public String getParentLocation() {
        return parentDirectory.getAbsolutePath();
    }

    public String getTemplateLocation() {
        return  templateDirectory.getAbsolutePath();
    }

}

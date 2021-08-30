package com.github.mrgeotech.dynamicbungee.loader;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;
import com.github.mrgeotech.dynamicbungee.Utils;
import com.github.mrgeotech.dynamicbungee.servers.Server;
import com.github.mrgeotech.dynamicbungee.servers.ServerHandler;
import com.github.mrgeotech.dynamicbungee.servers.ServerTemplate;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;

public class DynamicLoader {

    private DynamicBungee main;
    private static int port;
    private String motd;

    public DynamicLoader(DynamicBungee main) {
        this.main = main;
        DynamicLoader.port = 25566;
        this.motd = "A dynamically created server!";
    }

    public Server createServer(final ServerTemplate template) {
        final Server server = new Server("creating", new File(""), template);
        ProxyServer.getInstance().getScheduler().runAsync(main, () -> {
            try {
                downloadServer(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return server;
    }

    public ServerHandler startServer(Server server, boolean haveOutput) {
        if (!server.getName().equalsIgnoreCase("creating")) {
            ServerHandler handler = new ServerHandler(server, haveOutput);
            ProxyServer.getInstance().getScheduler().runAsync(main, handler);
            server.setHandler(handler);
            return handler;
        } else {
            return null;
        }
    }

    public ServerTemplate createBlankServerTemplate(final ServerTemplate template) {
        ProxyServer.getInstance().getScheduler().runAsync(main, () -> {
            try {
                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Creating new server template...").create());
                downloadServerTemplate("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/786/downloads/paper-1.16.5-786.jar", template);
                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("New server template created!").create());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return template;
    }

    public void deleteServer(Server server) {
        ProxyServer.getInstance().getScheduler().runAsync(main, () -> {
            Utils.deleteChildren(server.getDirectory());
            server.getDirectory().delete();
        });
    }

    public void loadAll() {
        ServerTemplate template;
        int port = DynamicLoader.port;
        for (File file : Utils.getChildrenFile(new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/"))) {
            template = ServerTemplate.createTemplate(file.getName());
            main.addTemplate(template);
            for (int i = 0; i < Utils.getChildrenFile(file).length; i++) {
                File file1 = Utils.getChildrenFile(file)[i];
                if (!file1.getName().contains("-template")) {
                    if (Utils.getPortFromDirectory(file1) > port) {
                        port = Utils.getPortFromDirectory(file1);
                    }
                    main.addServer(new Server(file1.getName(), file1, template),
                            new InetSocketAddress("0.0.0.0", Utils.getPortFromDirectory(file1)),
                            motd,
                            false);
                }
            }
        }
        DynamicLoader.port = port;
    }

    // Used to download and create a new server
    public boolean downloadServer(Server server) throws IOException {
        ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Creating server...").create());

        ServerTemplate template = server.getTemplate();

        // Getting the children directories that contain each server. They are numbered for labels
        String[] directories = Utils.getChildren(template.getParentDirectory());
        int i = directories != null ? directories.length : 0;
        new File(template.getParentLocation() + "/" + template.getName() + i).mkdirs();
        server.setDirectory(new File(template.getParentLocation() + "/" + template.getName() + i));

        Utils.copyDirectory(template.getTemplateLocation(), server.getDirectory().getAbsolutePath());

        File file = new File(server.getDirectory(), "/server.properties");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(String.format("#Minecraft server properties\n" +
                "#Mon Jul 19 16:57:48 CDT 2021\n" +
                "enable-jmx-monitoring=false\n" +
                "rcon.port=25575\n" +
                "level-seed=\n" +
                "gamemode=survival\n" +
                "enable-command-block=false\n" +
                "enable-query=false\n" +
                "generator-settings=\n" +
                "level-name=world\n" +
                "motd=A Minecraft Server\n" +
                "query.port=25565\n" +
                "pvp=true\n" +
                "generate-structures=true\n" +
                "difficulty=normal\n" +
                "network-compression-threshold=256\n" +
                "max-tick-time=60000\n" +
                "max-players=20\n" +
                "use-native-transport=true\n" +
                "online-mode=false\n" +
                "enable-status=true\n" +
                "allow-flight=false\n" +
                "broadcast-rcon-to-ops=true\n" +
                "view-distance=10\n" +
                "max-build-height=256\n" +
                "server-ip=\n" +
                "allow-nether=true\n" +
                "server-port=%s\n" +
                "enable-rcon=false\n" +
                "sync-chunk-writes=true\n" +
                "op-permission-level=4\n" +
                "prevent-proxy-connections=false\n" +
                "resource-pack=\n" +
                "entity-broadcast-range-percentage=100\n" +
                "rcon.password=\n" +
                "player-idle-timeout=0\n" +
                "debug=false\n" +
                "force-gamemode=false\n" +
                "rate-limit=0\n" +
                "hardcore=false\n" +
                "white-list=false\n" +
                "broadcast-console-to-ops=true\n" +
                "spawn-npcs=true\n" +
                "spawn-animals=true\n" +
                "snooper-enabled=true\n" +
                "function-permission-level=2\n" +
                "level-type=default\n" +
                "text-filtering-config=\n" +
                "spawn-monsters=true\n" +
                "enforce-whitelist=false\n" +
                "resource-pack-sha1=\n" +
                "spawn-protection=16\n" +
                "max-world-size=29999984\n", DynamicLoader.port));
        writer.close();

        // Fixing the server variable
        server.setName(template.getName() + i);
        main.addServer(server, new InetSocketAddress("localhost", port), motd, false);
        port++;
        ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server created!").create());
        return true;
    }

    // Used to download and create a new server template
    public boolean downloadServerTemplate(String url, ServerTemplate template) throws IOException {
        if (!new File(template.getTemplateLocation()).mkdirs()) return true;
        // Making the needed files for an instant start up
        File file = new File(template.getTemplateLocation() + "/plugins/");
        file.mkdirs();
        file = new File(template.getTemplateLocation() + "/eula.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).\n" +
                "#You also agree that tacos are tasty, and the best food in the world.\n" +
                "#Thu Apr 01 12:45:56 CDT 2021\n" +
                "eula=true\n");
        writer.close();

        return downloadFile(url, template.getTemplateLocation() + "/server.jar");
    }

    public boolean downloadFile(String url, String location) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(location)) {
            new File(location).createNewFile();
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0 ,1024)) != -1) {
                fileOutputStream.write(data, 0, byteContent);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}

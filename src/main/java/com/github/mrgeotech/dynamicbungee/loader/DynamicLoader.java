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
        DynamicLoader.port = 25567;
        this.motd = "A dynamically created server!";
    }

    /**
     * Creates a server from a template using the template
     *
     * @param template The template to copy
     * @return The server container
     */
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

    /**
     * Starts a server from a given server container
     * 
     * @param server Server that is going to be started
     * @param haveOutput Whether the server should output its console to the proxy console
     * @return Returns the server handler that is handling the I/O of the server process
     */
    public ServerHandler startServer(Server server, boolean haveOutput) {
        if (!server.getName().equalsIgnoreCase("creating")) {
            ServerHandler handler = new ServerHandler(server, haveOutput);
            try {
                Utils.updatePort(server.getDirectory(), port);
                ProxyServer.getInstance().getScheduler().runAsync(main, handler);
                server.setHandler(handler);
                return handler;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * Creates a blank server template from a template container
     *
     * @param template The template container to use for creation
     * @return The template used
     */
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

    /**
     * Deletes all of a servers files
     *
     * @param server The server to delete
     */
    public void deleteServer(Server server) {
        ProxyServer.getInstance().getScheduler().runAsync(main, () -> {
            Utils.deleteChildren(server.getDirectory());
            server.getDirectory().delete();
        });
    }

    /**
     * Loads all the servers that are stored on the disk
     */
    public void loadAll() {
        ServerTemplate template;
        for (File file : Utils.getChildrenFile(new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/"))) {
            if (file.getName().equalsIgnoreCase("main")) {
                continue;
            }
            template = ServerTemplate.createTemplate(file.getName());
            main.addTemplate(template);
            for (int i = 0; i < Utils.getChildrenFile(file).length; i++) {
                File file1 = Utils.getChildrenFile(file)[i];
                if (!file1.getName().contains("-template")) {
                    try {
                        Utils.updatePort(file1, port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    main.addServer(new Server(file1.getName(), file1, template),
                            new InetSocketAddress("0.0.0.0", port),
                            motd,
                            false);
                    port++;
                }
            }
        }
    }

    /**
     * Starts a server on port 25566 to act as a default server for people to connect to
     *
     * @return The server that was started
     */
    public Server startDefaultServer() throws IOException {
        Server server = new Server("main", new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/main"), ServerTemplate.DEFAULT_TEMPLATE);

        if (new File(ProxyServer.getInstance().getPluginsFolder(), "/DynamicBungee/server/paperspigot/main").mkdirs()) {
            Utils.copyDirectory(server.getTemplate().getTemplateLocation(), server.getDirectory().getAbsolutePath());

            File file = new File(server.getDirectory(), "/server.properties");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("#Minecraft server properties\n" +
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
                    "server-port=25566\n" +
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
                    "max-world-size=29999984\n");
            writer.close();
        }

        port = 25566;
        startServer(server, false);
        return server;
    }

    /**
     * Used to copy server files from a template to a new server location
     *
     * @param server The server container that is getting created
     * @return Returns if the server has been successfully copied
     */
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
        writer.write("#Minecraft server properties\n" +
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
                "server-port=000000\n" +
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
                "max-world-size=29999984\n");
        writer.close();

        // Fixing the server variable
        server.setName(template.getName() + i);
        main.addServer(server, new InetSocketAddress("localhost", port), motd, false);
        port++;
        ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server created!").create());
        return true;
    }

    /**
     * Creates new server template files.
     * This includes downloading a server jar and creating a OKed eula file
     *
     * @param url Downloads the jar from this url
     * @param template The template container for the server being downloaded
     * @return Whether the download of the server jar was successful
     */
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

    /**
     * Used to download a file from an url to a specified location
     *
     * @param url Url of the file to download
     * @param location Location to output the file once downloaded
     * @return Whether the file was downloaded successfully
     */
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

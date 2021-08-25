package com.github.mrgeotech.dynamicbungee;

import com.github.mrgeotech.dynamicbungee.commands.LoaderCommand;
import com.github.mrgeotech.dynamicbungee.loader.DynamicLoader;
import com.github.mrgeotech.dynamicbungee.servers.Server;
import com.github.mrgeotech.dynamicbungee.servers.ServerTemplate;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class DynamicBungee extends Plugin {

    private Map<String,ServerTemplate> templates;
    private Map<String,Server> servers;
    private DynamicLoader loader;

    @Override
    public void onEnable() {
        // Initiating the variables
        loader = new DynamicLoader(this);
        templates = new HashMap<>();
        servers = new HashMap<>();

        // Creating the default template
        ServerTemplate.init(this);
        templates.put("default", ServerTemplate.DEFAULT_TEMPLATE);

        // Registering commands
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LoaderCommand(this));

        // Loading in a servers that were already there
        loader.loadPreCreatedServers(ServerTemplate.DEFAULT_TEMPLATE);
    }

    @Override
    public void onDisable() {
        for (String server : servers.keySet()) {
            servers.get(server).getHandler().stop();
        }
    }

    public DynamicLoader getDynamicLoader() {
        return loader;
    }

    public ServerTemplate getTemplate(String name) {
        return templates.get(name);
    }

    public void addTemplate(ServerTemplate template) {
        templates.put(template.getName(), template);
    }

    public void addServer(Server server, InetSocketAddress address, String motd, boolean restricted) {
        ProxyServer.getInstance().getServers().put(server.getName(), ProxyServer.getInstance().constructServerInfo(server.getName(), address, motd, restricted));
        servers.put(server.getName(), server);
    }

    public Server getServer(String name) {
        return servers.get(name);
    }

    public boolean containsServer(String name) {
        return servers.containsKey(name);
    }

    public static void removeServer(String name) {
        ProxyServer.getInstance().getServerInfo(name).getPlayers().iterator().forEachRemaining(proxiedPlayer -> proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("lobby"), ServerConnectEvent.Reason.LOBBY_FALLBACK));
        ProxyServer.getInstance().getServers().remove(name);
    }

}

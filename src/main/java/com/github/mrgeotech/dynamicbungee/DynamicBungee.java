package com.github.mrgeotech.dynamicbungee;

import com.github.mrgeotech.dynamicbungee.commands.LoaderCommand;
import com.github.mrgeotech.dynamicbungee.loader.DynamicLoader;
import com.github.mrgeotech.dynamicbungee.servers.Server;
import com.github.mrgeotech.dynamicbungee.servers.ServerTemplate;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class DynamicBungee extends Plugin {

    private Map<String,ServerTemplate> templates;
    private Map<String,Server> servers;
    private DynamicLoader loader;

    @Override
    public void onEnable() {
        loader = new DynamicLoader(this);
        templates = new HashMap<>();
        servers = new HashMap<>();
        ServerTemplate.init(this);
        templates.put("default", ServerTemplate.DEFAULT_TEMPLATE);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LoaderCommand(this));
    }

    @Override
    public void onDisable() {
        for (String server : servers.keySet()) {
            servers.get(server).delete(this);
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
        for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
            p.disconnect(new ComponentBuilder("This server was forcefully closed.").create());
        }
        ProxyServer.getInstance().getServers().remove(name);
    }

}

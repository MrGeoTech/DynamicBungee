package com.github.mrgeotech.dynamicbungee.servers;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;

import java.io.File;

public class Server {

    private String name;
    private File directory;
    private ServerTemplate template;
    private ServerHandler handler;

    private boolean running;

    public Server(String name, File directory, ServerTemplate template) {
        this.name = name;
        this.directory = directory;
        this.template = template;
        this.running = false;
        this.handler = null;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized File getDirectory() {
        return directory;
    }

    public synchronized ServerTemplate getTemplate() {
        return template;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized void setDirectory(File directory) {
        this.directory = directory;
    }

    public synchronized void setTemplate(ServerTemplate template) {
        this.template = template;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized void setHandler(ServerHandler handler) {
        this.handler = handler;
    }

    public synchronized ServerHandler getHandler() {
        return handler;
    }

    public void delete(DynamicBungee main) {
        if (this.isRunning())
            this.handler.stop();
        while (this.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
        main.getDynamicLoader().deleteServer(this);
        main.removeServer(name);
    }

    public void stop() {
        if (handler != null) {
            handler.stop();
        }
    }

}

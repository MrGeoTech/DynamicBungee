package com.github.mrgeotech.dynamicbungee.servers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ServerHandler implements Runnable {

    private Server server;
    private short state;
    private String command;
    private boolean haveOutput;

    public ServerHandler(Server server, boolean haveOutput) {
        this.server = server;
        this.state = -1;
        this.haveOutput = haveOutput;
    }

    @Override
    public void run() {
        this.server.setRunning(true);

        ProcessBuilder builder = new ProcessBuilder("java", "-jar", "server.jar", "nogui");
        builder.directory(server.getDirectory());

        ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Starting server...").create());
        try {
            // Starts the server
            Process process = builder.start();

            // Getting the input and output streams
            BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            OutputStream input = process.getOutputStream();
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            try {
                while (state != 3) {
                    switch (state) {
                        case -1:
                            if (haveOutput) {
                                String line;
                                while (!(line = output.readLine()).contains("Timings Reset")) {
                                    ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.WHITE).append(line).create());
                                }
                            } else {
                                while (!output.readLine().contains("Timings Reset")) {
                                }
                            }
                            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server started!").create());
                            state = 0;
                            break;
                        case 0:
                            if (error.ready()) {
                                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.RED).append(error.readLine()).create());
                            }
                            if (haveOutput && output.ready()) {
                                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.WHITE).append(output.readLine()).create());
                            }
                            break;
                        case 1:
                            byte[] bytes = (command + "\n").getBytes();
                            input.write(bytes);
                            input.flush();
                            Thread.sleep(100);
                            while (output.ready()) {
                                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.WHITE).append(output.readLine()).create());
                            }
                            state = 0;
                            break;
                        case 2:
                            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Shutting down server..").create());
                            input.write("stop\n".getBytes());
                            input.flush();
                            try {
                                if (haveOutput) {
                                    String line;
                                    while (!(line = output.readLine()).contains("Closing Server")) {
                                        ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.WHITE).append(line).create());
                                    }
                                } else {
                                    while (!output.readLine().contains("Closing Server")) {}
                                }
                            } catch (IOException ignore) {
                            }
                            state = 3;
                            break;
                    }
                    Thread.sleep(100);
                }
            } catch (Exception ignore1) {}

            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server is closed!").create());
            server.setRunning(false);
            output.close();
            input.close();
            error.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runCommand(String command) {
        if (command.equalsIgnoreCase("stop")) {
            this.state = 2;
        } else {
            this.command = command;
            this.state = 1;
        }
    }

    public void stop() {
        this.state = 2;
    }

}

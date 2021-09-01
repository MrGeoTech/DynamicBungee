package com.github.mrgeotech.dynamicbungee.servers;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;
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
                        // When the server is starting
                        case -1:
                            String line;
                            try {
                                while (!(line = output.readLine()).contains("Timings Reset")) {
                                    color(line);
                                }
                            } catch (Exception ig) {
                                state = 2;
                                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server is trying to start on an already used port!!").create());
                                break;
                            }
                            line = null;
                            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Server started!").create());
                            state = 0;
                            break;
                        // When the server is running normally
                        case 0:
                            if (error.ready()) {
                                ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.RED).append(error.readLine()).create());
                            }
                            if (output.ready()) {
                                color(output.readLine());
                            }
                            break;
                        // When issuing a command to the server
                        case 1:
                            byte[] bytes = (command + "\n").getBytes();
                            input.write(bytes);
                            input.flush();
                            Thread.sleep(100);
                            while (output.ready()) {
                                color(output.readLine());
                            }
                            state = 0;
                            break;
                        // When the server is getting stopped
                        case 2:
                            DynamicBungee.kickPlayersOn(server.getName());
                            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("Shutting down server..").create());
                            input.write("stop\n".getBytes());
                            input.flush();
                            try {
                                while (true) {
                                    line = output.readLine();
                                    if (line.contains("Closing Server")) {
                                        break;
                                    }
                                    color(line);
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            state = 3;
                            break;
                    }
                    // Giving it some wait to make it not super resource intensive
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    public void color(String input) {
        if (haveOutput && input.contains("INFO")) {
            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.WHITE).append(input).create());
        } else if (input.contains("WARNING")) {
            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.YELLOW).append(input).create());
        } else if (input.contains("SEVERE")) {
            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("").color(ChatColor.DARK_AQUA).append("{" + server.getName() + "} ").color(ChatColor.RED).append(input).create());
        }
    }

}

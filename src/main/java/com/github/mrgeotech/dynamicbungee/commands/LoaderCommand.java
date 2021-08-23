package com.github.mrgeotech.dynamicbungee.commands;

import com.github.mrgeotech.dynamicbungee.DynamicBungee;
import com.github.mrgeotech.dynamicbungee.servers.ServerTemplate;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class LoaderCommand extends Command {

    private DynamicBungee main;

    public LoaderCommand(DynamicBungee main) {
        super("loader", "admin.loader");
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("Improper arguments!").create());
        } else if (args[0].equalsIgnoreCase("create")) {
            ServerTemplate template = main.getTemplate(args[1]);
            if (template != null) {
                main.getDynamicLoader().createServer(template);
            } else {
                sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("You must use an already created template!").create());
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            if (main.containsServer(args[1])) {
                if (args.length == 3) {
                    main.getServer(args[1]).setHandler(main.getDynamicLoader().startServer(main.getServer(args[1]), args[2].equalsIgnoreCase("true")));
                } else {
                    main.getServer(args[1]).setHandler(main.getDynamicLoader().startServer(main.getServer(args[1]), false));
                }
            } else {
                sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("You must start a server that has already been initiated!").create());
            }
        } else if (args[0].equalsIgnoreCase("send") && args.length >= 3) {
            if (main.containsServer(args[1]) && main.getServer(args[1]).isRunning()) {
                StringBuilder command = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    command.append(args[i]).append(" ");
                }
                if (command.toString().equalsIgnoreCase("stop ")) {
                    sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("Improper usage! Please use \"/loader stop " + args[2] + "\" in the future!").create());
                    main.getServer(args[1]).getHandler().stop();
                } else {
                    main.getServer(args[1]).getHandler().runCommand(command.toString());
                }
            } else {
                sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("You must send command to servers that are started!").create());
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (main.containsServer(args[1]))
                main.getServer(args[1].toLowerCase()).getHandler().stop();
            else
                sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("You can only stop servers that are started!").create());
        } else if (args[0].equalsIgnoreCase("createTemplate")) {
            if (main.getTemplate(args[1]) == null) {
                main.addTemplate(main.getDynamicLoader().createBlankServerTemplate(ServerTemplate.createTemplate(args[1])));
            } else {
                sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("Template already exists!").create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("Improper arguments!").create());
        }
    }

}

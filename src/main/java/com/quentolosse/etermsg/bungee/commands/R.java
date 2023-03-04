package com.quentolosse.etermsg.bungee.commands;

import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class R extends Command implements TabExecutor {

    Msg commandemsg;

    public R(Msg commandemsg){

        super("R");
        this.commandemsg = commandemsg;

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        
        String[] ret = {"<message>"};
        return Arrays.asList(ret);

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        if (commandemsg.lastMsg.containsKey(sender.getName())) {

            String[] newArgs = new String[args.length + 1];
            newArgs[0] =  commandemsg.lastMsg.get(sender.getName());

            for (int i = 0; i < args.length; i++) {
                newArgs[i + 1] = args[i];
            }

            commandemsg.execute(sender, newArgs);
        }
        else {
            sender.sendMessage(new ComponentBuilder("Utilisez cette commande après avoir reçu ou envoyé un message avec /msg").color(ChatColor.RED).create());
        }

    }
    
    
}

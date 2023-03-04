package com.quentolosse.etermsg.bungee.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.quentolosse.etermsg.bungee.MsgEventListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

public class Msg extends Command implements TabExecutor {

    String sendFormat, chatFormat, pseudoFormat, receiveFormat;
    MsgEventListener listener;
    Map<String,String> lastMsg = Collections.emptyMap();

    public Msg(Configuration config, MsgEventListener listener){

        super("Msg");
        this.listener = listener;
        this.sendFormat = config.getString("msg-send").replace("&", "§");
        this.receiveFormat = config.getString("msg-receive").replace("&", "§");
        this.chatFormat = config.getString("chat-format").replace("&", "§");
        this.pseudoFormat = config.getString("pseudo-format").replace("&", "§");

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        
        if (args.length == 1) {
            
            Set<String> joueurs =  new HashSet<String>();;
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){

                if (player.getName().startsWith(args[0])) {

                    joueurs.add(player.getName());
                    
                }

            }

            String[] ret = joueurs.toArray(new String[joueurs.size()]);

            return Arrays.asList(ret);
        }
        else{

            String[] ret = {"<player> <message>"};
            return Arrays.asList(ret);

        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        if (args.length <= 1) {sender.sendMessage(new ComponentBuilder("§cUtilisation de la commande : /msg <player> <message>").create()); return;}
        ProxiedPlayer targetPlayer;
        try {
            targetPlayer = ProxyServer.getInstance().getPlayer(args[0]);
        } catch (Exception e) {
            sender.sendMessage(new ComponentBuilder("§cCe joueur n'existe pas ou n'est pas en ligne").create()); 
            return;
        }
        if (targetPlayer == null) {
            sender.sendMessage(new ComponentBuilder("§cCe joueur n'existe pas ou n'est pas en ligne").create());
            return;
        }

        if (lastMsg.containsKey(sender.getName())) {
            lastMsg.remove(sender.getName());
        }
        lastMsg.put(sender.getName(), targetPlayer.getName());

        if (lastMsg.containsKey(targetPlayer.getName())) {
            lastMsg.remove(targetPlayer.getName());
        }
        lastMsg.put(targetPlayer.getName(), sender.getName());

        String message = "";
        for (int i = 1; i < args.length; i++){
            String arg = (args[i] + " ");
            message = (message + arg);
        }

        targetPlayer.sendMessage(new ComponentBuilder(receiveFormat.replace("%{player}", sender.getName()).replace("%{message}", message)).create());
        sender.sendMessage(new ComponentBuilder(sendFormat.replace("%{player}", targetPlayer.getName()).replace("%{message}", message)).create());

    }
    
    
}

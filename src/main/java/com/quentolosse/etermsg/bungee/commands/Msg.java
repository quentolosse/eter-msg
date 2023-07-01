package com.quentolosse.etermsg.bungee.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.quentolosse.etermsg.bungee.MsgEventListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

public class Msg extends Command implements TabExecutor {

    String sendFormat, chatFormat, pseudoFormat, receiveFormat, mdLogFormat, consoleLogFormat;
    MsgEventListener listener;
    Map<String,String> lastMsg = Maps.newHashMap();


    public Msg(Configuration config, MsgEventListener listener){

        super("Msg");
        this.listener = listener;
        this.sendFormat = config.getString("msg-send").replace("&", "§");
        this.receiveFormat = config.getString("msg-receive").replace("&", "§");
        this.chatFormat = config.getString("chat-format").replace("&", "§");
        this.pseudoFormat = config.getString("pseudo-format").replace("&", "§");
        this.mdLogFormat = config.getString("msg-md-log-format").replace("&", "§");
        this.consoleLogFormat = config.getString("msg-console-log-format").replace("&", "§");

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        
        if (args.length == 1) {
            
            Set<String> joueurs = new HashSet<>();
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){

                if (player.getName().startsWith(args[0])) {

                    joueurs.add(player.getName());
                    
                }

            }

            String[] ret = joueurs.toArray(new String[0]);

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

        String logConsole = consoleLogFormat.replace("%{sender}", sender.getName()).replace("%{receiver}", targetPlayer.getName()).replace("%{message}", message);
        
        System.out.println(logConsole);
        String log = mdLogFormat.replace("%{sender}", sender.getName()).replace("%{receiver}", targetPlayer.getName()).replace("%{message}", message);

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("chatLog.md", true));
            bw.write(log);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        targetPlayer.sendMessage(new ComponentBuilder(receiveFormat.replace("%{player}", sender.getName()).replace("%{message}", message)).create());
        sender.sendMessage(new ComponentBuilder(sendFormat.replace("%{player}", targetPlayer.getName()).replace("%{message}", message)).create());

    }
    
    
}

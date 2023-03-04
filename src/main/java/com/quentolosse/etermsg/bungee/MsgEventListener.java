package com.quentolosse.etermsg.bungee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;


public class MsgEventListener implements Listener{

    private class group{

        boolean shared;
        List<String> serveurs;

        public group(boolean shared, List<String> serveurs){
            this.shared = shared;
            this.serveurs = serveurs;
        }
        

    }

    String joinFormat, leaveFormat, defaultPrefix, chatFormat, pseudoFormat, logFormat, consoleFormat;
    Map<String, group> groupesServeurs = Collections.emptyMap();
    Map<String, String> groupeDuServeur = Collections.emptyMap();

    LuckPerms api;

    public MsgEventListener (Configuration config, LuckPerms api){

        this.joinFormat = config.getString("join-format").replace("&", "§");
        this.leaveFormat = config.getString("leave-format").replace("&", "§");
        this.defaultPrefix = config.getString("players-prefix").replace("&", "§");
        this.chatFormat = config.getString("chat-format").replace("&", "§");
        this.pseudoFormat = config.getString("pseudo-format").replace("&", "§");
        this.logFormat = config.getString("md-log-format");
        this.consoleFormat = config.getString("console-log-format");

        List<?> temp = config.getList("groups");
        
        
        ArrayList<LinkedHashMap> groupes = (ArrayList<LinkedHashMap>)temp; 

        for (LinkedHashMap groupe : groupes) {

            String nomDuGroupe = (String)groupe.keySet().toArray()[0];
            LinkedHashMap groupeSansNom = (LinkedHashMap)groupe.get(nomDuGroupe);

            boolean shared = (boolean)groupeSansNom.get("shared");
            List<String> serveurs = (List<String>)groupeSansNom.get("servers");

            this.groupesServeurs.put(nomDuGroupe, new group(shared, serveurs));
            for (String name : serveurs) {
                this.groupeDuServeur.put(name, nomDuGroupe);
            }

        }

        this.api = api;
    }

    
    @EventHandler
    public void onLogin(final PostLoginEvent event){
        ProxiedPlayer player = event.getPlayer();
        CachedMetaData metaData = this.api.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
        String prefix = metaData.getPrefix().replace("&", "§");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (ProxyServer.getInstance().getPlayers().contains(player)){

            String toSend = this.joinFormat.replace("%{prefix}", prefix).replace("%{player}", player.getName());
            ProxyServer.getInstance().broadcast(new ComponentBuilder(toSend).create());

        }
        else{
            System.out.println("Silent join : " + player.getName());
        }
    }

    @EventHandler
    public void onLeave(final PlayerDisconnectEvent event){

        ProxiedPlayer player = event.getPlayer();

        CachedMetaData metaData = this.api.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
        String prefix = metaData.getPrefix().replace("&", "§");

        String toSend = this.leaveFormat.replace("%{prefix}", prefix).replace("%{player}", player.getName());
        ProxyServer.getInstance().broadcast(new ComponentBuilder(toSend).create());
        
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {

        if (! event.getTag().equals("chatmsg:msg")) {
            return;
        }

        if (!(event.getSender() instanceof Server))
        {
            return;
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
        DataInputStream in = new DataInputStream(stream);
        
        ProxiedPlayer player;
        String message;

        try {
            player = ProxyServer.getInstance().getPlayer(in.readUTF());
            message = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
            
        CachedMetaData metaData = this.api.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
        String prefix = metaData.getPrefix().replace("&", "§");

        String serveur = player.getServer().getInfo().getName();
        Collection<ProxiedPlayer> receiversDebut = player.getServer().getInfo().getPlayers();
        Set<ProxiedPlayer> receivers = new HashSet<ProxiedPlayer>(receiversDebut);

        try{
            if (this.groupesServeurs.get(this.groupeDuServeur.get(serveur)).shared) {
                for (String name : this.groupesServeurs.get(this.groupeDuServeur.get(serveur)).serveurs) {
                    if (name != serveur) {
                        Collection<ProxiedPlayer> receiversTemp = ProxyServer.getInstance().getServers().get(name).getPlayers();
                        for (ProxiedPlayer p : receiversTemp) {
                            receivers.add(p);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            return;
        }

        String logConsole = consoleFormat.replace("%{serveur}", serveur.substring(0, 3).toUpperCase()).replace("%{prefix}", prefix).replace("%{player}", player.getName()).replace("%{message}", message);
        System.out.println(logConsole);

        String log = logFormat.replace("%{serveur}", serveur.substring(0, 3).toUpperCase()).replace("%{prefix}", prefix).replace("%{player}", player.getName()).replace("%{message}", message);

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader("chatLog.md"));
            bw = new BufferedWriter(new FileWriter("chatLog_temp.md"));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line+"\n");
            }
            bw.write(log);
        } catch (Exception e) {
            return;
        } finally {
            try {
                if(br != null)
                br.close();
            } catch (IOException e) {
                //
            }
            try {
                if(bw != null)
                bw.close();
            } catch (IOException e) {
                //
            }
        }

        File oldFile = new File("chatLog.md");
        oldFile.delete();

        File newFile = new File("chatLog_temp.md");
        newFile.renameTo(oldFile);


        for(ProxiedPlayer receiver : receivers){
            
            String messagePseudoFormat = new String();
            if(message.toLowerCase().contains(receiver.getName().toLowerCase())){
                String[] messageWithoutMentions = message.toLowerCase().split(receiver.getName().toLowerCase());
                String messageWithNoMentions = new String();
                for (String part : messageWithoutMentions) {
                    messageWithNoMentions = messageWithNoMentions.concat(part);
                }
                int i = 0; // déso pour ce nom pas très explicit, j'avais pas d'idées ¯\_(ツ)_/¯  ...  Comment ça personne ne lit mon code ?
                for (String part : messageWithoutMentions) {
                    messagePseudoFormat = messagePseudoFormat.concat(messageWithNoMentions.substring(i, i + part.length()));
                    messagePseudoFormat = messagePseudoFormat.concat(pseudoFormat);
                    i += part.length();
                }
                if (!message.toLowerCase().endsWith(receiver.getName().toLowerCase())) {
                    messagePseudoFormat = (String)messagePseudoFormat.subSequence(0, messagePseudoFormat.length() - pseudoFormat.length());
                }
            }
            else {
                messagePseudoFormat = message;
            }
                
            String messageMention = messagePseudoFormat.replace("%{pseudo}", receiver.getName());
            String toSend = chatFormat.replace("%{prefix}", prefix).replace("%{pseudo}", player.getName()).replace("%{message}", messageMention);
            receiver.sendMessage(new ComponentBuilder(toSend).create());
        }
           
    }


}

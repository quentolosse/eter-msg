package com.quentolosse.etermsg.spigot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class SpigotMsgEventListener implements Listener{
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws IOException
    {

        if (event.isCancelled()) return;
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        out.writeUTF(event.getPlayer().getName());
        out.writeUTF(event.getMessage());

        event.getPlayer().sendPluginMessage(Main.get_instance(), "chatmsg:msg", stream.toByteArray());
     
        event.setCancelled(true);

    }

}

package com.quentolosse.etermsg.spigot;

import com.quentolosse.etermsg.spigot.SpigotMsgEventListener;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    private static main instance;

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new SpigotMsgEventListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(instance, "chatmsg:msg");
        instance = this;

    }

    public static main get_instance(){
        return instance;
    }

}
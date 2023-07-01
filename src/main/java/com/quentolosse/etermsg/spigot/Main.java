package com.quentolosse.etermsg.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {

        instance = this;
        getServer().getPluginManager().registerEvents(new SpigotMsgEventListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(instance, "chatmsg:msg");

    }

    public static Main get_instance(){
        return instance;
    }

}
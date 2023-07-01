package com.quentolosse.etermsg.bungee;

import java.io.File;
import java.io.IOException;

import com.quentolosse.etermsg.bungee.commands.Msg;
import com.quentolosse.etermsg.bungee.commands.R;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin{

    private Configuration configuration;


    @Override
    public void onEnable() {
        
        LuckPerms api = LuckPermsProvider.get();

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MsgEventListener listener = new MsgEventListener(configuration, api, this);
        Msg commandemsg = new Msg(configuration, listener);
        R commander = new R(commandemsg);

        ProxyServer.getInstance().registerChannel("chatmsg:msg");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, commandemsg);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, commander);
        ProxyServer.getInstance().getPluginManager().registerListener(this, listener);

    }


}

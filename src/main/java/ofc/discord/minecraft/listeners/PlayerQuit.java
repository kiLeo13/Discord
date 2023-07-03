package ofc.discord.minecraft.listeners;

import ofc.discord.minecraft.Minecraft;
import ofc.discord.minecraft.ServerStatus;
import ofc.discord.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        Discord.asyncUpdateStatus(ServerStatus.ONLINE);
        Discord.broadcast(player.getName() + " saiu do servidor", Minecraft.skin(player), null, Color.RED);
    }
}
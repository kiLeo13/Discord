package ofc.discord.minecraft.listeners;

import ofc.discord.minecraft.Minecraft;
import ofc.discord.minecraft.ServerStatus;
import ofc.discord.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        boolean playedBefore = player.hasPlayedBefore();
        Discord.asyncUpdateStatus(ServerStatus.ONLINE);

        String text = playedBefore
                ? String.format("%s entrou no servidor", player.getName())
                : String.format("%s entrou no servidor pela primeira vez", player.getName());

        Discord.broadcast(
                text,
                Minecraft.skin(player),
                null,
                playedBefore ? Color.GREEN : Color.PINK
        );
    }
}
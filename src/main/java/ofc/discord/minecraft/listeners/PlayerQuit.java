package ofc.discord.minecraft.listeners;

import ofc.discord.discord.DiscordBroadcaster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    private static final DiscordBroadcaster broadcaster = DiscordBroadcaster.getBroadcaster();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        broadcaster.send(player, player.getName() + " left the game", DiscordBroadcaster.EventType.QUIT);
    }
}
package ofc.discord.minecraft.listeners;

import ofc.discord.discord.DiscordBroadcaster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private static final DiscordBroadcaster broadcaster = DiscordBroadcaster.getBroadcaster();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        broadcaster.send(player, player.getName() + " joined the game", DiscordBroadcaster.EventType.JOIN);
    }
}
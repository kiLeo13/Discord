package ofc.discord.minecraft.listeners;

import ofc.discord.discord.DiscordBroadcaster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    private static final DiscordBroadcaster broadcaster = DiscordBroadcaster.getBroadcaster();

    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        broadcaster.send(player, event.getDeathMessage(), DiscordBroadcaster.EventType.DEATH);
    }
}
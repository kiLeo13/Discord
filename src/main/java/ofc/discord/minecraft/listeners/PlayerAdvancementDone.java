package ofc.discord.minecraft.listeners;

import ofc.discord.discord.DiscordBroadcaster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PlayerAdvancementDone implements Listener {
    private static final DiscordBroadcaster broadcaster = DiscordBroadcaster.getBroadcaster();

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {

        Player player = event.getPlayer();

        String frame = event.getAdvancement().getDisplay().frame().name();

        broadcaster.send(player, advancement, DiscordBroadcaster.EventType.ADVANCEMENT);
    }
}
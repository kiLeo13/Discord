package ofc.discord.minecraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerRunCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        String[] args = message.split(" ");

        if (args[0].equalsIgnoreCase("/reload")) {
            player.sendRichMessage("<red><b>NEVER</b> run <gold>/reload<red> with <blue>[Discord]<red> plugin installed!\nYou should always restart/stop your server in this case.");
            event.setCancelled(true);
        }
    }
}
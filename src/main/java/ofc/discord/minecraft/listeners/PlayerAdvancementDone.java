package ofc.discord.minecraft.listeners;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.minecraft.locale.Language;
import ofc.discord.discord.DiscordBroadcaster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PlayerAdvancementDone implements Listener {
    private static final DiscordBroadcaster broadcaster = DiscordBroadcaster.getBroadcaster();

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {

        AdvancementDisplay display = event.getAdvancement().getDisplay();

        // nothing to display
        if (display == null) return;

        String key = display.frame().translationKey();
        String message = Language.getInstance().getOrDefault("chat.type.advancement." + key);
        broadcaster.send(event.getPlayer(), message, DiscordBroadcaster.EventType.ADVANCEMENT);
    }
}
package ofc.discord.minecraft.listeners;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ofc.discord.discord.Discord;
import ofc.discord.minecraft.Minecraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.awt.*;
import java.util.HashMap;

public class PlayerAdvancementDone implements Listener {
    private static final HashMap<String, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {

        Player player = event.getPlayer();
        AdvancementDisplay display = event.getAdvancement().getDisplay();
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        Component compMessage = event.message();

        // Wait, a player is able to do more than 1 advancement within a single second? WOW
        // Well, this was made to block fucking commands like /advancement grant everything
        if (!inCooldown(player, 1000))
            return;

        // Nothing to display
        if (compMessage == null)
            return;

        String title = serializer.serialize(compMessage)
                .replace("[", "")
                .replace("]", "");
        String description = display == null ? null : serializer.serialize(display.description());

        Discord.broadcast(title, Minecraft.skin(player), description, Color.YELLOW);
    }

    /**
     * The cooldown that the player advancement has triggered the event.
     *
     * @param player The player who triggered the event.
     * @param wait The amount of time you want to check whether has passed or not.
     * @return {@code true} if the {@code wait} time has passed, {@code false} otherwise.
     */
    private boolean inCooldown(Player player, int wait) {
        String uuid = player.getUniqueId().toString();
        long now = System.currentTimeMillis();

        if (!cooldown.containsKey(uuid)) {
            System.out.println("Returning true because the player is not present in the hashmap.");
            cooldown.put(uuid, now);
            return true;
        }

        long last = cooldown.get(uuid);

        if (now - last > wait) {
            System.out.println("Returning true because " + (now - last) + " is greater than 1000");
            cooldown.put(uuid, now);
            return true;
        }

        return false;
    }
}
package ofc.discord.minecraft.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ofc.discord.discord.Discord;
import ofc.discord.minecraft.Minecraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.kyori.adventure.text.Component;

import java.awt.*;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();
        Component deathMessage = event.deathMessage();

        // Nothing to display
        if (deathMessage == null)
            return;

        String text = PlainTextComponentSerializer.plainText().serialize(deathMessage);

        Discord.broadcast(text, Minecraft.skin(player), null, Color.BLACK);
    }
}
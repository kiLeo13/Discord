package ofc.discord.minecraft.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ofc.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSendMessage implements Listener {

    @EventHandler
    public void onMessageSend(AsyncChatEvent event) {

        Component message = event.message();
        Player player = event.getPlayer();
        String guildId = Discord.getPlugin().getConfig().getString("guild");
        String textChannelId = Discord.getPlugin().getConfig().getString("broadcast-channel");
        String content = MiniMessage.miniMessage().serialize(message);

        if (guildId == null || textChannelId == null) return;

        Guild guild = Discord.getJda().getGuildById(guildId);
        TextChannel channel = guild.getTextChannelById(textChannelId);

        String send = String.format("%s: %s", player.getName(), content);
        channel.sendMessage(send).queue(null, e -> {
            System.err.println("Could not broadcast message from Minecraft to Discord");
            e.printStackTrace();
        });
    }
}
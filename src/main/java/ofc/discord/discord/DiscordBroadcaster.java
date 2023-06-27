package ofc.discord.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.internal.utils.Checks;
import ofc.discord.Discord;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DiscordBroadcaster {
    private static DiscordBroadcaster instance;
    private static TextChannel broadcast;

    private DiscordBroadcaster() {}

    public static DiscordBroadcaster getBroadcaster() {
        if (instance == null) instance = new DiscordBroadcaster();
        return instance;
    }

    public void send(@NotNull Player player, String description, EventType eventType) {
        refreshData();
        MessageEmbed embed = getEmbed(player, description, eventType);

        broadcast.sendMessageEmbeds(embed).queue(null, e -> {
            System.err.println("Could not broadcast player event: " + eventType.name());
            e.printStackTrace();
        });
    }

    private MessageEmbed getEmbed(Player player, String description, EventType event) {
        final EmbedBuilder builder = new EmbedBuilder();

        String head = "https://cravatar.eu/helmavatar/" + player.getName() + "/64.png";

        builder
                .setAuthor(player.getName(), null, head)
                .setDescription(description)
                .setColor(event.color);

        return builder.build();
    }

    private void refreshData() {
        String channelId = Discord.getPlugin().getConfig().getString("broadcast-channel");

        Checks.notNull(channelId, "Channel ID");

        broadcast = Discord.getJda().getTextChannelById(channelId);

        Checks.notNull(broadcast, "Broadcast channel");
    }

    public enum EventType {
        DEATH(Color.BLACK),
        ADVANCEMENT(Color.YELLOW),
        JOIN(Color.GREEN),
        QUIT(Color.RED);

        final Color color;

        EventType(Color color) {
            this.color = color;
        }
    }
}
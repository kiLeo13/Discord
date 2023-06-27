package ofc.discord.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.internal.utils.Checks;
import ofc.discord.Discord;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;

public class DiscordStatus {
    private static EmbedBuilder builder = new EmbedBuilder();
    private static DiscordStatus instance;
    private static TextChannel status;

    private DiscordStatus() {}

    public static DiscordStatus getStatusUpdater() {
        if (instance == null) instance = new DiscordStatus();
        return instance;
    }

    public void update() {
        refreshData();

        String message = Discord.getPlugin().getConfig().getString("status-message");
        MessageEmbed embed = embed(status.getGuild());

    }

    private MessageEmbed embed(Guild guild) {
        builder.clear();

        FileConfiguration config = Discord.getPlugin().getConfig();
        Server server = Bukkit.getServer();
        int online = server.getOnlinePlayers().size();

        String url = config.getString("server-image");

        builder
                .setTitle("Minecraft Server")
                .setThumbnail(url == null || url.isBlank() ? null : url)
                .addField("🌵 Versão", "`" + server.getMinecraftVersion() + "`", true) // Version
                .addField("👥 Online", "`" + (online < 10 ? "0" + online : online) + "`", true)
                .addField("🌐 Status", "", true)
                .setDescription("Informações do servidor de Minecraft atualizadas em tempo real.")
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private void refreshData() {
        String statusId = Discord.getPlugin().getConfig().getString("status-channel");

        Checks.notNull(statusId, "Status channel ID");

        status = Discord.getJda().getTextChannelById(statusId);

        Checks.notNull(status, "Status channel");
    }
}
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
import org.geysermc.floodgate.api.FloodgateApi;

public class DiscordStatus {
    private static final FloodgateApi floodgate = FloodgateApi.getInstance();
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
        int bedrock = floodgate.getPlayerCount();
        int java = server.getOnlinePlayers().size() - bedrock;
        int total = java + bedrock;

        String url = config.getString("server-image");

        builder
                .setTitle("Minecraft Server")
                .setThumbnail(url == null || url.isBlank() ? null : url)
                .addField("🌵 Versão", "`" + server.getMinecraftVersion() + "`", true) // Version
                .addField("", "", true)
                .addField("👥 Online (" + (total < 10 ? "0" + total : total) + ")", "`" + String.format("""
                        Bedrock: `%s`
                        Java: `%s`
                        """,
                        bedrock < 10 ? "0" + bedrock : bedrock,
                        java <  10 ? "0" + java : java
                ) + "`", true) // Online players
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
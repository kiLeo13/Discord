package ofc.discord.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.internal.utils.Checks;
import ofc.discord.DiscordCraft;
import ofc.discord.minecraft.ServerStatus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Discord {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static TextChannel BROADCAST_CHANNEL;
    public static TextChannel STATUS_CHANNEL;

    public static void broadcast(String subject, String icon, String description, Color color) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setAuthor(subject, null, icon)
                .setDescription(description)
                .setColor(color);

        BROADCAST_CHANNEL.sendMessageEmbeds(builder.build()).queue(null, e -> {
            System.err.println("Could not broadcast event because " + e.getMessage());
            e.printStackTrace();
        });
    }

    public static void asyncUpdateStatus(ServerStatus status) {
        scheduler.schedule(() -> updateStatus(status), 100, TimeUnit.MILLISECONDS);
    }

    public static void updateStatus(ServerStatus status) {

        Server server = Bukkit.getServer();
        FileConfiguration config = DiscordCraft.getPlugin().getConfig();
        String supportedVersions = config.getString("server-data.versions");
        String image = config.getString("status-banner");
        String ipJava = config.getString("server-data.ip.java");
        String ipBedrock = config.getString("server-data.ip.bedrock");
        String icon = config.getString("server-icon");
        int online = server.getOnlinePlayers().size();

        if (status == ServerStatus.OFFLINE)
            serverDisable();

        pushNewStatus(
                ipJava == null || ipJava.isBlank() ? null : ipJava,
                ipBedrock == null || ipBedrock.isBlank() ? null : ipBedrock,
                online,
                server.getOnlineMode(),
                status,
                icon == null || icon.isBlank() ? null : icon,
                image == null || image.isBlank() ? null : image,
                server.getMinecraftVersion(),
                supportedVersions == null || supportedVersions.isBlank() ? null : supportedVersions
        );
    }

    private static void serverDisable() {
        int online = Bukkit.getOnlinePlayers().size();
        String message = String.format("Desconectados `%s` %s",
                online < 10 ? "0" + online : online,
                online == 1 ? "jogador" : "jogadores");
        broadcast("🛑 Servidor desligado", null, online == 0 ? null : message, Color.ORANGE);
    }

    public static void serverEnable() {
        broadcast("✅ Servidor aberto", null, null, Color.ORANGE);
    }

    /**
     * Sends a new embed with up to date information about the Minecraft Server to Discord.
     *
     * @param ipJava The IP used to connect to the server.
     * @param ipBedrock The IP used to connect through a Proxy like Geyser for bedrock users.
     * @param connected The amount of players connected to the server.
     * @param online Whether the server is online mode (authenticates players) or not (unsafe).
     * @param status The {@link ServerStatus} of the server.
     * @param icon The Minecraft server icon.
     * @param banner The image to be shown in the embed.
     * @param version The Minecraft version that runs the server.
     * @param supported All the possible Minecraft versions (Servers with Viaversion/Via Backwards).
     */
    private static void pushNewStatus(String ipJava, String ipBedrock, int connected, boolean online, ServerStatus status, String icon, String banner, String version, String supported) {
        final EmbedBuilder builder = new EmbedBuilder();
        Message message = fetchStatusMessage();

        Guild guild = BROADCAST_CHANNEL.getGuild();
        String players = String.format("Online: `%s`", connected < 10 ? "0" + connected : connected);
        String ips = String.format("Bedrock: `%s`\nJava: `%s`", ipBedrock == null ? "Indisponível" : ipBedrock, ipJava == null ? "Indisponível" : ipJava);

        builder
                .setTitle("Servidor de Minecraft")
                .setDescription("Informações em tempo real sobre o servidor de Minecraft.")
                .setColor(status.color())
                .addField("👥 Jogadores", status == ServerStatus.OFFLINE ? "Online: `00`" : players, true)
                .addField("🛡 Auth", online ? "`Sim`" : "`Não`", true)
                .addField("📡 Status", "`" + status.message() + "`", true)
                .addField("💻 Versão `" + version + "`", "Suporta: `" + (supported == null ? version : supported) + "`", true)
                .addField("🌐 IP", ips, false)
                .setThumbnail(icon)
                .setImage(banner)
                .setFooter(guild.getName(), guild.getIconUrl());

        // If the status message does not exist or was not found
        if (message == null) {
            Discord.STATUS_CHANNEL.sendMessageEmbeds(builder.build()).queue(m -> {
                DiscordCraft.getPlugin().getConfig().set("status-message", m.getId());
                DiscordCraft.getPlugin().saveConfig();
            }, Throwable::printStackTrace);
        }

        // If the status message already exists and was found which is usally the expected behavior
        else
            message.editMessageEmbeds(builder.build()).queue();
    }

    /**
     * @return The {@link Message} to have the content edited to the new server status
     * or null if none was found or does not exist.
     */
    private static Message fetchStatusMessage() {
        String statusMessage = DiscordCraft.getPlugin().getConfig().getString("status-message");

        return statusMessage == null || statusMessage.isBlank()
                ? null
                : STATUS_CHANNEL.retrieveMessageById(statusMessage).complete();
    }

    public static void refreshData() {
        FileConfiguration config = DiscordCraft.getPlugin().getConfig();
        String statusId = config.getString("status-channel");
        String broadcastId = config.getString("broadcast-channel");

        Checks.notNull(broadcastId, "Broadcast channel ID");
        Checks.notNull(statusId, "Status channel ID");

        STATUS_CHANNEL = DiscordCraft.getJda().getTextChannelById(statusId);
        BROADCAST_CHANNEL = DiscordCraft.getJda().getTextChannelById(broadcastId);

        Checks.notNull(BROADCAST_CHANNEL, "Broadcast channel");
        Checks.notNull(STATUS_CHANNEL, "Status channel");
    }
}
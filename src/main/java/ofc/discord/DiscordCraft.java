package ofc.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ofc.discord.discord.SlashCommandHandler;
import ofc.discord.discord.minecraft.commands.*;
import ofc.discord.minecraft.commands.General;
import ofc.discord.minecraft.ServerStatus;
import ofc.discord.minecraft.listeners.*;
import ofc.discord.discord.Discord;
import ofc.discord.discord.listeners.DiscordMessageReceived;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DiscordCraft extends JavaPlugin {
    private static final ExecutorService threadJDA = Executors.newSingleThreadExecutor();
    private static JDA jda;
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        // Registering plugin stuff
        registerListeners();
        registerCommands();

        // Start the bot on another thread to avoid stalling the server
        threadJDA.execute(this::runJDA);
    }

    @Override
    public void onDisable() {
        Discord.updateStatus(ServerStatus.OFFLINE);

        // Shut down JDA (Discord bot)
        if (jda != null) {
            Bukkit.getLogger().info("Shutting down Discord bot...");
            try {
                jda.shutdown();
                jda.awaitShutdown(15, TimeUnit.SECONDS);

                Bukkit.getLogger().info("Discord bot has been successfully shut down");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getLogger().info("Shutting down JDA executor...");
        threadJDA.shutdown();
        Bukkit.getLogger().info("JDA executor has been successfully shut down");
        Bukkit.getLogger().info("All done! Bye bye ;)");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static JDA getJda() {
        return jda;
    }

    private void runJDA() {
        String token = this.getConfig().getString("token");

        if (token == null || token.isBlank()) {
            System.err.println("Token not found, exiting...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("Starting Discord bot...");

        try {
            jda = JDABuilder.createDefault(token,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing("Minecraft"))
                    .setEventManager(new AnnotatedEventManager())
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .build()
                    .awaitReady();

            registerJDAListeners();

            this.getLogger().info("Discord bot has been successfully started.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Failed to login, exiting...");
        }

        SlashCommandHandler.addCommands(
                new Ban("ban"),
                new Kick("kick"),
                new Health("health"),
                new Teleport("teleport"),
                new Tell("tell"),
                new Unban("unban"),
                new Whitelist("whitelist"),
                new PlayerTeleport("pteleport"),
                new Ping("ping")
        );
        Discord.refreshData();
        Discord.updateStatus(ServerStatus.ONLINE);
        Discord.serverEnable();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerSendMessage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerAdvancementDone(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRunCommand(), this);
    }

    private void registerCommands() {
        PluginCommand general = this.getCommand("discord");
        if (general != null) general.setExecutor(new General());
    }

    private void registerJDAListeners() {
        jda.addEventListener(
                new DiscordMessageReceived(),
                new SlashCommandHandler()
        );
        this.getLogger().info("Successfully registered " + jda.getRegisteredListeners().size() + " listeners.");
    }
}

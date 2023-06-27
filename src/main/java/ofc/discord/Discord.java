package ofc.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ofc.discord.discord.listeners.*;
import ofc.discord.minecraft.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Discord extends JavaPlugin {
    private static final ExecutorService threadJDA = Executors.newSingleThreadExecutor();
    private static JDA jda;
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        registerListeners();

        // Start the bot on another thread to avoid hogging the server
        threadJDA.execute(this::runJDA);
    }

    @Override
    public void onDisable() {

        // Shut down JDA (Discord bot)
        Bukkit.getLogger().info("\\033[0;33mShutting down Discord bot...");
        try {
            jda.shutdownNow();
            jda.awaitShutdown();

            Bukkit.getLogger().info("Discord bot has been successfully shut down");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().info("\\033[0;33mShutting down JDA executor...");
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
            Bukkit.getPluginManager().disablePlugin(this);
            System.err.println("Token not found, exiting...");
            return;
        }

        this.getLogger().info("\\033[0;33mStarting Discord bot...");

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

            this.getLogger().info("\\033[0;32mDiscord bot has been successfully started.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Failed to login, exiting...");
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerSendMessage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerAdvancementDone(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRunCommand(), this);
    }

    private void registerJDAListeners() {
        jda.addEventListener(new MessageReceived());
        this.getLogger().info("Successfully registered " + jda.getRegisteredListeners().size() + " listeners.");
    }
}

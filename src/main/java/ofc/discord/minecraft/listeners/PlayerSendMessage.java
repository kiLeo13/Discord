package ofc.discord.minecraft.listeners;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ofc.discord.DiscordCraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerSendMessage implements Listener {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final HashMap<String, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onMessageSend(AsyncChatEvent event) {

        Component message = event.message();
        Player player = event.getPlayer();
        String content = MiniMessage.miniMessage().serialize(message);

        // Some plugins will make this event to be triggered twice
        // So I am setting a cooldown of 50ms between messages
        if (!cooldown(player)) return;

        // Send it on another thread
        executor.execute(() -> sendWebhook(player, content));
    }

    private void sendWebhook(Player player, String message) {
        String webhookURL = DiscordCraft.getPlugin().getConfig().getString("broadcast-webhook");

        if (webhookURL == null || webhookURL.isBlank())
            throw new IllegalArgumentException("Broadcast webhook cannot be null or empty");

        try (WebhookClient webhook = WebhookClient.withUrl(webhookURL)) {
            final WebhookMessageBuilder builder = new WebhookMessageBuilder();

            builder
                    .setAvatarUrl("https://cravatar.eu/helmavatar/" + player.getName() + "/64.png")
                    .setUsername(player.getName())
                    .setContent(message);

            // Set the exception handling method
            webhook.setErrorHandler((client, msg, throwable) -> {
                System.err.println("Could not broadcast player message to Discord");

                if (throwable != null)
                    throwable.printStackTrace();
            });

            webhook.send(builder.build());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean cooldown(Player player) {
        String uuid = player.getUniqueId().toString();
        long now = System.currentTimeMillis();

        if (!cooldown.containsKey(uuid) || now - cooldown.get(uuid) > 50) {
            cooldown.put(uuid, now);
            return true;
        }

        return false;
    }
}
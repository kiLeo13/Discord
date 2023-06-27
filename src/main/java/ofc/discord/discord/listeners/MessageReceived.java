package ofc.discord.discord.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ofc.discord.Discord;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String channelId = Discord.getPlugin().getConfig().getString("broadcast-channel");
        Message message = event.getMessage();
        Message referencedMessage = message.getReferencedMessage();
        TextChannel channel = message.getChannel().asTextChannel();
        User author = message.getAuthor();

        if (!channel.getId().equals(channelId) || author.isBot()) return;

        minecraftSendMessage(message, referencedMessage);
    }

    private void minecraftSendMessage(Message message, Message reference) {
        final StringBuilder builder = new StringBuilder();

        // The message template
        String MESSAGE = format(message, Discord.getPlugin().getConfig().getString("discord-format"));

        if (reference != null
                && !reference.getAuthor().isBot()
                && reference.getEmbeds().isEmpty()
                && !reference.getContentStripped().isBlank()
        ) {

            // The referenced message template (if any)
            String REFERENCE = format(reference, Discord.getPlugin().getConfig().getString("discord-with-reply"));

            builder.append(REFERENCE);
        }

        builder.append(MESSAGE);
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(builder.toString().stripTrailing() + "<reset>"));
    }

    private String format(Message message, String str) {
        if (str == null)
            return null;

        String global = message.getAuthor().getGlobalName();
        String prefix = Discord.getPlugin().getConfig().getString("discord-prefix");

        return str
                .replace("{pf}", prefix == null ? "<dark_blue>[<blue>Discord<dark_blue>]<reset>" : prefix)
                .replace("{username}", global == null ? "Unknown" : global)
                .replace("{user}", message.getAuthor().getName())
                .replace("{message}", message.getContentStripped());
    }
}
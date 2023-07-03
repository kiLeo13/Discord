package ofc.discord.discord.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ofc.discord.DiscordCraft;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DiscordMessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String channelId = DiscordCraft.getPlugin().getConfig().getString("broadcast-channel");
        Message message = event.getMessage();
        Message referencedMessage = message.getReferencedMessage();
        MessageChannelUnion channel = message.getChannel();
        User author = message.getAuthor();

        if (!channel.getId().equals(channelId) || author.isBot()) return;

        // The message may be empty if only a file has been sent,
        // so we are going to ignore and delete it
        if (message.getContentStripped().isBlank()) {
            message.delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            return;
        }

        minecraftSendMessage(message, referencedMessage);
    }

    private void minecraftSendMessage(Message message, Message reference) {
        final StringBuilder builder = new StringBuilder();

        // The message template
        String MESSAGE = format(message, DiscordCraft.getPlugin().getConfig().getString("discord-format"));

        if (reference != null
                && !reference.getAuthor().isBot()
                && reference.getEmbeds().isEmpty()
                && !reference.getContentStripped().isBlank()
        ) {

            // The referenced message template (if any)
            String REFERENCE = format(reference, DiscordCraft.getPlugin().getConfig().getString("discord-with-reply"));

            builder.append(REFERENCE)
                    .append("\n");
        }

        builder.append(MESSAGE);
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(builder.toString().stripTrailing() + "<reset>"));
    }

    private String format(Message message, String format) {
        if (format == null)
            return null;

        String global = message.getAuthor().getEffectiveName();
        String prefix = DiscordCraft.getPlugin().getConfig().getString("discord-prefix");

        return format
                .replace("{pf}", prefix == null ? "<blue>[Discord]<reset>" : prefix)
                .replace("{username}", global)
                .replace("{message}", message.getContentStripped());
    }
}
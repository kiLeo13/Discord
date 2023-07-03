package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ofc.discord.internal.SlashMinecraftCommand;
import ofc.discord.minecraft.Minecraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Kick extends SlashMinecraftCommand {

    public Kick(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {
        OptionMapping OPTION_reason = cmd.getOption("reason");

        String playerInput = cmd.getOption("player").getAsString();
        String reason = OPTION_reason == null ? null : OPTION_reason.getAsString();

        Player player = Bukkit.getPlayer(playerInput);

        if (player == null) {
            cmd.reply("Jogador não encontrado.").queue();
            return;
        }

        if (reason == null || reason.isBlank())
            Minecraft.sync(player::kick);
        else
            Minecraft.sync(() -> player.kick(MiniMessage.miniMessage().deserialize(reason.replace("\\n", "\n"))));

        cmd.reply(
                reason == null || reason.isBlank()
                ? String.format("Jogador `%s` foi expulso do servidor.", player.getName())
                : String.format("Jogador `%s` foi expulso do servidor por `%s`.", player.getName(), MiniMessage.miniMessage().stripTags(reason))
        ).setEphemeral(true).queue();
    }
}
package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import ofc.discord.minecraft.Minecraft;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ban extends SlashMinecraftCommand {

    public Ban(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {
        OptionMapping OPTION_reason = cmd.getOption("reason");

        String playerInput = cmd.getOption("player").getAsString();
        String reason = OPTION_reason == null ? null : OPTION_reason.getAsString();
        Player player = Bukkit.getPlayer(playerInput);

        if (player == null) {
            cmd.reply("Jogador não encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (player.isBanned()) {
            cmd.reply("O jogador informado já está banido.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Minecraft.sync(() -> {
            BanEntry ban = player.banPlayer(reason);
            cmd.reply(String.format("Jogador `%s` foi baindo por `%s`", player.getName(), ban.getReason() == null ? "Nenhum motivo fornecido" : ban.getReason()))
                    .setEphemeral(true)
                    .queue();
        });
    }
}
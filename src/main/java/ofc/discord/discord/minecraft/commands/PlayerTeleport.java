package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerTeleport extends SlashMinecraftCommand {

    public PlayerTeleport(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        String from = cmd.getOption("playerf").getAsString();
        String to = cmd.getOption("playert").getAsString();

        Player playerFrom = Bukkit.getPlayer(from);
        Player playerTo = Bukkit.getPlayer(to);

        if (playerFrom == null || playerTo == null) {
            cmd.reply("Jogador `" + (playerFrom == null ? from : to) + "` não encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        playerFrom.teleportAsync(playerTo.getLocation());
        cmd.reply(String.format("Jogador `%s` teletransportado até `%s`.", playerFrom.getName(), playerTo.getName()))
                .setEphemeral(true)
                .queue();
    }
}
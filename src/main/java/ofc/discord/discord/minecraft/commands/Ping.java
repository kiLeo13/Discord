package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ping extends SlashMinecraftCommand {

    public Ping(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        String playerInput = cmd.getOption("player").getAsString();
        Player player = Bukkit.getPlayer(playerInput);

        if (player == null) {
            cmd.reply("Jogador não encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int ping = player.getPing();
        cmd.reply(String.format("Ping de `%s`: `%dms`", player.getName(), ping))
                .setEphemeral(true)
                .queue();
    }
}
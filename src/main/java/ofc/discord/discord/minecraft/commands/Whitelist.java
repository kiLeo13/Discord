package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class Whitelist extends SlashMinecraftCommand {

    public Whitelist(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {
        OptionMapping OPTION_player = cmd.getOption("player");

        String method = cmd.getOption("method").getAsString();
        String playerInput = OPTION_player == null ? null : OPTION_player.getAsString();

        switch (method.toLowerCase()) {

            // If it's meant to add someone to the whitelist
            case "add" -> {
                OfflinePlayer player = retrievePlayer(playerInput);

                if (player == null) {
                    cmd.reply("Nenhum jogador foi fornecido ou nenhum foi encontrado.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                if (player.isWhitelisted()) {
                    cmd.reply("O jogador já está na whitelist.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                player.setWhitelisted(true);
                cmd.reply("Jogador `" + player.getName() + "` foi adicionado com sucesso na whitelist.")
                        .setEphemeral(true)
                        .queue();
            }

            case "remove" -> {
                OfflinePlayer player = retrievePlayer(playerInput);

                if (player == null) {
                    cmd.reply("Nenhum jogador foi fornecido ou nenhum foi encontrado.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                if (!player.isWhitelisted()) {
                    cmd.reply("O jogador já não está na whitelist.")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                player.setWhitelisted(false);
                cmd.reply("Jogador `" + player.getName() + "` foi removido com sucesso da whitelist.")
                        .setEphemeral(true)
                        .queue();
            }

            case "list" -> {
                final List<OfflinePlayer> players = Bukkit.getWhitelistedPlayers().stream().toList();
                final InputStream whitelistedStream = new ByteArrayInputStream(getWhitelisted(players).getBytes());
                final FileUpload upload = FileUpload.fromData(whitelistedStream, "whitelisted.yml");

                if (players.isEmpty()) {
                    cmd.reply("Nenhum jogador presente na whitelist.").queue();
                    return;
                }

                MessageCreateData send = new MessageCreateBuilder()
                        .setFiles(upload)
                        .setContent("Aqui estão todos os `" + players.size() + "` membros presentes na whitelist!")
                        .build();

                cmd.reply(send).queue();
            }
        }
    }

    private OfflinePlayer retrievePlayer(String arg) {
        if (arg == null || arg.isBlank())
            return null;

        return Bukkit.getOfflinePlayer(arg);
    }

    private String getWhitelisted(List<OfflinePlayer> whitelisted) {
        final StringBuilder builder = new StringBuilder();

        for (OfflinePlayer op : whitelisted) {
            String name = op.getName();

            builder.append(op.getUniqueId())
                    .append(": ")
                    .append(name == null ? "Unavailable" : name)
                    .append("\n");
        }

        return builder.toString().stripTrailing();
    }
}